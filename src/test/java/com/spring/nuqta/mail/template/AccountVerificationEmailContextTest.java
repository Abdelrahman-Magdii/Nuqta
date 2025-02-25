package com.spring.nuqta.mail.template;

import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AccountVerificationEmailContextTest {

    private AccountVerificationEmailContext emailContext;

    @BeforeEach
    void setUp() {
        emailContext = new AccountVerificationEmailContext();
    }

    @Test
    void testInitWithUserEntity() {
        UserEntity user = new UserEntity();
        user.setUsername("testUser");
        user.setEmail("test@example.com");

        emailContext.init(user);

        assertEquals("testUser", emailContext.getContext().get("User"));
        assertEquals("test@example.com", emailContext.getTo());
        assertEquals("mail", emailContext.getTemplateLocation());
        assertEquals("Complete your registration", emailContext.getSubject());
        assertEquals("no-reply@Nuqta.com", emailContext.getFrom());
    }

    @Test
    void testInitWithOrgEntity() {
        OrgEntity org = new OrgEntity();
        org.setOrgName("NuqtaOrg");
        org.setEmail("org@example.com");

        emailContext.init(org);

        assertEquals("NuqtaOrg", emailContext.getContext().get("User"));
        assertEquals("org@example.com", emailContext.getTo());
    }

    @Test
    void testSetToken() {
        String token = "123456";
        emailContext.setToken(token);
        assertEquals(token, emailContext.getContext().get("token"));
    }

    @Test
    void testBuildVerificationUrl() {
        String baseURL = "http://localhost:8080";
        String token = "abc123";
        String email = "user@example.com";

        emailContext.buildVerificationUrl(baseURL, token, email);

        String expectedUrl = "http://localhost:8080/api/auth/verify?token=abc123&mail=user@example.com";
        assertEquals(expectedUrl, emailContext.getContext().get("verificationURL"));
    }
}
