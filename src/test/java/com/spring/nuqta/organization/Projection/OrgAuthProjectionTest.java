package com.spring.nuqta.organization.Projection;

import com.spring.nuqta.enums.Scope;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OrgAuthProjectionTest {

    @Test
    void testObjectCreation() {
        OrgAuthProjection org = new OrgAuthProjection(1L, "org@example.com", "securePass123", "orgName", Scope.ORGANIZATION, true, "LIC-98765");

        assertNotNull(org);
        assertEquals(1L, org.id());
        assertEquals("org@example.com", org.email());
        assertEquals("securePass123", org.password());
        assertEquals("LIC-98765", org.licenseNumber());
        assertEquals(Scope.ORGANIZATION, org.scope());
        assertTrue(org.enabled());
    }

    @Test
    void testEqualityAndHashCode() {
        OrgAuthProjection org1 = new OrgAuthProjection(1L, "org@example.com", "securePass123", "orgName", Scope.ORGANIZATION, true, "LIC-98765");
        OrgAuthProjection org2 = new OrgAuthProjection(1L, "org@example.com", "securePass123", "orgName", Scope.ORGANIZATION, true, "LIC-98765");
        OrgAuthProjection org3 = new OrgAuthProjection(2L, "other@example.com", "diffPass", "orgName", Scope.USER, false, "LIC-98765");

        assertEquals(org1, org2);
        assertNotEquals(org1, org3);

        assertEquals(org1.hashCode(), org2.hashCode());
        assertNotEquals(org1.hashCode(), org3.hashCode());
    }

    @Test
    void testToString() {
        OrgAuthProjection org = new OrgAuthProjection(1L, "org@example.com", "securePass123", "orgName", Scope.ORGANIZATION, true, "LIC-98765");

        String result = org.toString();
        assertTrue(result.contains("OrgAuthProjection"));
        assertTrue(result.contains("email=org@example.com"));
        assertTrue(result.contains("licenseNumber=LIC-98765"));
    }
}
