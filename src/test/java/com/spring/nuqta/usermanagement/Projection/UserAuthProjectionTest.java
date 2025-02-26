package com.spring.nuqta.usermanagement.Projection;

import com.spring.nuqta.enums.Scope;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class UserAuthProjectionTest {

    @Test
    void testObjectCreation() {
        UserAuthProjection user = new UserAuthProjection(1L, "john_doe", "john@example.com", "hashedPassword", Scope.USER, true);

        assertNotNull(user);
        assertEquals(1L, user.id());
        assertEquals("john_doe", user.username());
        assertEquals("john@example.com", user.email());
        assertEquals("hashedPassword", user.password());
        assertEquals(Scope.USER, user.scope());
        assertTrue(user.enabled());
    }

    @Test
    void testEqualityAndHashCode() {
        UserAuthProjection user1 = new UserAuthProjection(1L, "john_doe", "john@example.com", "hashedPassword", Scope.USER, true);
        UserAuthProjection user2 = new UserAuthProjection(1L, "john_doe", "john@example.com", "hashedPassword", Scope.USER, true);
        UserAuthProjection user3 = new UserAuthProjection(2L, "jane_doe", "jane@example.com", "anotherPassword", Scope.ORGANIZATION, false);

        assertEquals(user1, user2);
        assertNotEquals(user1, user3);

        assertEquals(user1.hashCode(), user2.hashCode());
        assertNotEquals(user1.hashCode(), user3.hashCode());
    }

    @Test
    void testToString() {
        UserAuthProjection user = new UserAuthProjection(1L, "john_doe", "john@example.com", "hashedPassword", Scope.USER, true);

        String expectedString = "UserAuthProjection[id=1, username=john_doe, email=john@example.com, password=hashedPassword, scope=USER, enabled=true]";
        assertTrue(user.toString().contains("UserAuthProjection"));
        assertTrue(user.toString().contains("username=john_doe"));
        assertTrue(user.toString().contains("email=john@example.com"));
    }
}
