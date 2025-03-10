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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepo userRepository;

    @Mock
    private OrgRepo organizationRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtilsUser jwtUtilsUser;

    @Mock
    private JwtUtilsOrganization jwtUtilsOrganization;

    @InjectMocks
    private AuthService authService;

    private Map<String, Object> userParams;
    private Map<String, Object> orgParams;

    private UserAuthProjection userProjection;
    private OrgAuthProjection orgProjection;

    @BeforeEach
    void setUp() {
        userParams = Map.of(
                "username", "testUser",
                "email", "test@example.com",
                "password", "password123"
        );

        orgParams = Map.of(
                "licenseNumber", "LIC123",
                "email", "org@example.com",
                "password", "orgPassword123"
        );

        userProjection = mock(UserAuthProjection.class);
        orgProjection = mock(OrgAuthProjection.class);
    }

    @Test
    void testAuthUser_Success() throws SystemException {
        // Arrange
        UserAuthProjection userProjection = mock(UserAuthProjection.class);
        when(userRepository.findUserAuthProjectionByUsername(anyString())).thenReturn(Optional.of(userProjection));
        when(userRepository.findUserAuthProjectionByEmail(anyString())).thenReturn(Optional.of(userProjection)); // Stub for email lookup
        when(userProjection.password()).thenReturn("encodedPassword"); // Mock the encoded password
        when(passwordEncoder.matches("password123", "encodedPassword")).thenReturn(true); // Match the exact password
        when(userProjection.enabled()).thenReturn(true);
        when(jwtUtilsUser.generateToken(userProjection)).thenReturn("jwtToken");
        when(jwtUtilsUser.getExpireAt("jwtToken")).thenReturn(new Date(123456789L));

        // Act
        AuthUserDto result = authService.authUser(userParams);

        // Assert
        assertNotNull(result);
        assertEquals("jwtToken", result.getAccessToken());
        verify(userRepository, times(2)).findUserAuthProjectionByUsername(anyString()); // Verify username lookup
        verify(userRepository, times(1)).findUserAuthProjectionByEmail(anyString()); // Verify email lookup
        verify(passwordEncoder, times(1)).matches("password123", "encodedPassword");
    }

    @Test
    void testAuthOrganization_Success() throws SystemException {
        // Arrange
        OrgAuthProjection orgProjection = mock(OrgAuthProjection.class);
        when(organizationRepository.findOrgAuthProjectionByEmail(anyString())).thenReturn(Optional.of(orgProjection));
        when(organizationRepository.findOrgAuthProjectionByLicenseNumber(anyString())).thenReturn(Optional.of(orgProjection)); // Stub for license number lookup
        when(orgProjection.password()).thenReturn("encodedOrgPassword"); // Mock the encoded password
        when(passwordEncoder.matches("orgPassword123", "encodedOrgPassword")).thenReturn(true); // Match the exact password
        when(orgProjection.enabled()).thenReturn(true);
        when(jwtUtilsOrganization.generateToken(orgProjection)).thenReturn("jwtToken");
        when(jwtUtilsOrganization.getExpireAt("jwtToken")).thenReturn(new Date(123456789L));

        // Act
        AuthOrgDto result = authService.authOrganization(orgParams);

        // Assert
        assertNotNull(result);
        assertEquals("jwtToken", result.getAccessToken());
        verify(organizationRepository, times(2)).findOrgAuthProjectionByEmail(anyString()); // Verify email lookup
        verify(organizationRepository, times(1)).findOrgAuthProjectionByLicenseNumber(anyString()); // Verify license number lookup
        verify(passwordEncoder, times(1)).matches("orgPassword123", "encodedOrgPassword");
    }


    @Test
    void testAuthUser_InvalidCredentials() {
        // Arrange
        when(userRepository.findUserAuthProjectionByUsername(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        GlobalException exception = assertThrows(GlobalException.class, () -> authService.authUser(userParams));
        assertEquals("error.auth.usernameOrEmailInvalid", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }


    @Test
    void testAuthOrganization_InvalidCredentials() {
        // Arrange
        when(organizationRepository.findOrgAuthProjectionByEmail(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        GlobalException exception = assertThrows(GlobalException.class, () -> authService.authOrganization(orgParams));
        assertEquals("error.org.emailInvalid", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testAuthByToken_UserToken() {
        // Arrange
        UserAuthProjection userProjection = mock(UserAuthProjection.class);
        when(jwtUtilsUser.getScope("userToken")).thenReturn("USER");
        when(jwtUtilsUser.getSubject("userToken")).thenReturn("testUser@gmail.com");
        when(userRepository.findUserAuthProjectionByEmail("testUser@gmail.com")).thenReturn(Optional.of(userProjection));
        when(jwtUtilsUser.getExpireAt("userToken")).thenReturn(new Date(123456789L));

        // Act
        Optional<?> result = authService.authByToken("userToken");

        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get() instanceof AuthUserDto);
        verify(jwtUtilsUser, times(1)).getScope("userToken");
        verify(userRepository, times(0)).findUserAuthProjectionByEmail("testUser");
    }

    @Test
    void testAuthByToken_OrganizationToken() {
        // Arrange
        OrgAuthProjection orgProjection = mock(OrgAuthProjection.class);
        when(jwtUtilsOrganization.getScope("orgToken")).thenReturn("ORGANIZATION");
        when(jwtUtilsOrganization.getSubject("orgToken")).thenReturn("org@example.com");
        when(organizationRepository.findOrgAuthProjectionByEmail("org@example.com")).thenReturn(Optional.of(orgProjection));
        when(jwtUtilsOrganization.getExpireAt("orgToken")).thenReturn(new Date(123456789L));

        // Act
        Optional<?> result = authService.authByToken("orgToken");

        // Assert
        assertTrue(result.isPresent());
        assertTrue(result.get() instanceof AuthOrgDto);
        verify(jwtUtilsOrganization, times(1)).getScope("orgToken");
        verify(organizationRepository, times(1)).findOrgAuthProjectionByEmail("org@example.com");
    }

    @Test
    void testAuthByToken_InvalidToken() {
        // Arrange
        when(jwtUtilsUser.getScope("invalidToken")).thenReturn("INVALID");

        // Act
        Optional<?> result = authService.authByToken("invalidToken");

        // Assert
        assertTrue(result.isEmpty());
    }

    // -------------------- validateUserParams Tests --------------------

    @Test
    void testValidateUserParams_ValidInput() {
        // Arrange
        String username = "testUser";
        String email = "test@example.com";
        String password = "password123";

        // Act & Assert
        assertDoesNotThrow(() -> authService.validateUserParams(username, email, password));
    }

    @Test
    void testValidateUserParams_MissingUsernameAndEmail() {
        // Arrange
        String username = null;
        String email = null;
        String password = "password123";

        // Act & Assert
        GlobalException exception = assertThrows(GlobalException.class,
                () -> authService.validateUserParams(username, email, password));
        assertEquals("error.auth.emailOrUsernameRequired", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testValidateUserParams_MissingPassword() {
        // Arrange
        String username = "testUser";
        String email = "test@example.com";
        String password = null;

        // Act & Assert
        GlobalException exception = assertThrows(GlobalException.class,
                () -> authService.validateUserParams(username, email, password));
        assertEquals("error.auth.passwordRequired", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    // -------------------- validateUserAuth Tests --------------------

    @Test
    void testValidateUserAuth_InvalidUsername() {
        // Arrange
        String username = "invalidUser";
        String email = null;
        String password = "password123";

        when(userRepository.findUserAuthProjectionByUsername(username)).thenReturn(Optional.empty());

        // Act & Assert
        GlobalException exception = assertThrows(GlobalException.class,
                () -> authService.validateUserAuth(username, email, password));
        assertEquals("error.auth.usernameOrEmailInvalid", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testValidateUserAuth_InvalidPassword() {
        // Arrange
        String username = "testUser";
        String email = null;
        String password = "wrongPassword";

        when(userRepository.findUserAuthProjectionByUsername(username)).thenReturn(Optional.of(userProjection));
        when(userProjection.enabled()).thenReturn(true);
        when(userProjection.password()).thenReturn("encodedPassword");
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(false);

        // Act & Assert
        GlobalException exception = assertThrows(GlobalException.class,
                () -> authService.validateUserAuth(username, email, password));
        assertEquals("error.auth.passwordInvalid", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    // -------------------- validateOrganizationParams Tests --------------------

    @Test
    void testValidateOrganizationParams_ValidInput() {
        // Arrange
        String licenseNumber = "LIC123";
        String email = "org@example.com";
        String password = "orgPassword123";

        // Act & Assert
        assertDoesNotThrow(() -> authService.validateOrganizationParams(licenseNumber, email, password));
    }

    @Test
    void testValidateOrganizationParams_MissingLicenseNumberAndEmail() {
        // Arrange
        String licenseNumber = null;
        String email = null;
        String password = "orgPassword123";

        // Act & Assert
        GlobalException exception = assertThrows(GlobalException.class,
                () -> authService.validateOrganizationParams(licenseNumber, email, password));
        assertEquals("error.org.licenseOrEmailRequired", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testValidateOrganizationParams_MissingPassword() {
        // Arrange
        String licenseNumber = "LIC123";
        String email = "org@example.com";
        String password = null;

        // Act & Assert
        GlobalException exception = assertThrows(GlobalException.class,
                () -> authService.validateOrganizationParams(licenseNumber, email, password));
        assertEquals("error.org.passwordRequired", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    // -------------------- validateOrganizationAuth Tests --------------------

    @Test
    void testValidateOrganizationAuth_ValidEmail() {
        // Arrange
        String licenseNumber = null;
        String email = "org@example.com";
        String password = "orgPassword123";

        when(organizationRepository.findOrgAuthProjectionByEmail(email)).thenReturn(Optional.of(orgProjection));
        when(orgProjection.enabled()).thenReturn(true);
        when(orgProjection.password()).thenReturn("encodedPassword");
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(true);

        // Act
        OrgAuthProjection result = authService.validateOrganizationAuth(licenseNumber, password, email);

        // Assert
        assertNotNull(result);
        verify(organizationRepository, times(2)).findOrgAuthProjectionByEmail(email);
        verify(passwordEncoder, times(1)).matches(password, "encodedPassword");
    }

    @Test
    void testValidateOrganizationAuth_InvalidEmail() {
        // Arrange
        String licenseNumber = null;
        String email = "invalid@example.com";
        String password = "orgPassword123";

        when(organizationRepository.findOrgAuthProjectionByEmail(email)).thenReturn(Optional.empty());

        // Act & Assert
        GlobalException exception = assertThrows(GlobalException.class,
                () -> authService.validateOrganizationAuth(licenseNumber, password, email));
        assertEquals("error.org.emailInvalid", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }


    @Test
    void testValidateUserAuth_ValidUsername() {
        // Arrange
        String username = "testUser";
        String email = null;
        String password = "password123";

        when(userRepository.findUserAuthProjectionByUsername(username)).thenReturn(Optional.of(userProjection));
        when(userProjection.enabled()).thenReturn(true);
        when(userProjection.password()).thenReturn("encodedPassword");
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(true);

        // Act
        UserAuthProjection result = authService.validateUserAuth(username, email, password);

        // Assert
        assertNotNull(result);
        verify(userRepository, times(2)).findUserAuthProjectionByUsername(username); // Expect 2 calls
        verify(passwordEncoder, times(1)).matches(password, "encodedPassword");
    }

    @Test
    void testValidateOrganizationAuth_InvalidPassword() {
        // Arrange
        String licenseNumber = null;
        String email = "org@example.com";
        String password = "wrongPassword";

        // Mock the repository to return a non-empty Optional
        when(organizationRepository.findOrgAuthProjectionByEmail(email)).thenReturn(Optional.of(orgProjection));
        when(orgProjection.enabled()).thenReturn(true);
        when(orgProjection.password()).thenReturn("encodedPassword");
        when(passwordEncoder.matches(password, "encodedPassword")).thenReturn(false);

        // Act & Assert
        GlobalException exception = assertThrows(GlobalException.class,
                () -> authService.validateOrganizationAuth(licenseNumber, password, email));
        assertEquals("error.auth.passwordInvalid", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }
}