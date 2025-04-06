package com.spring.nuqta.forgotPassword.General;

import com.spring.nuqta.exception.GlobalException;
import com.spring.nuqta.forgotPassword.Entity.ResetPasswordEntity;
import com.spring.nuqta.forgotPassword.Repo.ResetPasswordRepo;
import com.spring.nuqta.forgotPassword.Services.ResetPasswordService;
import com.spring.nuqta.mail.Services.EmailService;
import com.spring.nuqta.mail.template.ForgotPasswordWithOtp;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.organization.Projection.OrgAuthProjection;
import com.spring.nuqta.organization.Repo.OrgRepo;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import com.spring.nuqta.usermanagement.Projection.UserAuthProjection;
import com.spring.nuqta.usermanagement.Repo.UserRepo;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeneralResetTest {

    private final String testEmail = "test@example.com";
    private final String testOtp = "123456";
    private final String testPassword = "newPassword123";
    private final Locale testLocale = Locale.US;
    @Mock
    private ResetPasswordRepo resetPasswordRepo;
    @Mock
    private EmailService emailService;
    @Mock
    private ResetPasswordService resetPasswordService;
    @Mock
    private UserRepo userRepo;
    @Mock
    private OrgRepo organizationRepo;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private MessageSource messageSource;
    @InjectMocks
    private GeneralReset generalReset;

    @Test
    void sendOtpEmail_UserNotFound_ReturnsOkWithMessage() {
        when(userRepo.findUserAuthProjectionByEmail(testEmail)).thenReturn(Optional.empty());
        when(organizationRepo.findOrgAuthProjectionByEmail(testEmail)).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("email.not.found"), any(), eq(testLocale))).thenReturn("Test message");

        ResponseEntity<Map<String, String>> response = generalReset.sendOtpEmail(testEmail);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Test message", response.getBody().get("message"));
    }

    @Test
    void sendOtpEmail_UserFoundButNotVerified_ReturnsBadRequest() {
        UserAuthProjection user = mock(UserAuthProjection.class);
        when(user.enabled()).thenReturn(false);
        when(userRepo.findUserAuthProjectionByEmail(testEmail)).thenReturn(Optional.of(user));
        when(organizationRepo.findOrgAuthProjectionByEmail(testEmail)).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("email.not.verified"), any(), eq(testLocale))).thenReturn("Test message");

        ResponseEntity<Map<String, String>> response = generalReset.sendOtpEmail(testEmail);

        assertEquals(400, response.getStatusCodeValue());
        assertEquals("Test message", response.getBody().get("message"));
    }

    @Test
    void sendOtpEmail_UserFoundAndVerified_SendsEmail() throws MessagingException {
        UserAuthProjection user = mock(UserAuthProjection.class);
        when(user.enabled()).thenReturn(true);
        when(user.email()).thenReturn(testEmail);
        when(userRepo.findUserAuthProjectionByEmail(testEmail)).thenReturn(Optional.of(user));
        when(organizationRepo.findOrgAuthProjectionByEmail(testEmail)).thenReturn(Optional.empty());

        ResetPasswordEntity resetEntity = new ResetPasswordEntity();
        when(resetPasswordService.generateOtp()).thenReturn(testOtp);
        when(resetPasswordRepo.findByUser_Email(testEmail)).thenReturn(null);
        when(resetPasswordRepo.save(any(ResetPasswordEntity.class))).thenReturn(resetEntity);

        UserEntity fullUser = new UserEntity();
        when(userRepo.findByEmail(testEmail)).thenReturn(Optional.of(fullUser));

        doNothing().when(emailService).sendMail(any(ForgotPasswordWithOtp.class));
        when(messageSource.getMessage(eq("otp.success"), any(), eq(testLocale))).thenReturn("Test message");

        ResponseEntity<Map<String, String>> response = generalReset.sendOtpEmail(testEmail);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Test message", response.getBody().get("message"));
        verify(emailService).sendMail(any(ForgotPasswordWithOtp.class));
    }

    @Test
    void sendOtpEmail_OrgFoundAndVerified_SendsEmail() throws MessagingException {
        OrgAuthProjection org = mock(OrgAuthProjection.class);
        when(org.enabled()).thenReturn(true);
        when(org.email()).thenReturn(testEmail);
        when(organizationRepo.findOrgAuthProjectionByEmail(testEmail)).thenReturn(Optional.of(org));
        when(userRepo.findUserAuthProjectionByEmail(testEmail)).thenReturn(Optional.empty());

        ResetPasswordEntity resetEntity = new ResetPasswordEntity();
        when(resetPasswordService.generateOtp()).thenReturn(testOtp);
        when(resetPasswordRepo.findByOrganization_Email(testEmail)).thenReturn(null);
        when(resetPasswordRepo.save(any(ResetPasswordEntity.class))).thenReturn(resetEntity);

        OrgEntity fullOrg = new OrgEntity();
        when(organizationRepo.findByEmail(testEmail)).thenReturn(Optional.of(fullOrg));

        doNothing().when(emailService).sendMail(any(ForgotPasswordWithOtp.class));
        when(messageSource.getMessage(eq("otp.success"), any(), eq(testLocale))).thenReturn("Test message");

        ResponseEntity<Map<String, String>> response = generalReset.sendOtpEmail(testEmail);

        assertEquals(200, response.getStatusCodeValue());
        assertEquals("Test message", response.getBody().get("message"));
        verify(emailService).sendMail(any(ForgotPasswordWithOtp.class));
    }

    @Test
    void sendOtpEmail_EmailSendingFails_ReturnsInternalError() throws MessagingException {
        UserAuthProjection user = mock(UserAuthProjection.class);
        when(user.enabled()).thenReturn(true);
        when(user.email()).thenReturn(testEmail);
        when(userRepo.findUserAuthProjectionByEmail(testEmail)).thenReturn(Optional.of(user));
        when(organizationRepo.findOrgAuthProjectionByEmail(testEmail)).thenReturn(Optional.empty());

        ResetPasswordEntity resetEntity = new ResetPasswordEntity();
        when(resetPasswordService.generateOtp()).thenReturn(testOtp);
        when(resetPasswordRepo.findByUser_Email(testEmail)).thenReturn(null);
        when(resetPasswordRepo.save(any(ResetPasswordEntity.class))).thenReturn(resetEntity);

        UserEntity fullUser = new UserEntity();
        when(userRepo.findByEmail(testEmail)).thenReturn(Optional.of(fullUser));

        doThrow(new MessagingException("Failed to send")).when(emailService).sendMail(any(ForgotPasswordWithOtp.class));
        when(messageSource.getMessage(eq("otp.send.error"), any(), eq(testLocale))).thenReturn("Test message");

        ResponseEntity<Map<String, String>> response = generalReset.sendOtpEmail(testEmail);

        assertEquals(500, response.getStatusCodeValue());
        assertEquals("Test message", response.getBody().get("message"));
    }

    @Test
    void retrieveOrCreateOtpEntity_UserExists_ReturnsExistingEntity() {
        UserAuthProjection user = mock(UserAuthProjection.class);
        when(user.email()).thenReturn(testEmail);
        Optional<UserAuthProjection> userOpt = Optional.of(user);
        Optional<OrgAuthProjection> orgOpt = Optional.empty();

        ResetPasswordEntity existingEntity = new ResetPasswordEntity();
        when(resetPasswordRepo.findByUser_Email(testEmail)).thenReturn(existingEntity);

        ResetPasswordEntity result = generalReset.retrieveOrCreateOtpEntity(userOpt, orgOpt);

        assertSame(existingEntity, result);
    }

    @Test
    void retrieveOrCreateOtpEntity_OrgExists_ReturnsExistingEntity() {
        OrgAuthProjection org = mock(OrgAuthProjection.class);
        when(org.email()).thenReturn(testEmail);
        Optional<OrgAuthProjection> orgOpt = Optional.of(org);
        Optional<UserAuthProjection> userOpt = Optional.empty();

        ResetPasswordEntity existingEntity = new ResetPasswordEntity();
        when(resetPasswordRepo.findByOrganization_Email(testEmail)).thenReturn(existingEntity);

        ResetPasswordEntity result = generalReset.retrieveOrCreateOtpEntity(userOpt, orgOpt);

        assertSame(existingEntity, result);
    }

    @Test
    void retrieveOrCreateOtpEntity_NewUser_CreatesNewEntity() {
        UserAuthProjection user = mock(UserAuthProjection.class);
        when(user.email()).thenReturn(testEmail);
        Optional<UserAuthProjection> userOpt = Optional.of(user);
        Optional<OrgAuthProjection> orgOpt = Optional.empty();

        when(resetPasswordRepo.findByUser_Email(testEmail)).thenReturn(null);
        when(resetPasswordRepo.findByOrganization_Email(testEmail)).thenReturn(null);

        UserEntity fullUser = new UserEntity();
        when(userRepo.findByEmail(testEmail)).thenReturn(Optional.of(fullUser));

        ResetPasswordEntity result = generalReset.retrieveOrCreateOtpEntity(userOpt, orgOpt);

        assertNotNull(result);
        assertSame(fullUser, result.getUser());
    }

    @Test
    void retrieveOrCreateOtpEntity_NewOrg_CreatesNewEntity() {
        OrgAuthProjection org = mock(OrgAuthProjection.class);
        when(org.email()).thenReturn(testEmail);
        Optional<OrgAuthProjection> orgOpt = Optional.of(org);
        Optional<UserAuthProjection> userOpt = Optional.empty();

        when(resetPasswordRepo.findByUser_Email(testEmail)).thenReturn(null);
        when(resetPasswordRepo.findByOrganization_Email(testEmail)).thenReturn(null);

        OrgEntity fullOrg = new OrgEntity();
        when(organizationRepo.findByEmail(testEmail)).thenReturn(Optional.of(fullOrg));

        ResetPasswordEntity result = generalReset.retrieveOrCreateOtpEntity(userOpt, orgOpt);

        assertNotNull(result);
        assertSame(fullOrg, result.getOrganization());
    }

    @Test
    void retrieveOrCreateOtpEntity_NoUserOrOrg_ThrowsException() {
        assertThrows(NoSuchElementException.class, () -> {
            generalReset.retrieveOrCreateOtpEntity(Optional.empty(), Optional.empty());
        });
    }

    @Test
    void resetPassword_ValidUserOtp_UpdatesPassword() {
        ResetPasswordEntity token = new ResetPasswordEntity();
        token.setOtp(testOtp);
        token.setExpiredAt(LocalDateTime.now().plusMinutes(5));

        UserEntity user = new UserEntity();
        user.setEmail(testEmail);
        token.setUser(user);

        when(resetPasswordRepo.findByOtp(testOtp)).thenReturn(token);
        when(userRepo.findById(any())).thenReturn(Optional.of(user));
        when(userRepo.existsByEmail(testEmail)).thenReturn(true);
        when(passwordEncoder.encode(testPassword)).thenReturn("encodedPassword");

        boolean result = generalReset.resetPassword(testEmail, testOtp, testPassword);

        assertTrue(result);
        verify(userRepo).save(user);
        verify(resetPasswordService).deleteOtp(token);
    }

    @Test
    void resetPassword_ValidOrgOtp_UpdatesPassword() {
        ResetPasswordEntity token = new ResetPasswordEntity();
        token.setOtp(testOtp);
        token.setExpiredAt(LocalDateTime.now().plusMinutes(5));

        OrgEntity org = new OrgEntity();
        org.setEmail(testEmail);
        token.setOrganization(org);

        when(resetPasswordRepo.findByOtp(testOtp)).thenReturn(token);
        when(organizationRepo.findById(any())).thenReturn(Optional.of(org));
        when(organizationRepo.existsByEmail(testEmail)).thenReturn(true);
        when(passwordEncoder.encode(testPassword)).thenReturn("encodedPassword");

        boolean result = generalReset.resetPassword(testEmail, testOtp, testPassword);

        assertTrue(result);
        verify(organizationRepo).save(org);
        verify(resetPasswordService).deleteOtp(token);
    }

    @Test
    void resetPassword_ExpiredOtp_ThrowsException() {
        ResetPasswordEntity token = new ResetPasswordEntity();
        token.setOtp(testOtp);
        token.setExpiredAt(LocalDateTime.now().minusMinutes(1));

        when(resetPasswordRepo.findByOtp(testOtp)).thenReturn(token);

        assertThrows(GlobalException.class, () -> {
            generalReset.resetPassword(testEmail, testOtp, testPassword);
        });
    }

    @Test
    void resetPassword_InvalidOtp_ReturnsFalse() {
        when(resetPasswordRepo.findByOtp(testOtp)).thenReturn(null);

        boolean result = generalReset.resetPassword(testEmail, testOtp, testPassword);

        assertFalse(result);
    }

    @Test
    void resetPassword_UserNotFound_ReturnsFalse() {
        ResetPasswordEntity token = new ResetPasswordEntity();
        token.setOtp(testOtp);
        token.setExpiredAt(LocalDateTime.now().plusMinutes(5));

        UserEntity user = new UserEntity();
        user.setEmail(testEmail);
        token.setUser(user);

        when(resetPasswordRepo.findByOtp(testOtp)).thenReturn(token);
        when(userRepo.findById(any())).thenReturn(Optional.empty());

        boolean result = generalReset.resetPassword(testEmail, testOtp, testPassword);

        assertFalse(result);
    }

    @Test
    void getMS_ReturnsMessage() {
        when(messageSource.getMessage(eq("test.key"), any(), eq(testLocale))).thenReturn("Test message");

        String result = generalReset.getMS("test.key");

        assertEquals("Test message", result);
    }
}