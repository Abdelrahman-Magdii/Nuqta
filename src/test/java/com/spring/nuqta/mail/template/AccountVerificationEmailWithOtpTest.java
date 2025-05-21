package com.spring.nuqta.mail.template;

import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountVerificationEmailWithOtpTest {

    private AccountVerificationEmailWithOtp emailContext;

    @BeforeEach
    void setUp() {
        emailContext = new AccountVerificationEmailWithOtp();
    }

    @Test
    void testInitWithUserEntity() {
        UserEntity user = new UserEntity();
        user.setUsername("testUser");
        user.setEmail("test@example.com");

        emailContext.init(user);

        assertEquals("testUser", emailContext.getContext().get("User"));
        assertEquals("test@example.com", emailContext.getTo());
        assertEquals("mailOtp", emailContext.getTemplateLocation());
        assertEquals("Complete your registration", emailContext.getSubject());
        assertEquals("Nuqta.help@gmail.com", emailContext.getFrom());
    }

    @Test
    void testInitWithOrgEntity() {
        OrgEntity org = new OrgEntity();
        org.setOrgName("TestOrg");
        org.setEmail("org@example.com");

        emailContext.init(org);

        assertEquals("TestOrg", emailContext.getContext().get("User"));
        assertEquals("org@example.com", emailContext.getTo());
        assertEquals("mailOtp", emailContext.getTemplateLocation());
        assertEquals("Complete your registration", emailContext.getSubject());
        assertEquals("Nuqta.help@gmail.com", emailContext.getFrom());
    }

    @Test
    void testBuildVerificationOtp() {
        String otp = "123456";
        emailContext.buildVerificationOtp(otp);

        assertEquals("123456", emailContext.getContext().get("otp"));
    }
}
