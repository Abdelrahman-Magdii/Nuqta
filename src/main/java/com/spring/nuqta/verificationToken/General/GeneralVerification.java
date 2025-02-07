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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component
public class GeneralVerification {

    private final EmailService emailService;
    private final VerificationTokenService verificationTokenService;
    private final OrgRepo organizationRepository;
    private final UserRepo userRepository;


    @Value("${site.base.url.http}")
    private String baseUrl;


    @Autowired
    public GeneralVerification(EmailService emailService, VerificationTokenService verificationTokenService, OrgRepo organizationRepository, UserRepo userRepository) {
        this.organizationRepository = organizationRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.verificationTokenService = verificationTokenService;
    }

    public <T> void sendOtpEmail(T entity) {
        VerificationToken token = verificationTokenService.createToken();

        token.setToken(token.getToken());

        if (entity instanceof UserEntity) {
            token.setUser((UserEntity) entity);
        } else if (entity instanceof OrgEntity) {
            token.setOrganization((OrgEntity) entity);
        } else {
            throw new IllegalArgumentException("Unsupported entity type");
        }
        verificationTokenService.saveToken(token);


        AccountVerificationEmailContext context = new AccountVerificationEmailContext();
        context.init(entity);
        String email = (entity instanceof UserEntity user) ? user.getEmail() : ((OrgEntity) entity).getEmail();
        context.buildVerificationUrl(baseUrl, token.getToken(), email);


        try {
            emailService.sendMail(context);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public boolean verifyRegistration(String token, String mail) {
        Optional<VerificationToken> tokenOpt = Optional.ofNullable(verificationTokenService.findByToken(token));

        if (tokenOpt.isPresent()) {
            VerificationToken verificationToken = tokenOpt.get();

            if (verificationToken.isExpired()) {
                throw new GlobalException("Expired verification token.", HttpStatus.BAD_REQUEST);
            }

            if (verificationToken.getUser() != null) {
                Optional<UserEntity> userOpt = userRepository.findById(verificationToken.getUser().getId());
                Optional<UserEntity> entity = userRepository.findByEmail(mail);
                verificationToken.getUser().getEmail();
                if (userOpt.isPresent() && entity.isPresent()) {
                    UserEntity user = userOpt.get();
                    user.setEnabled(true);
                    userRepository.save(user); // Save user (modify if needed)
                    verificationTokenService.removeToken(verificationToken);
                    return true;
                }
            } else if (verificationToken.getOrganization() != null) {
                Optional<OrgEntity> orgOpt = organizationRepository.findById(verificationToken.getOrganization().getId());
                Optional<OrgEntity> entity = organizationRepository.findByEmail(mail);
                if (orgOpt.isPresent() && entity.isPresent()) {
                    OrgEntity org = orgOpt.get();
                    org.setEnabled(true);
                    organizationRepository.save(org); // Save organization (modify if needed)
                    verificationTokenService.removeToken(verificationToken);
                    return true;
                }
            }

        }
        return false; // Token not found or user does not exist
    }

}
