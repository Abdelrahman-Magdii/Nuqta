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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GeneralResetTest {

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

    @InjectMocks
    private GeneralReset generalReset;

    private String email;
    private String otp;
    private String newPassword;

    @BeforeEach
    void setUp() {
        email = "test@example.com";
        otp = "123456";
        newPassword = "newPassword123";
    }

    @Test
    void testSendOtpEmail_UserNotFound() {
        when(userRepo.findUserAuthProjectionByEmail(email)).thenReturn(Optional.empty());
        when(organizationRepo.findOrgAuthProjectionByEmail(email)).thenReturn(Optional.empty());

        String result = generalReset.sendOtpEmail(email);

        assertEquals("Email not found. Please check the email address and try again.", result);
    }

    @Test
    void testSendOtpEmail_UserNotVerified() {
        UserAuthProjection user = mock(UserAuthProjection.class);
        when(user.enabled()).thenReturn(false);
        when(userRepo.findUserAuthProjectionByEmail(email)).thenReturn(Optional.of(user));

        String result = generalReset.sendOtpEmail(email);

        assertEquals("Email not verified. Please complete sign-in.", result);
    }

    @Test
    void testSendOtpEmail_Success() throws MessagingException {
        // Mock user projection
        UserAuthProjection user = mock(UserAuthProjection.class);
        when(user.enabled()).thenReturn(true);
        when(user.email()).thenReturn(email);
        when(userRepo.findUserAuthProjectionByEmail(email)).thenReturn(Optional.of(user));

        // Mock user entity
        UserEntity userEntity = new UserEntity();
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(userEntity));

        // Mock OTP generation and saving
        ResetPasswordEntity resetPasswordEntity = new ResetPasswordEntity();
        when(resetPasswordService.generateOtp()).thenReturn(otp);
        when(resetPasswordRepo.save(any(ResetPasswordEntity.class))).thenReturn(resetPasswordEntity);

        // Create the expected email context
        ForgotPasswordWithOtp expectedContext = new ForgotPasswordWithOtp();
        expectedContext.init(user);
        expectedContext.buildVerificationOtp(otp);

        // Mock email sending
        doNothing().when(emailService).sendMail(expectedContext);

        // Call the method
        String result = generalReset.sendOtpEmail(email);

        // Assertions
        assertEquals("Success sent OTP to your email.", result);
        verify(resetPasswordRepo, times(1)).save(any(ResetPasswordEntity.class));
        verify(emailService, times(1)).sendMail(expectedContext);
    }

    @Test
    void testSendOtpEmail_EmailSendingFailed() throws MessagingException {
        // Mock user projection
        UserAuthProjection user = mock(UserAuthProjection.class);
        when(user.enabled()).thenReturn(true);
        when(user.email()).thenReturn(email);
        when(userRepo.findUserAuthProjectionByEmail(email)).thenReturn(Optional.of(user));

        // Mock user entity
        UserEntity userEntity = new UserEntity();
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(userEntity));

        // Mock OTP generation and saving
        ResetPasswordEntity resetPasswordEntity = new ResetPasswordEntity();
        when(resetPasswordService.generateOtp()).thenReturn(otp);
        when(resetPasswordRepo.save(any(ResetPasswordEntity.class))).thenReturn(resetPasswordEntity);

        // Mock email sending to throw an exception
        doThrow(MessagingException.class).when(emailService).sendMail(any(ForgotPasswordWithOtp.class));

        // Call the method
        String result = generalReset.sendOtpEmail(email);

        // Assertions
        assertEquals("Error sending OTP email. Please try again later.", result);
    }

    @Test
    void testRetrieveOrCreateOtpEntity_UserFound() {
        UserAuthProjection user = mock(UserAuthProjection.class);
        when(user.email()).thenReturn(email);

        UserEntity userEntity = new UserEntity();
        when(userRepo.findByEmail(email)).thenReturn(Optional.of(userEntity));

        ResetPasswordEntity result = generalReset.retrieveOrCreateOtpEntity(Optional.of(user), Optional.empty());

        assertNotNull(result);
        assertEquals(userEntity, result.getUser());
    }

    @Test
    void testRetrieveOrCreateOtpEntity_OrganizationFound() {
        OrgAuthProjection org = mock(OrgAuthProjection.class);
        when(org.email()).thenReturn(email);

        OrgEntity orgEntity = new OrgEntity();
        when(organizationRepo.findByEmail(email)).thenReturn(Optional.of(orgEntity));

        ResetPasswordEntity result = generalReset.retrieveOrCreateOtpEntity(Optional.empty(), Optional.of(org));

        assertNotNull(result);
        assertEquals(orgEntity, result.getOrganization());
    }

    @Test
    void testRetrieveOrCreateOtpEntity_NoUserOrOrganizationFound() {
        assertThrows(NoSuchElementException.class, () ->
                generalReset.retrieveOrCreateOtpEntity(Optional.empty(), Optional.empty()));
    }

    @Test
    void testResetPassword_TokenExpired() {
        ResetPasswordEntity resetPasswordEntity = new ResetPasswordEntity();
        resetPasswordEntity.setExpiredAt(LocalDateTime.now().minusMinutes(10));
        when(resetPasswordRepo.findByOtp(otp)).thenReturn(resetPasswordEntity);

        assertThrows(GlobalException.class, () -> generalReset.resetPassword(email, otp, newPassword));
    }

    @Test
    void testResetPassword_UserPasswordResetSuccess() {
        ResetPasswordEntity resetPasswordEntity = new ResetPasswordEntity();
        resetPasswordEntity.setExpiredAt(LocalDateTime.now().plusMinutes(10));
        UserEntity userEntity = new UserEntity();
        resetPasswordEntity.setUser(userEntity);
        when(resetPasswordRepo.findByOtp(otp)).thenReturn(resetPasswordEntity);
        when(userRepo.findById(any())).thenReturn(Optional.of(userEntity));
        when(userRepo.existsByEmail(email)).thenReturn(true);

        boolean result = generalReset.resetPassword(email, otp, newPassword);

        assertTrue(result);
        verify(userRepo, times(1)).save(userEntity);
        verify(resetPasswordService, times(1)).deleteOtp(resetPasswordEntity);
    }

    @Test
    void testResetPassword_OrganizationPasswordResetSuccess() {
        ResetPasswordEntity resetPasswordEntity = new ResetPasswordEntity();
        resetPasswordEntity.setExpiredAt(LocalDateTime.now().plusMinutes(10));
        OrgEntity orgEntity = new OrgEntity();
        resetPasswordEntity.setOrganization(orgEntity);
        when(resetPasswordRepo.findByOtp(otp)).thenReturn(resetPasswordEntity);
        when(organizationRepo.findById(any())).thenReturn(Optional.of(orgEntity));
        when(organizationRepo.existsByEmail(email)).thenReturn(true);

        boolean result = generalReset.resetPassword(email, otp, newPassword);

        assertTrue(result);
        verify(organizationRepo, times(1)).save(orgEntity);
        verify(resetPasswordService, times(1)).deleteOtp(resetPasswordEntity);
    }

    @Test
    void testResetPassword_TokenNotFound() {
        when(resetPasswordRepo.findByOtp(otp)).thenReturn(null);

        boolean result = generalReset.resetPassword(email, otp, newPassword);

        assertFalse(result);
    }

    @Test
    void testSendOtpEmail_OrganizationSuccess() throws MessagingException {
        // Mock organization projection
        OrgAuthProjection organization = mock(OrgAuthProjection.class);
        when(organization.enabled()).thenReturn(true);
        when(organization.email()).thenReturn(email);
        when(organizationRepo.findOrgAuthProjectionByEmail(email)).thenReturn(Optional.of(organization));

        // Mock organization entity
        OrgEntity orgEntity = new OrgEntity();
        when(organizationRepo.findByEmail(email)).thenReturn(Optional.of(orgEntity));

        // Mock OTP generation and saving
        ResetPasswordEntity resetPasswordEntity = new ResetPasswordEntity();
        when(resetPasswordService.generateOtp()).thenReturn(otp);
        when(resetPasswordRepo.save(any(ResetPasswordEntity.class))).thenReturn(resetPasswordEntity);

        // Create the expected email context
        ForgotPasswordWithOtp expectedContext = new ForgotPasswordWithOtp();
        expectedContext.init(organization);  // Ensuring this doesn't throw an exception
        expectedContext.buildVerificationOtp(otp);

        // Mock email sending
        doNothing().when(emailService).sendMail(expectedContext);

        // Call the method
        String result = generalReset.sendOtpEmail(email);

        // Assertions
        assertEquals("Success sent OTP to your email.", result);
        verify(resetPasswordRepo, times(1)).save(any(ResetPasswordEntity.class));
        verify(emailService, times(1)).sendMail(expectedContext);
    }
}