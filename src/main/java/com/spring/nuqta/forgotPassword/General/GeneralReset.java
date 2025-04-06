package com.spring.nuqta.forgotPassword.General;

import com.spring.nuqta.exception.GlobalException;
import com.spring.nuqta.forgotPassword.Entity.ResetPasswordEntity;
import com.spring.nuqta.forgotPassword.Repo.ResetPasswordRepo;
import com.spring.nuqta.forgotPassword.Services.ResetPasswordService;
import com.spring.nuqta.mail.Services.EmailService;
import com.spring.nuqta.mail.template.ForgotPasswordWithOtp;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.organization.Projection.OrgAuthProjection;
import com.spring.nuqta.organization.Repo.OrgRepo;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import com.spring.nuqta.usermanagement.Projection.UserAuthProjection;
import com.spring.nuqta.usermanagement.Repo.UserRepo;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

@Slf4j
@Component
public class GeneralReset {

    private final ResetPasswordRepo resetPasswordRepo;
    private final EmailService emailService;
    private final ResetPasswordService resetPasswordService;
    private final UserRepo userRepo;
    private final OrgRepo organizationRepo;
    private final PasswordEncoder passwordEncoder;
    private final MessageSource ms;

    @Autowired
    public GeneralReset(EmailService emailService, ResetPasswordService otpVerifyService, UserRepo userRepo, OrgRepo organizationRepo, PasswordEncoder passwordEncoder, ResetPasswordRepo resetPasswordRepo, MessageSource ms) {
        this.emailService = emailService;
        this.resetPasswordService = otpVerifyService;
        this.userRepo = userRepo;
        this.organizationRepo = organizationRepo;
        this.passwordEncoder = passwordEncoder;
        this.resetPasswordRepo = resetPasswordRepo;
        this.ms = ms;
    }

    public ResponseEntity<Map<String, String>> sendOtpEmail(String email) {
        Map<String, String> response = new HashMap<>();

        // Find user or organization by email
        Optional<UserAuthProjection> user = userRepo.findUserAuthProjectionByEmail(email);
        Optional<OrgAuthProjection> organization = organizationRepo.findOrgAuthProjectionByEmail(email);

        if (user.isEmpty() && organization.isEmpty()) {
            response.put("message", getMS("email.not.found"));
            return ResponseEntity.ok(response);
        }

        if ((user.isPresent() && user.get().enabled()) || (organization.isPresent() && organization.get().enabled())) {
            ResetPasswordEntity resetPasswordEntity = retrieveOrCreateOtpEntity(user, organization);

            // Generate OTP and store it
            String otp = resetPasswordService.generateOtp();
            resetPasswordEntity.setOtp(otp);
            resetPasswordEntity.setExpiredAt(LocalDateTime.now().plusMinutes(5));

            resetPasswordRepo.save(resetPasswordEntity);

            // Prepare and send OTP email
            ForgotPasswordWithOtp context = new ForgotPasswordWithOtp();
            if (user.isPresent()) {
                context.init(user.get());
            } else {
                context.init(organization.get());
            }
            context.buildVerificationOtp(otp);

            try {
                emailService.sendMail(context);
                response.put("message", getMS("otp.success"));
                return ResponseEntity.ok(response);
            } catch (MessagingException e) {
                response.put("message", getMS("otp.send.error"));
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        } else {
            log.warn("Email not verified: {}", email);
            response.put("message", getMS("email.not.verified"));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    public ResetPasswordEntity retrieveOrCreateOtpEntity(Optional<UserAuthProjection> userOpt, Optional<OrgAuthProjection> orgOpt) {
        String email = orgOpt.map(OrgAuthProjection::email)
                .or(() -> userOpt.map(UserAuthProjection::email))
                .orElseThrow(() -> new NoSuchElementException("No user or organization found for email: null"));

        ResetPasswordEntity resetPasswordEntity = Optional.ofNullable(resetPasswordRepo.findByUser_Email(email))
                .orElseGet(() -> resetPasswordRepo.findByOrganization_Email(email));

        if (resetPasswordEntity == null) {
            resetPasswordEntity = new ResetPasswordEntity();
            if (orgOpt.isPresent()) {
                OrgEntity org = organizationRepo.findByEmail(email)
                        .orElseThrow(() -> new NoSuchElementException("Organization not found for email: " + email));
                resetPasswordEntity.setOrganization(org);
            } else {
                UserEntity user = userRepo.findByEmail(email)
                        .orElseThrow(() -> new NoSuchElementException("User not found for email: " + email));
                resetPasswordEntity.setUser(user);
            }
        }
        return resetPasswordEntity;
    }

    public boolean resetPassword(String mail, String otp, String newPassword) {
        Optional<ResetPasswordEntity> tokenOpt = Optional.ofNullable(resetPasswordRepo.findByOtp(otp));

        if (tokenOpt.isPresent()) {
            ResetPasswordEntity verify = tokenOpt.get();

            if (verify.isExpired()) {
                throw new GlobalException("verification.code.expired", HttpStatus.BAD_REQUEST);
            }

            if (verify.getUser() != null) {
                Optional<UserEntity> userOpt = userRepo.findById(verify.getUser().getId());
                boolean entity = userRepo.existsByEmail(mail);

                if (userOpt.isPresent() && entity) {
                    UserEntity user = userOpt.get();
                    user.setPassword(passwordEncoder.encode(newPassword));
                    userRepo.save(user);
                    resetPasswordService.deleteOtp(verify);
                    return true;
                }
            } else if (verify.getOrganization() != null) {
                Optional<OrgEntity> orgOpt = organizationRepo.findById(verify.getOrganization().getId());
                boolean entity = organizationRepo.existsByEmail(mail);

                if (orgOpt.isPresent() && entity) {
                    OrgEntity org = orgOpt.get();
                    org.setPassword(passwordEncoder.encode(newPassword));
                    organizationRepo.save(org);
                    resetPasswordService.deleteOtp(verify);
                    return true;
                }
            }
        }
        return false;
    }

    public String getMS(String messageKey) {
        return ms.getMessage(messageKey, null, LocaleContextHolder.getLocale());
    }
}
