package com.spring.nuqta.authentication.Services;

import com.spring.nuqta.authentication.Dto.AuthOrgDto;
import com.spring.nuqta.authentication.Dto.AuthUserDto;
import com.spring.nuqta.authentication.Jwt.JwtUtilsOrganization;
import com.spring.nuqta.authentication.Jwt.JwtUtilsUser;
import com.spring.nuqta.enums.Scope;
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
                String.valueOf(jwtUtilsUser.getExpireAt(token)),
                jwtUtilsUser.createRefreshToken(user), user.getScope());
    }


    public AuthOrgDto authOrganization(final Map<String, Object> params)
            throws SystemException {
        String referenceId = (String) params.get("reference_id");
        String password = (String) params.get("password");

        validateOrganizationParams(referenceId, password);
        OrgEntity organization = validateOrganizationAuth(referenceId, password);

        String token = jwtUtilsOrganization.generateToken(organization);

        return new AuthOrgDto(organization.getId(), token,
                String.valueOf(jwtUtilsOrganization.getExpireAt(token)),
                jwtUtilsOrganization.createRefreshToken(organization),
                organization.getScope());
    }


    public <T> Optional<T> authByToken(final String token) throws SystemException {
        String scope = jwtUtilsUser.getScope(token);
        String subject;

        if (Scope.USER.equals(scope)) {
            subject = jwtUtilsUser.getSubject(token);
            return (Optional<T>) userRepository.findByUsername(subject)
                    .map(user -> createUserDto(user, token));
        } else if (Scope.ORGANIZATION.equals(scope)) {
            subject = jwtUtilsOrganization.getSubject(token);
            return (Optional<T>) organizationRepository.findByEmail(subject)
                    .map(organization -> createOrgDto(organization, token));
        }
        return Optional.empty();
    }

    // ----------------------- Private Methods ---------------------------

    private AuthUserDto createUserDto(UserEntity user, String token) {
        return new AuthUserDto(user.getId(), token,
                String.valueOf(jwtUtilsUser.getExpireAt(token)),
                jwtUtilsUser.createRefreshToken(user), user.getScope());
    }

    private AuthOrgDto createOrgDto(OrgEntity organization, String token) {
        return new AuthOrgDto(organization.getId(), token,
                String.valueOf(jwtUtilsOrganization.getExpireAt(token)),
                jwtUtilsOrganization.createRefreshToken(organization), organization.getScope());
    }

    // -------------------- Validation and Auth Logic --------------------

    private void validateUserParams(String username, String email, String password) {
        log.info("Validating user params: username = {}, email = {}, password = {}",
                username, email, password != null ? "******" : null);
        if ((username == null || username.trim().isEmpty())
                && (email == null || email.trim().isEmpty())) {
            throw new GlobalException("error.emailOrLoginName.required", HttpStatus.BAD_REQUEST);
        }
        if (password == null || password.trim().isEmpty()) {
            throw new GlobalException("error.password.required", HttpStatus.BAD_REQUEST);
        }
    }

    private UserEntity validateUserAuth(String username, String email, String password) {
        UserEntity user = (username != null)
                ? userRepository.findByUsername(username)
                .orElseThrow(() -> new GlobalException(
                        "error.loginNameOrEmail.invalid", HttpStatus.BAD_REQUEST))
                : userRepository.findByEmail(email)
                .orElseThrow(() -> new GlobalException(
                        "error.loginNameOrEmail.invalid", HttpStatus.BAD_REQUEST));

        Optional<UserEntity> user1 = userRepository.findByUsername(username);
        Optional<UserEntity> user2 = userRepository.findByEmail(email);
        if (user1.isPresent()
                && passwordEncoder.matches(password, user1.get().getPassword())) {
            return (UserEntity) user1.get();
        } else if (user2.isPresent()
                && passwordEncoder.matches(password, user2.get().getPassword())) {
            return (UserEntity) user2.get();
        } else if (!passwordEncoder.matches(password, user1.get().getPassword())) {
            throw new GlobalException("error.password.invalid", HttpStatus.BAD_REQUEST);
        } else {
            throw new GlobalException("error.loginNameOrEmail.invalid", HttpStatus.BAD_REQUEST);
        }

    }

    private void validateOrganizationParams(String referenceId, String password) {
        if (referenceId == null || referenceId.trim().isEmpty()) {
            throw new GlobalException("error.referenceId.required", HttpStatus.BAD_REQUEST);
        }
        if (password == null || password.trim().isEmpty()) {
            throw new GlobalException("error.password.required", HttpStatus.BAD_REQUEST);
        }
    }

    private OrgEntity validateOrganizationAuth(String email, String password) {
        OrgEntity organization =
                organizationRepository.findByEmail(email)
                        .orElseThrow(() -> new GlobalException(
                                "error.email.invalid", HttpStatus.BAD_REQUEST));

        if (!passwordEncoder.matches(password, organization.getPassword())) {
            throw new GlobalException("error.password.invalid", HttpStatus.BAD_REQUEST);
        }
        return organization;
    }

}