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
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@TestPropertySource(properties = "site.base.url.http=http://localhost:8080")
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
    }

    @Test
    void testSendOtpEmail_User() throws MessagingException {
        when(verificationTokenService.createToken()).thenReturn(token);
        doNothing().when(emailService).sendMail(any());

        assertDoesNotThrow(() -> generalVerification.sendOtpEmail(user));
        verify(verificationTokenService, times(1)).saveToken(token);
        verify(emailService, times(1)).sendMail(any());
    }

    @Test
    void testSendOtpEmail_Org() throws MessagingException {
        token.setOrganization(org);
        when(verificationTokenService.createToken()).thenReturn(token);
        doNothing().when(emailService).sendMail(any());

        assertDoesNotThrow(() -> generalVerification.sendOtpEmail(org));
        verify(verificationTokenService, times(1)).saveToken(token);
        verify(emailService, times(1)).sendMail(any());
    }

    @Test
    void testVerifyRegistration_ValidUserToken() {
        when(verificationTokenService.findByToken("test-token")).thenReturn(token);
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        when(userRepo.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

        boolean result = generalVerification.verifyRegistration("test-token", "user@example.com");

        assertTrue(result);
        assertTrue(user.isEnabled());
        verify(userRepo, times(1)).save(user);
        verify(verificationTokenService, times(1)).removeToken(token);
    }

    @Test
    void testVerifyRegistration_ValidOrgToken() {
        token.setUser(null);
        token.setOrganization(org);
        when(verificationTokenService.findByToken("test-token")).thenReturn(token);
        when(orgRepo.findById(org.getId())).thenReturn(Optional.of(org));
        when(orgRepo.findByEmail(org.getEmail())).thenReturn(Optional.of(org));

        boolean result = generalVerification.verifyRegistration("test-token", "org@example.com");

        assertTrue(result);
        assertTrue(org.isEnabled());
        verify(orgRepo, times(1)).save(org);
        verify(verificationTokenService, times(1)).removeToken(token);
    }

    @Test
    void testVerifyRegistration_InvalidToken() {
        when(verificationTokenService.findByToken("invalid-token")).thenReturn(null);

        boolean result = generalVerification.verifyRegistration("invalid-token", "user@example.com");

        assertFalse(result);
    }

    @Test
    void testVerifyRegistration_ExpiredToken() {
        token.setExpiredAt(java.time.LocalDateTime.now().minusMinutes(1));
        when(verificationTokenService.findByToken("test-token")).thenReturn(token);

        GlobalException exception = assertThrows(GlobalException.class, () ->
                generalVerification.verifyRegistration("test-token", "user@example.com"));

        assertEquals("Expired verification token.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }
}
