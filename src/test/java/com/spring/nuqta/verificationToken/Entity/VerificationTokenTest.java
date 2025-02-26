package com.spring.nuqta.verificationToken.Entity;

import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class VerificationTokenTest {

    @Test
    void testTokenCreation() {
        VerificationToken token = new VerificationToken();
        token.setToken("testToken");
        token.setExpiredAt(LocalDateTime.now().plusMinutes(10)); // 10 min validity

        assertThat(token.getToken()).isEqualTo("testToken");
        assertThat(token.getExpiredAt()).isNotNull();
    }

    @Test
    void testTokenNotExpired() {
        VerificationToken token = new VerificationToken();
        token.setExpiredAt(LocalDateTime.now().plusMinutes(5));

        assertThat(token.isExpired()).isFalse();
    }

    @Test
    void testTokenExpired() {
        VerificationToken token = new VerificationToken();
        token.setExpiredAt(LocalDateTime.now().minusMinutes(5));

        assertThat(token.isExpired()).isTrue();
    }

    @Test
    void testTokenWithNullExpiration() {
        VerificationToken token = new VerificationToken();
        token.setExpiredAt(null);

        assertThat(token.isExpired()).isFalse();
    }

    @Test
    void testUserAssociation() {
        UserEntity user = new UserEntity();
        user.setId(1L);
        user.setEmail("user@example.com");

        VerificationToken token = new VerificationToken();
        token.setUser(user);

        assertThat(token.getUser()).isNotNull();
        assertThat(token.getUser().getEmail()).isEqualTo("user@example.com");
    }

    @Test
    void testOrganizationAssociation() {
        OrgEntity org = new OrgEntity();
        org.setId(2L);
        org.setEmail("org@example.com");

        VerificationToken token = new VerificationToken();
        token.setOrganization(org);

        assertThat(token.getOrganization()).isNotNull();
        assertThat(token.getOrganization().getEmail()).isEqualTo("org@example.com");
    }
}
