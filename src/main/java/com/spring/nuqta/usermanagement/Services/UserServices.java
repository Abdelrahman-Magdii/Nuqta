package com.spring.nuqta.usermanagement.Services;

import com.spring.nuqta.authentication.Entity.VerificationToken;
import com.spring.nuqta.authentication.Services.VerificationTokenService;
import com.spring.nuqta.base.Services.BaseServices;
import com.spring.nuqta.enums.Scope;
import com.spring.nuqta.exception.GlobalException;
import com.spring.nuqta.mail.Services.EmailService;
import com.spring.nuqta.mail.template.AccountVerificationEmailContext;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.organization.Repo.OrgRepo;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import com.spring.nuqta.usermanagement.Repo.UserRepo;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServices extends BaseServices<UserEntity, Long> {

    private final UserRepo userRepository;
    private final OrgRepo organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;
    @Value("${site.base.url.http}")
    private String baseUrl;

    private static void validateUserFields(UserEntity params) {
        if (Objects.isNull(params.getUsername())) {
            throw new GlobalException("Username is required.", HttpStatus.BAD_REQUEST);
        }
        if (Objects.isNull(params.getPassword())) {
            throw new GlobalException("Password is required.", HttpStatus.BAD_REQUEST);
        }
        if (Objects.isNull(params.getEmail())) {
            throw new GlobalException("Email is required.", HttpStatus.BAD_REQUEST);
        }
        if (Objects.isNull(params.getPhoneNumber())) {
            throw new GlobalException("Mobile phone number is required.", HttpStatus.BAD_REQUEST);
        }
        if (Objects.isNull(params.getScope())) {
            throw new GlobalException("Scope is required.", HttpStatus.BAD_REQUEST);
        }

        if (!(Scope.USER.equals(params.getScope()))) {
            throw new GlobalException("Invalid scope. Scope must be 'USER'.", HttpStatus.BAD_REQUEST);
        }

        if (Objects.isNull(params.getDonation().getLocation())) {
            throw new GlobalException("Location is required.", HttpStatus.BAD_REQUEST);
        }
    }

    @Override
    public List<UserEntity> findAll() throws GlobalException {
        List<UserEntity> users = super.findAll();
        if (users.isEmpty()) {
            throw new GlobalException("No users found", HttpStatus.NOT_FOUND);
        }
        return users;
    }

    @Override
    public UserEntity findById(Long id) throws GlobalException {
        UserEntity user = super.findById(id);
        if (user == null) {
            throw new GlobalException("User not found with ID: " + id, HttpStatus.NOT_FOUND);
        }
        return user;
    }

    @Override
    public UserEntity update(UserEntity entity) throws GlobalException {
        if (entity == null || entity.getId() == null) {
            throw new GlobalException("User ID cannot be null", HttpStatus.BAD_REQUEST);
        }
        UserEntity existingUser = super.findById(entity.getId());
        if (existingUser == null) {
            throw new GlobalException("User not found with ID: " + entity.getId(), HttpStatus.NOT_FOUND);
        }

        if (existingUser.getDonation().getId() == null || !existingUser.getDonation().getId().equals(entity.getDonation().getId())) {
            throw new GlobalException("Donation not found with ID: " + entity.getDonation().getId(), HttpStatus.NOT_FOUND);
        }

        existingUser.setId(entity.getId());
        existingUser.setUsername(entity.getUsername());
//        existingUser.setEmail(entity.getEmail());
//        existingUser.setPassword(passwordEncoder.encode(entity.getPassword()));
        existingUser.setGender(entity.getGender());
        existingUser.setPhoneNumber(entity.getPhoneNumber());
        existingUser.setScope(entity.getScope());
        existingUser.setDonation(entity.getDonation());

        return super.update(existingUser);
    }

    @Override
    public void deleteById(Long id) throws GlobalException {
        UserEntity user = super.findById(id);
        if (user == null) {
            throw new GlobalException("User not found with ID: " + id, HttpStatus.NOT_FOUND);
        }
        super.deleteById(id);
    }

    @Transactional
    public void saveUser(UserEntity entity) {
        validateUserFields(entity);

        Optional<UserEntity> existingUser =
                userRepository.findByUsernameOrEmail(entity.getUsername(), entity.getEmail());

        if (existingUser.isPresent()) {
            throw new GlobalException("Username and email is exist", HttpStatus.BAD_REQUEST);
        }

        UserEntity userCreation = new UserEntity();
        userCreation.setUsername(entity.getUsername());
        userCreation.setEmail(entity.getEmail());
        userCreation.setPassword(passwordEncoder.encode(entity.getPassword()));
        userCreation.setDonation(entity.getDonation());
        userCreation.setScope(entity.getScope());
        userCreation.setGender(entity.getGender());
        userCreation.setPhoneNumber(entity.getPhoneNumber());
        userCreation.setBirthDate(entity.getBirthDate());


        userCreation = userRepository.save(userCreation);

        sendVerificationEmail(userCreation);
    }

    public void sendVerificationEmail(UserEntity entity) {
        VerificationToken token = verificationTokenService.createToken();
        token.setUser(entity);

        verificationTokenService.saveToken(token);

        AccountVerificationEmailContext context = new AccountVerificationEmailContext();
        context.init(entity);
        context.setToken(token.getToken());
        context.buildVerificationUrl(baseUrl, token.getToken(), entity.getEmail());

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