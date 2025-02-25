package com.spring.nuqta.mail.template;

import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ForgotPasswordWithOtpTest {

    private ForgotPasswordWithOtp emailContext;

    @BeforeEach
    void setUp() {
        emailContext = new ForgotPasswordWithOtp();
    }

    @Test
    void testInitWithUserEntity() {
        UserEntity user = new UserEntity();
        user.setUsername("testUser");
        user.setEmail("test@example.com");

        emailContext.init(user);

        assertEquals("testUser", emailContext.getContext().get("User"));
        assertEquals("test@example.com", emailContext.getTo());
        assertEquals("mailForgotPassword", emailContext.getTemplateLocation());
        assertEquals("Password Reset", emailContext.getSubject());
        assertEquals("no-reply@Nuqta.com", emailContext.getFrom());
    }

    @Test
    void testInitWithOrgEntity() {
        OrgEntity org = new OrgEntity();
        org.setOrgName("TestOrg");
        org.setEmail("org@example.com");

        emailContext.init(org);

        assertEquals("TestOrg", emailContext.getContext().get("User"));
        assertEquals("org@example.com", emailContext.getTo());
        assertEquals("mailForgotPassword", emailContext.getTemplateLocation());
        assertEquals("Password Reset", emailContext.getSubject());
        assertEquals("no-reply@Nuqta.com", emailContext.getFrom());
    }

    @Test
    void testBuildVerificationOtp() {
        String otp = "654321";
        emailContext.buildVerificationOtp(otp);

        assertEquals("654321", emailContext.getContext().get("otp"));
    }
}
