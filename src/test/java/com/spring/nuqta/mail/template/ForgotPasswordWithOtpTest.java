package com.spring.nuqta.mail.template;

import com.spring.nuqta.organization.Projection.OrgAuthProjection;
import com.spring.nuqta.usermanagement.Projection.UserAuthProjection;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ForgotPasswordWithOtpTest {

    @InjectMocks
    private ForgotPasswordWithOtp forgotPasswordWithOtp;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testInitWithUserAuthProjection() {
        // Arrange
        UserAuthProjection user = mock(UserAuthProjection.class);
        when(user.username()).thenReturn("testUser");
        when(user.email()).thenReturn("testUser@example.com");

        // Act
        forgotPasswordWithOtp.init(user);

        // Assert
        assertEquals("testUser", forgotPasswordWithOtp.getContext().get("User"));
        assertEquals("testUser@example.com", forgotPasswordWithOtp.getTo());
        assertEquals("mailForgotPassword", forgotPasswordWithOtp.getTemplateLocation());
        assertEquals("Password Reset", forgotPasswordWithOtp.getSubject());
        assertEquals("no-reply@Nuqta.com", forgotPasswordWithOtp.getFrom());
    }

    @Test
    public void testInitWithOrgAuthProjection() {
        // Arrange
        OrgAuthProjection org = mock(OrgAuthProjection.class);
        when(org.orgName()).thenReturn("testOrg");
        when(org.email()).thenReturn("testOrg@example.com");

        // Act
        forgotPasswordWithOtp.init(org);

        // Assert
        assertEquals("testOrg", forgotPasswordWithOtp.getContext().get("User"));
        assertEquals("testOrg@example.com", forgotPasswordWithOtp.getTo());
        assertEquals("mailForgotPassword", forgotPasswordWithOtp.getTemplateLocation());
        assertEquals("Password Reset", forgotPasswordWithOtp.getSubject());
        assertEquals("no-reply@Nuqta.com", forgotPasswordWithOtp.getFrom());
    }

    @Test
    public void testBuildVerificationOtp() {
        // Arrange
        String otp = "123456";

        // Act
        forgotPasswordWithOtp.buildVerificationOtp(otp);

        // Assert
        assertEquals("123456", forgotPasswordWithOtp.getContext().get("otp"));
    }
}