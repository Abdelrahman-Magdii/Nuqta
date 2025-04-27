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
    void getMS_ReturnsMessage() {
        when(messageSource.getMessage(eq("test.key"), eq(null), any(Locale.class)))
                .thenReturn("Test message");

        String result = generalReset.getMS("test.key");

        assertEquals("Test message", result);
    }

    @Test
    void sendOtpEmail_WhenUserNotFound_ReturnsNotFoundMessage() {
        // Setup
        when(userRepo.findUserAuthProjectionByEmail(testEmail)).thenReturn(Optional.empty());
        when(organizationRepo.findOrgAuthProjectionByEmail(testEmail)).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("email.not.found"), eq(null), any(Locale.class)))
                .thenReturn("Email not found");

        // Execute
        ResponseEntity<Map<String, String>> response = generalReset.sendOtpEmail(testEmail);

        // Verify
        assertEquals("Email not found", response.getBody().get("message"));
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void sendOtpEmail_WhenUserFoundAndEnabled_SendsOtp() throws MessagingException {
        // Setup
        UserAuthProjection userProjection = mock(UserAuthProjection.class);
        when(userProjection.enabled()).thenReturn(true);
        when(userProjection.email()).thenReturn(testEmail);

        when(userRepo.findUserAuthProjectionByEmail(testEmail)).thenReturn(Optional.of(userProjection));
        when(resetPasswordService.generateOtp()).thenReturn(testOtp);
        when(userRepo.findByEmail(testEmail)).thenReturn(Optional.of(new UserEntity()));
        when(messageSource.getMessage(eq("otp.success"), eq(null), any(Locale.class)))
                .thenReturn("OTP sent successfully");

        // Execute
        ResponseEntity<Map<String, String>> response = generalReset.sendOtpEmail(testEmail);

        // Verify
        verify(emailService).sendMail(any(ForgotPasswordWithOtp.class));
        assertEquals("OTP sent successfully", response.getBody().get("message"));
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void sendOtpEmail_WhenOrganizationFoundAndEnabled_SendsOtp() throws MessagingException {
        // Setup
        OrgAuthProjection orgProjection = mock(OrgAuthProjection.class);
        when(orgProjection.enabled()).thenReturn(true);
        when(orgProjection.email()).thenReturn(testEmail);

        when(organizationRepo.findOrgAuthProjectionByEmail(testEmail)).thenReturn(Optional.of(orgProjection));
        when(resetPasswordService.generateOtp()).thenReturn(testOtp);
        when(organizationRepo.findByEmail(testEmail)).thenReturn(Optional.of(new OrgEntity()));
        when(messageSource.getMessage(eq("otp.success"), eq(null), any(Locale.class)))
                .thenReturn("OTP sent successfully");

        // Execute
        ResponseEntity<Map<String, String>> response = generalReset.sendOtpEmail(testEmail);

        // Verify
        verify(emailService).sendMail(any(ForgotPasswordWithOtp.class));
        assertEquals("OTP sent successfully", response.getBody().get("message"));
        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void sendOtpEmail_WhenUserFoundButNotVerified_ReturnsBadRequest() {
        // Setup
        UserAuthProjection userProjection = mock(UserAuthProjection.class);
        when(userProjection.enabled()).thenReturn(false);
        when(userRepo.findUserAuthProjectionByEmail(testEmail)).thenReturn(Optional.of(userProjection));
        when(messageSource.getMessage(eq("email.not.verified"), eq(null), any(Locale.class)))
                .thenReturn("Email not verified");

        // Execute
        ResponseEntity<Map<String, String>> response = generalReset.sendOtpEmail(testEmail);

        // Verify
        assertEquals("Email not verified", response.getBody().get("message"));
        assertEquals(400, response.getStatusCodeValue());
    }

    @Test
    void sendOtpEmail_WhenEmailSendingFails_ReturnsInternalError() throws MessagingException {
        // Setup
        UserAuthProjection userProjection = mock(UserAuthProjection.class);
        when(userProjection.enabled()).thenReturn(true);
        when(userProjection.email()).thenReturn(testEmail);

        when(userRepo.findUserAuthProjectionByEmail(testEmail)).thenReturn(Optional.of(userProjection));
        when(resetPasswordService.generateOtp()).thenReturn(testOtp);
        when(userRepo.findByEmail(testEmail)).thenReturn(Optional.of(new UserEntity()));
        when(messageSource.getMessage(eq("otp.send.error"), eq(null), any(Locale.class)))
                .thenReturn("Failed to send OTP");
        doThrow(new MessagingException()).when(emailService).sendMail(any(ForgotPasswordWithOtp.class));

        // Execute
        ResponseEntity<Map<String, String>> response = generalReset.sendOtpEmail(testEmail);

        // Verify
        assertEquals("Failed to send OTP", response.getBody().get("message"));
        assertEquals(500, response.getStatusCodeValue());
    }
}