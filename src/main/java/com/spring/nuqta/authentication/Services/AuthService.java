package com.spring.nuqta.authentication.Services;

import com.spring.nuqta.authentication.Dto.AuthOrgDto;
import com.spring.nuqta.authentication.Dto.AuthUserDto;
import com.spring.nuqta.authentication.Jwt.JwtUtilsOrganization;
import com.spring.nuqta.authentication.Jwt.JwtUtilsUser;
import com.spring.nuqta.exception.GlobalException;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.organization.Repo.OrgRepo;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import com.spring.nuqta.usermanagement.Repo.UserRepo;
import jakarta.transaction.SystemException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
public class AuthService {

    private final UserRepo userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrgRepo organizationRepository;
    private final JwtUtilsUser jwtUtilsUser;
    private final JwtUtilsOrganization jwtUtilsOrganization;

    public AuthService(UserRepo userRepository, PasswordEncoder passwordEncoder, OrgRepo organizationRepository, JwtUtilsUser jwtUtilsUser, JwtUtilsOrganization jwtUtilsOrganization) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.organizationRepository = organizationRepository;
        this.jwtUtilsUser = jwtUtilsUser;
        this.jwtUtilsOrganization = jwtUtilsOrganization;
    }

    // ----------------------- Public Methods ---------------------------

    public AuthUserDto authUser(final Map<String, Object> params) throws SystemException {
        String username = (String) params.get("username");
        String email = (String) params.get("email");
        String password = (String) params.get("password");

        validateUserParams(username, email, password);
        UserEntity user = validateUserAuth(username, email, password);

        String token = jwtUtilsUser.generateToken(user);

        return new AuthUserDto(user.getId(), token,
                String.valueOf(jwtUtilsUser.getExpireAt(token)), user.getScope());
    }


    public AuthOrgDto authOrganization(final Map<String, Object> params)
            throws SystemException {
        String licenseNumber = (String) params.get("licenseNumber");
        String email = (String) params.get("email");
        String password = (String) params.get("password");


        validateOrganizationParams(licenseNumber, email, password);
        OrgEntity organization = validateOrganizationAuth(licenseNumber, password, email);

        String token = jwtUtilsOrganization.generateToken(organization);

        return new AuthOrgDto(organization.getId(), token,
                String.valueOf(jwtUtilsOrganization.getExpireAt(token)),
                organization.getScope());
    }


    public <T> Optional<T> authByToken(final String token) {
        String scope = jwtUtilsUser.getScope(token);

        if (Objects.equals(scope, "USER")) {
            return authenticateUser(token);
        } else if (Objects.equals(scope, "ORGANIZATION")) {
            return authenticateOrganization(token);
        }

        return Optional.empty();
    }

    private <T> Optional<T> authenticateUser(String token) {
        String subject = jwtUtilsUser.getSubject(token);

        return userRepository.findByUsername(subject)
                .map(user -> {
                    AuthUserDto authuserDto = createUserDto(user, token);
                    return (T) authuserDto;
                });
    }

    private <T> Optional<T> authenticateOrganization(String token) {
        String subject = jwtUtilsOrganization.getSubject(token);
        return organizationRepository.findByEmail(subject)
                .map(organization -> {
                    AuthOrgDto authOrgDto = createOrgDto(organization, token);
                    return (T) authOrgDto;
                });
    }


    // ----------------------- Private Methods ---------------------------

    private AuthUserDto createUserDto(UserEntity user, String token) {
        return new AuthUserDto(user.getId(), token,
                String.valueOf(jwtUtilsUser.getExpireAt(token)), user.getScope());
    }

    private AuthOrgDto createOrgDto(OrgEntity organization, String token) {
        return new AuthOrgDto(organization.getId(), token,
                String.valueOf(jwtUtilsOrganization.getExpireAt(token)), organization.getScope());
    }

    // -------------------- Validation and Auth Logic --------------------

    private void validateUserParams(String username, String email, String password) {
        log.info("Validating user params: username = {}, email = {}, password = {}",
                username, email, password != null ? "******" : null);
        if ((username == null || username.trim().isEmpty())
                && (email == null || email.trim().isEmpty())) {
            throw new GlobalException("Email Or Username required", HttpStatus.BAD_REQUEST);
        }
        if (password == null || password.trim().isEmpty()) {
            throw new GlobalException("Password required", HttpStatus.BAD_REQUEST);
        }
    }

    private UserEntity validateUserAuth(String username, String email, String password) {
        UserEntity user = (username != null)
                ? userRepository.findByUsername(username)
                .orElseThrow(() -> new GlobalException(
                        "Username Or Email invalid.", HttpStatus.BAD_REQUEST))
                : userRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalException(
                        "Username Or Email invalid.", HttpStatus.BAD_REQUEST));

        if (user.isEnabled()) {
            Optional<UserEntity> user1 = userRepository.findByUsername(username);
            Optional<UserEntity> user2 = userRepository.findByEmail(email);
            if (user1.isPresent()
                    && passwordEncoder.matches(password, user1.get().getPassword())) {
                return (UserEntity) user1.get();
            } else if (user2.isPresent()
                    && passwordEncoder.matches(password, user2.get().getPassword())) {
                return (UserEntity) user2.get();
            } else if (!passwordEncoder.matches(password, user1.get().getPassword())) {
                throw new GlobalException("Password invalid.", HttpStatus.BAD_REQUEST);
            } else {
                throw new GlobalException("Username Or Email invalid.", HttpStatus.BAD_REQUEST);
            }
        } else {
            throw new GlobalException("Check Email verification sent", HttpStatus.NOT_FOUND);
        }


    }

    private void validateOrganizationParams(String licenseNumber, String email, String password) {
        if ((licenseNumber == null || licenseNumber.trim().isEmpty())
                && (email == null || email.trim().isEmpty())) {
            throw new GlobalException("License Number or Email required", HttpStatus.BAD_REQUEST);
        }
        if (password == null || password.trim().isEmpty()) {
            throw new GlobalException("Password required.", HttpStatus.BAD_REQUEST);
        }
    }


    private OrgEntity validateOrganizationAuth(String licenseNumber, String password, String email) {
        OrgEntity organization = (email != null) ?
                organizationRepository.findByEmail(email)
                        .orElseThrow(() -> new GlobalException(
                                "Organization Email invalid", HttpStatus.BAD_REQUEST)) :
                organizationRepository.findByLicenseNumber(licenseNumber)
                        .orElseThrow(() -> new GlobalException(
                                "License Number invalid", HttpStatus.BAD_REQUEST));

        if (organization.isEnabled()) {
            Optional<OrgEntity> org1 = organizationRepository.findByLicenseNumber(licenseNumber);
            Optional<OrgEntity> org2 = organizationRepository.findByEmail(email);

            if (org1.isPresent()
                    && passwordEncoder.matches(password, org1.get().getPassword())) {
                return (OrgEntity) org1.get();
            } else if (org2.isPresent()
                    && passwordEncoder.matches(password, org2.get().getPassword())) {
                return (OrgEntity) org2.get();
            } else if (!passwordEncoder.matches(password, org1.get().getPassword())) {
                throw new GlobalException("Password invalid.", HttpStatus.BAD_REQUEST);
            } else {
                throw new GlobalException("License Number Or Email invalid.", HttpStatus.BAD_REQUEST);
            }
        } else if (organization.getEmail() == null) {
            throw new GlobalException("Organization Not Found", HttpStatus.NOT_FOUND);
        } else {
            throw new GlobalException("Check Organization verification sent", HttpStatus.NOT_FOUND);
        }

    }


}