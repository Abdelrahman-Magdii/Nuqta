package com.spring.nuqta.verificationToken.General;

import com.spring.nuqta.exception.GlobalException;
import com.spring.nuqta.mail.Services.EmailService;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.organization.Repo.OrgRepo;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import com.spring.nuqta.usermanagement.Repo.UserRepo;
import com.spring.nuqta.verificationToken.Entity.VerificationToken;
import com.spring.nuqta.verificationToken.Services.VerificationTokenService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeneralVerificationTest {

    @Mock
    private EmailService emailService;

    @Mock
    private VerificationTokenService verificationTokenService;

    @Mock
    private OrgRepo orgRepo;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private GeneralVerification generalVerification;

    private VerificationToken token;
    private UserEntity user;
    private OrgEntity org;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(generalVerification, "baseUrl", "http://localhost:8080");

        user = new UserEntity();
        user.setId(1L);
        user.setEmail("user@example.com");

        org = new OrgEntity();
        org.setId(1L);
        org.setEmail("org@example.com");

        token = new VerificationToken();
        token.setToken("test-token");
        token.setUser(user);
        token.setExpiredAt(LocalDateTime.now().plusMinutes(10)); // Valid token
    }

    // ✅ Test sending OTP email to a user
    @Test
    void testSendOtpEmail_User() throws MessagingException {
        when(verificationTokenService.createToken()).thenReturn(token);
        doNothing().when(emailService).sendMail(any());

        assertDoesNotThrow(() -> generalVerification.sendOtpEmail(user));

        verify(verificationTokenService, times(1)).saveToken(token);
        verify(emailService, times(1)).sendMail(any());
    }

    // ✅ Test sending OTP email to an organization
    @Test
    void testSendOtpEmail_Org() throws MessagingException {
        token.setOrganization(org);
        when(verificationTokenService.createToken()).thenReturn(token);
        doNothing().when(emailService).sendMail(any());

        assertDoesNotThrow(() -> generalVerification.sendOtpEmail(org));

        verify(verificationTokenService, times(1)).saveToken(token);
        verify(emailService, times(1)).sendMail(any());
    }

    // ✅ Test successful user verification
    @Test
    void testVerifyRegistration_ValidUserToken() {
        when(verificationTokenService.findByToken("test-token")).thenReturn(token);
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        boolean result = generalVerification.verifyRegistration("test-token", "user@example.com");

        assertTrue(result);
        assertTrue(user.isEnabled());
        verify(userRepo, times(1)).save(user);
        verify(verificationTokenService, times(1)).removeToken(token);
    }

    // ✅ Test successful organization verification
    @Test
    void testVerifyRegistration_ValidOrgToken() {
        token.setUser(null);
        token.setOrganization(org);

        when(verificationTokenService.findByToken("test-token")).thenReturn(token);
        when(orgRepo.findByEmail(org.getEmail())).thenReturn(Optional.of(org));

        boolean result = generalVerification.verifyRegistration("test-token", "org@example.com");

        assertTrue(result);
        assertTrue(org.isEnabled());
        verify(orgRepo, times(1)).save(org);
        verify(verificationTokenService, times(1)).removeToken(token);
    }

    // ✅ Test verification with an invalid token
    @Test
    void testVerifyRegistration_InvalidToken() {
        when(verificationTokenService.findByToken("invalid-token")).thenReturn(null);

        boolean result = generalVerification.verifyRegistration("invalid-token", "user@example.com");

        assertFalse(result);
    }

    // ✅ Test verification with an expired token
    @Test
    void testVerifyRegistration_ExpiredToken() {
        token.setExpiredAt(LocalDateTime.now().minusMinutes(1)); // Expired token
        when(verificationTokenService.findByToken("test-token")).thenReturn(token);

        GlobalException exception = assertThrows(GlobalException.class, () ->
                generalVerification.verifyRegistration("test-token", "user@example.com"));

        assertEquals("error.token", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    // ✅ Test verification when the user is not found
    @Test
    void testVerifyRegistration_UserNotFound() {
        when(verificationTokenService.findByToken("test-token")).thenReturn(token);
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.empty());

        boolean result = generalVerification.verifyRegistration("test-token", "user@example.com");

        assertFalse(result);
    }

    // ✅ Test verification when the organization is not found
    @Test
    void testVerifyRegistration_OrgNotFound() {
        token.setUser(null);
        token.setOrganization(org);

        when(verificationTokenService.findByToken("test-token")).thenReturn(token);
        when(orgRepo.findByEmail(org.getEmail())).thenReturn(Optional.empty());

        boolean result = generalVerification.verifyRegistration("test-token", "org@example.com");

        assertFalse(result);
    }

    // ✅ Test verification when no entity is associated with the token
    @Test
    void testVerifyRegistration_NoEntity() {
        token.setUser(null);
        token.setOrganization(null);

        when(verificationTokenService.findByToken("test-token")).thenReturn(token);

        boolean result = generalVerification.verifyRegistration("test-token", "random@example.com");

        assertFalse(result);
    }

    // ✅ Test email sending failure handling
    @Test
    void testSendOtpEmail_EmailFailure() throws MessagingException {
        when(verificationTokenService.createToken()).thenReturn(token);
        doThrow(new MessagingException("Email sending failed")).when(emailService).sendMail(any());

        assertDoesNotThrow(() -> generalVerification.sendOtpEmail(user));

        verify(verificationTokenService, times(1)).saveToken(token);
        verify(emailService, times(1)).sendMail(any());
    }
}
