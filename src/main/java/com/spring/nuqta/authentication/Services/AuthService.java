package com.spring.nuqta.authentication.Services;

import com.spring.nuqta.authentication.Dto.AuthOrgDto;
import com.spring.nuqta.authentication.Dto.AuthUserDto;
import com.spring.nuqta.authentication.Jwt.JwtUtilsOrganization;
import com.spring.nuqta.authentication.Jwt.JwtUtilsUser;
import com.spring.nuqta.exception.GlobalException;
import com.spring.nuqta.organization.Projection.OrgAuthProjection;
import com.spring.nuqta.organization.Repo.OrgRepo;
import com.spring.nuqta.usermanagement.Projection.UserAuthProjection;
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
        UserAuthProjection user = validateUserAuth(username, email, password);

        String token = jwtUtilsUser.generateToken(user);

        return new AuthUserDto(user.id(), token,
                String.valueOf(jwtUtilsUser.getExpireAt(token)), user.scope());
    }


    public AuthOrgDto authOrganization(final Map<String, Object> params)
            throws SystemException {
        String licenseNumber = (String) params.get("licenseNumber");
        String email = (String) params.get("email");
        String password = (String) params.get("password");


        validateOrganizationParams(licenseNumber, email, password);
        OrgAuthProjection organization = validateOrganizationAuth(licenseNumber, password, email);

        String token = jwtUtilsOrganization.generateToken(organization);

        return new AuthOrgDto(organization.id(), token,
                String.valueOf(jwtUtilsOrganization.getExpireAt(token)),
                organization.scope());
    }


    public <T> Optional<T> authByToken(final String token) {
        if ("USER".equals(jwtUtilsUser.getScope(token))) {
            return authenticateUser(token);
        } else if ("ORGANIZATION".equals(jwtUtilsOrganization.getScope(token))) {
            return authenticateOrganization(token);
        }
        return Optional.empty();
    }

    private <T> Optional<T> authenticateUser(String token) {
        String subject = jwtUtilsUser.getSubject(token);

        return userRepository.findUserAuthProjectionByUsername(subject)
                .map(user -> {
                    AuthUserDto authuserDto = createUserDto(user, token);
                    return (T) authuserDto;
                });
    }

    private <T> Optional<T> authenticateOrganization(String token) {
        String subject = jwtUtilsOrganization.getSubject(token);
        return organizationRepository.findOrgAuthProjectionByEmail(subject)
                .map(organization -> {
                    AuthOrgDto authOrgDto = createOrgDto(organization, token);
                    return (T) authOrgDto;
                });
    }


    // ----------------------- Private Methods ---------------------------

    private AuthUserDto createUserDto(UserAuthProjection user, String token) {
        return new AuthUserDto(user.id(), token,
                String.valueOf(jwtUtilsUser.getExpireAt(token)), user.scope());
    }

    private AuthOrgDto createOrgDto(OrgAuthProjection organization, String token) {
        return new AuthOrgDto(organization.id(), token,
                String.valueOf(jwtUtilsOrganization.getExpireAt(token)), organization.scope());
    }

    // -------------------- Validation and Auth Logic --------------------

    public void validateUserParams(String username, String email, String password) {
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

    public UserAuthProjection validateUserAuth(String username, String email, String password) {
        UserAuthProjection user = (username != null)
                ? userRepository.findUserAuthProjectionByUsername(username) // First call
                .orElseThrow(() -> new GlobalException("Username Or Email invalid.", HttpStatus.BAD_REQUEST))
                : userRepository.findUserAuthProjectionByEmail(email) // Second call (if username is null)
                .orElseThrow(() -> new GlobalException("Username Or Email invalid.", HttpStatus.BAD_REQUEST));

        if (user.enabled()) {
            Optional<UserAuthProjection> user1 = userRepository.findUserAuthProjectionByUsername(username);
            Optional<UserAuthProjection> user2 = userRepository.findUserAuthProjectionByEmail(email);
            if (user1.isPresent()
                    && passwordEncoder.matches(password, user1.get().password())) {
                return (UserAuthProjection) user1.get();
            } else if (user2.isPresent()
                    && passwordEncoder.matches(password, user2.get().password())) {
                return (UserAuthProjection) user2.get();
            } else if (!passwordEncoder.matches(password, user1.get().password())) {
                throw new GlobalException("Password invalid.", HttpStatus.BAD_REQUEST);
            } else {
                throw new GlobalException("Username Or Email invalid.", HttpStatus.BAD_REQUEST);
            }
        } else {
            throw new GlobalException("Check Email verification sent", HttpStatus.NOT_FOUND);
        }


    }

    public void validateOrganizationParams(String licenseNumber, String email, String password) {
        if ((licenseNumber == null || licenseNumber.trim().isEmpty())
                && (email == null || email.trim().isEmpty())) {
            throw new GlobalException("License Number or Email required", HttpStatus.BAD_REQUEST);
        }
        if (password == null || password.trim().isEmpty()) {
            throw new GlobalException("Password required.", HttpStatus.BAD_REQUEST);
        }
    }


    public OrgAuthProjection validateOrganizationAuth(String licenseNumber, String password, String email) {
        OrgAuthProjection organization = (email != null)
                ? organizationRepository.findOrgAuthProjectionByEmail(email)
                .orElseThrow(() -> new GlobalException("Organization Email invalid", HttpStatus.BAD_REQUEST))
                : organizationRepository.findOrgAuthProjectionByLicenseNumber(licenseNumber)
                .orElseThrow(() -> new GlobalException("License Number invalid", HttpStatus.BAD_REQUEST));

        if (organization.enabled()) {
            Optional<OrgAuthProjection> org1 = organizationRepository.findOrgAuthProjectionByLicenseNumber(licenseNumber);
            Optional<OrgAuthProjection> org2 = organizationRepository.findOrgAuthProjectionByEmail(email);

            if (org1.isPresent() && passwordEncoder.matches(password, org1.get().password())) {
                return org1.get();
            } else if (org2.isPresent() && passwordEncoder.matches(password, org2.get().password())) {
                return org2.get();
            } else {
                throw new GlobalException("Password invalid.", HttpStatus.BAD_REQUEST);
            }
        } else if (organization.email() == null) {
            throw new GlobalException("Organization Not Found", HttpStatus.NOT_FOUND);
        } else {
            throw new GlobalException("Check Organization verification sent", HttpStatus.NOT_FOUND);
        }
    }
}