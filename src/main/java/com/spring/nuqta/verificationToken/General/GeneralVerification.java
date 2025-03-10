package com.spring.nuqta.verificationToken.General;

import com.spring.nuqta.exception.GlobalException;
import com.spring.nuqta.mail.Services.EmailService;
import com.spring.nuqta.mail.template.AccountVerificationEmailContext;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.organization.Repo.OrgRepo;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import com.spring.nuqta.usermanagement.Repo.UserRepo;
import com.spring.nuqta.verificationToken.Entity.VerificationToken;
import com.spring.nuqta.verificationToken.Services.VerificationTokenService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeneralVerification {

    private final EmailService emailService;
    private final VerificationTokenService verificationTokenService;
    private final OrgRepo orgRepo;
    private final UserRepo userRepo;

    @Value("${token.base.url}")
    private String baseUrl;

    /**
     * Sends OTP email to either a UserEntity or OrgEntity.
     *
     * @param entity the recipient entity (User or Org)
     */
    public <T> void sendOtpEmail(T entity) {
        VerificationToken token = verificationTokenService.createToken();

        if (entity instanceof UserEntity user) {
            token.setUser(user);
        } else if (entity instanceof OrgEntity org) {
            token.setOrganization(org);
        }

        verificationTokenService.saveToken(token);

        AccountVerificationEmailContext context = new AccountVerificationEmailContext();
        context.init(entity);
        String email = (entity instanceof UserEntity user) ? user.getEmail() : ((OrgEntity) entity).getEmail();
        context.buildVerificationUrl(baseUrl, token.getToken(), email);

        try {
            emailService.sendMail(context);
            log.info("OTP email sent to {}", email);
        } catch (MessagingException e) {
            log.error("Failed to send OTP email to {}: {}", email, e.getMessage(), e);
        }
    }

    /**
     * Verifies user or organization registration using a token.
     *
     * @param tokenValue the verification token
     * @param email      the email associated with the entity
     * @return true if verification succeeds, false otherwise
     */
    public boolean verifyRegistration(String tokenValue, String email) {
        VerificationToken token = verificationTokenService.findByToken(tokenValue);

        if (token == null) {
            log.warn("Invalid verification token: {}", tokenValue);
            return false;
        }

        if (token.getExpiredAt() != null && token.getExpiredAt().isBefore(LocalDateTime.now())) {
            log.warn("Expired verification token: {}", tokenValue);
            throw new GlobalException("error.token", HttpStatus.BAD_REQUEST);
        }

        if (token.getUser() != null) {
            return verifyUser(token, email);
        } else if (token.getOrganization() != null) {
            return verifyOrganization(token, email);
        }

        log.error("Verification failed: No associated entity for token {}", tokenValue);
        return false;
    }

    private boolean verifyUser(VerificationToken token, String email) {
        UserEntity user = token.getUser();

        Optional<UserEntity> existingUser = userRepo.findByEmail(email);
        if (existingUser.isEmpty() || !existingUser.get().getId().equals(user.getId())) {
            log.warn("Verification failed: User not found or mismatch for email {}", email);
            return false;
        }

        user.setEnabled(true);
        userRepo.save(user);
        verificationTokenService.removeToken(token);
        log.info("User {} successfully verified", email);
        return true;
    }

    private boolean verifyOrganization(VerificationToken token, String email) {
        OrgEntity org = token.getOrganization();

        Optional<OrgEntity> existingOrg = orgRepo.findByEmail(email);
        if (existingOrg.isEmpty() || !existingOrg.get().getId().equals(org.getId())) {
            log.warn("Verification failed: Organization not found or mismatch for email {}", email);
            return false;
        }

        org.setEnabled(true);
        orgRepo.save(org);
        verificationTokenService.removeToken(token);
        log.info("Organization {} successfully verified", email);
        return true;
    }
}
