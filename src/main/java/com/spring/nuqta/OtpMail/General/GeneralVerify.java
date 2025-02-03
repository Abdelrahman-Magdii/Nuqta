package com.spring.nuqta.OtpMail.General;

import com.spring.nuqta.OtpMail.Entity.OtpVerifyEntity;
import com.spring.nuqta.OtpMail.Repo.OtpVerifyRepo;
import com.spring.nuqta.OtpMail.Services.OtpVerifyService;
import com.spring.nuqta.exception.GlobalException;
import com.spring.nuqta.mail.Services.EmailService;
import com.spring.nuqta.mail.template.AccountVerificationEmailWithOtp;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.organization.Repo.OrgRepo;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import com.spring.nuqta.usermanagement.Repo.UserRepo;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Component
public class GeneralVerify {

    private final OtpVerifyRepo otpVerifyRepo;
    private final EmailService emailService;
    private final OtpVerifyService otpVerifyService;
    private final UserRepo userRepository;
    private final OrgRepo organizationRepository;

    @Autowired
    public GeneralVerify(OtpVerifyRepo otpVerifyRepo, EmailService emailService, OtpVerifyService otpVerifyService, UserRepo userRepository, OrgRepo organizationRepository) {
        this.otpVerifyRepo = otpVerifyRepo;
        this.emailService = emailService;
        this.otpVerifyService = otpVerifyService;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
    }

    public <T> void sendOtpEmail(T entity) {
        OtpVerifyEntity otpVerifyEntity = new OtpVerifyEntity();
        String otp = otpVerifyService.generateOtp();

        otpVerifyEntity.setOtp(otp);
        otpVerifyEntity.setExpiredAt(LocalDateTime.now().plusSeconds(1));

        if (entity instanceof UserEntity) {
            otpVerifyEntity.setUser((UserEntity) entity);
        } else if (entity instanceof OrgEntity) {
            otpVerifyEntity.setOrganization((OrgEntity) entity);
        } else {
            throw new IllegalArgumentException("Unsupported entity type");
        }

        otpVerifyRepo.save(otpVerifyEntity);

        AccountVerificationEmailWithOtp context = new AccountVerificationEmailWithOtp();
        context.init(entity);
        context.buildVerificationOtp(otp);

        try {
            emailService.sendMail(context);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public boolean verifyEmail(String mail, String otp) {

        Optional<OtpVerifyEntity> tokenOpt = Optional.ofNullable(otpVerifyRepo.findByOtp(otp));

        if (tokenOpt.isPresent()) {
            OtpVerifyEntity verify = tokenOpt.get();
            log.info("Verifying OTP email: {}", verify.getOtp());
            if (verify.isExpired()) {
                throw new GlobalException("Verification code expired. Please request a new code.", HttpStatus.BAD_REQUEST);
            }

            if (verify.getUser() != null) {
                Optional<UserEntity> userOpt = userRepository.findById(verify.getUser().getId());
                Optional<UserEntity> entity = userRepository.findByEmail(mail);

                if (userOpt.isPresent() && entity.isPresent()) {
                    UserEntity user = userOpt.get();
                    user.setEnabled(true);
                    userRepository.save(user); // Save user (modify if needed)
                    otpVerifyService.deleteOtp(verify);
                    return true;
                }
            } else if (verify.getOrganization() != null) {
                Optional<OrgEntity> orgOpt = organizationRepository.findById(verify.getOrganization().getId());
                Optional<OrgEntity> entity = organizationRepository.findByEmail(mail);

                if (orgOpt.isPresent() && entity.isPresent()) {
                    OrgEntity org = orgOpt.get();
                    org.setEnabled(true);
                    organizationRepository.save(org); // Save organization (modify if needed)
                    otpVerifyService.deleteOtp(verify);
                    return true;
                }
            }

        }
        return false; // Token not found or user does not exist
    }
    
    public String resendOtp(String email) {
        // Try to find the user or organization by email
        Optional<UserEntity> userOpt = userRepository.findByEmail(email);
        Optional<OrgEntity> orgOpt = organizationRepository.findByEmail(email);

        // If neither user nor organization exists, return error message
        if (userOpt.isEmpty() && orgOpt.isEmpty()) {
            return "Email not found. Please check the email address and try again.";
        }

        // Log safely without calling .get() on an empty Optional
        userOpt.ifPresent(user -> log.info("Verifying OTP for User email: {}, Enabled: {}", user.getEmail(), user.isEnabled()));
        orgOpt.ifPresent(org -> log.info("Verifying OTP for Org email: {}, Enabled: {}", org.getEmail(), org.isEnabled()));

        // If the user or organization is already verified, return message
        if ((userOpt.isPresent() && userOpt.get().isEnabled()) ||
                (orgOpt.isPresent() && orgOpt.get().isEnabled())) {
            return "Email already verified.";
        }

        // Retrieve or create an OTP entity
        OtpVerifyEntity otpVerifyEntity = null;
        if (orgOpt.isPresent()) {
            otpVerifyEntity = otpVerifyRepo.findByOrganization(orgOpt.get());
        } else if (userOpt.isPresent()) {
            otpVerifyEntity = otpVerifyRepo.findByUser(userOpt.get());
        }

        // If no existing OTP entity found, create a new one
        if (otpVerifyEntity == null) {
            otpVerifyEntity = new OtpVerifyEntity();
            if (orgOpt.isPresent()) {
                otpVerifyEntity.setOrganization(orgOpt.get());
            } else {
                otpVerifyEntity.setUser(userOpt.get());
            }
        }

        // Generate a new OTP
        String newOtp = otpVerifyService.generateOtp();
        otpVerifyEntity.setOtp(newOtp);
        otpVerifyEntity.setExpiredAt(LocalDateTime.now().plusMinutes(5)); // OTP valid for 5 minutes

        // Save the new OTP to the database
        otpVerifyRepo.save(otpVerifyEntity);

        // Prepare the email content with the new OTP
        AccountVerificationEmailWithOtp context = new AccountVerificationEmailWithOtp();
        if (userOpt.isPresent()) {
            context.init(userOpt.get()); // Initialize email context with UserEntity
        } else {
            context.init(orgOpt.get()); // Initialize email context with OrgEntity
        }
        context.buildVerificationOtp(newOtp); // Build OTP-specific email content

        // Try to send the email with the OTP
        try {
            emailService.sendMail(context); // Send email with new OTP
            return "A new OTP has been sent to your email.";
        } catch (MessagingException e) {
            log.error("Error sending OTP email to {}: {}", email, e.getMessage());
            return "Error sending OTP email. Please try again later.";
        }
    }

}
