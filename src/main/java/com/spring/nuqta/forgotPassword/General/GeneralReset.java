package com.spring.nuqta.forgotPassword.General;

import com.spring.nuqta.exception.GlobalException;
import com.spring.nuqta.forgotPassword.Entity.ResetPasswordEntity;
import com.spring.nuqta.forgotPassword.Repo.ResetPasswordRepo;
import com.spring.nuqta.forgotPassword.Services.ResetPasswordService;
import com.spring.nuqta.mail.Services.EmailService;
import com.spring.nuqta.mail.template.ForgotPasswordWithOtp;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.organization.Repo.OrgRepo;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import com.spring.nuqta.usermanagement.Repo.UserRepo;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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

    @Autowired
    public GeneralReset(EmailService emailService, ResetPasswordService otpVerifyService, UserRepo userRepo, OrgRepo organizationRepo, PasswordEncoder passwordEncoder, ResetPasswordRepo resetPasswordRepo) {
        this.emailService = emailService;
        this.resetPasswordService = otpVerifyService;
        this.userRepo = userRepo;
        this.organizationRepo = organizationRepo;
        this.passwordEncoder = passwordEncoder;
        this.resetPasswordRepo = resetPasswordRepo;
    }

    public String sendOtpEmail(String email) {

        // Find user or organization by email
        Optional<UserEntity> user = userRepo.findByEmail(email);
        Optional<OrgEntity> organization = organizationRepo.findByEmail(email);

        if (user.isEmpty() && organization.isEmpty()) {
            return "Email not found. Please check the email address and try again.";
        }


        if ((user.isPresent() && user.get().isEnabled()) || (organization.isPresent() && organization.get().isEnabled())) {

            // Retrieve or create an OTP entity
            ResetPasswordEntity resetPasswordEntity = null;
            if (organization.isPresent()) {
                resetPasswordEntity = resetPasswordRepo.findByOrganization(organization.get());
            } else {
                resetPasswordEntity = resetPasswordRepo.findByUser(user.get());
            }

            // If no existing OTP entity found, create a new one
            if (resetPasswordEntity == null) {
                resetPasswordEntity = new ResetPasswordEntity();
                if (organization.isPresent()) {
                    resetPasswordEntity.setOrganization(organization.get());
                } else {
                    resetPasswordEntity.setUser(user.get());
                }
            }

            // Generate OTP and store it
            String otp = resetPasswordService.generateOtp();
            resetPasswordEntity.setOtp(otp);
            resetPasswordEntity.setExpiredAt(LocalDateTime.now().plusMinutes(5)); // Reasonable expiry time

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
                return "Success sent OTP to your email.";
            } catch (MessagingException e) {
                return "Error sending OTP email. Please try again later.";
            }
        } else {
            return "Email not verify. Please complete sign in.";
        }


    }


    public boolean resetPassword(String mail, String otp, String newPassword) {

        Optional<ResetPasswordEntity> tokenOpt = Optional.ofNullable(resetPasswordRepo.findByOtp(otp));

        if (tokenOpt.isPresent()) {
            ResetPasswordEntity verify = tokenOpt.get();

            if (verify.isExpired()) {
                throw new GlobalException("Verification code expired. Please request a new code.", HttpStatus.BAD_REQUEST);
            }

            if (verify.getUser() != null) {
                Optional<UserEntity> userOpt = userRepo.findById(verify.getUser().getId());
                Optional<UserEntity> entity = userRepo.findByEmail(mail);

                if (userOpt.isPresent() && entity.isPresent()) {
                    UserEntity user = userOpt.get();
                    user.setPassword(passwordEncoder.encode(newPassword));
                    userRepo.save(user); // Save user (modify if needed)
                    resetPasswordService.deleteOtp(verify);
                    return true;
                }
            } else if (verify.getOrganization() != null) {
                Optional<OrgEntity> orgOpt = organizationRepo.findById(verify.getOrganization().getId());
                Optional<OrgEntity> entity = organizationRepo.findByEmail(mail);

                if (orgOpt.isPresent() && entity.isPresent()) {
                    OrgEntity org = orgOpt.get();
                    org.setPassword(passwordEncoder.encode(newPassword));
                    organizationRepo.save(org); // Save organization (modify if needed)
                    resetPasswordService.deleteOtp(verify);
                    return true;
                }
            }

        }
        return false; // Token not found or user does not exist
    }

}
