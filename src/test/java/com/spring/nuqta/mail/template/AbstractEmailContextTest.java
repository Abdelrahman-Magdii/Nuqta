package com.spring.nuqta.mail.template;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class AbstractEmailContextTest {

    private TestEmailContext emailContext;

    // Concrete subclass for testing
    static class TestEmailContext extends AbstractEmailContext {
        @Override
        public <T> void init(T context) {
            put("initKey", "initialized");
        }
    }

    @BeforeEach
    void setUp() {
        emailContext = new TestEmailContext();
    }

    @Test
    void testPutAndGetContext() {
        emailContext.put("key1", "value1");
        emailContext.put("key2", 42);

        assertEquals("value1", emailContext.getContext().get("key1"));
        assertEquals(42, emailContext.getContext().get("key2"));
    }

    @Test
    void testPutWithNullKey() {
        Object result = emailContext.put(null, "value");
        assertNull(result);  // Should return null since key is null
    }

    @Test
    void testInitMethod() {
        emailContext.init(null);
        assertEquals("initialized", emailContext.getContext().get("initKey"));
    }

    @Test
    void testSettersAndGetters() {
        emailContext.setFrom("no-reply@example.com");
        emailContext.setTo("user@example.com");
        emailContext.setSubject("Test Subject");
        emailContext.setEmail("user@example.com");
        emailContext.setTemplateLocation("template.html");

        assertEquals("no-reply@example.com", emailContext.getFrom());
        assertEquals("user@example.com", emailContext.getTo());
        assertEquals("Test Subject", emailContext.getSubject());
        assertEquals("user@example.com", emailContext.getEmail());
        assertEquals("template.html", emailContext.getTemplateLocation());
    }

    @Test
    void testContextInitialization() {
        assertNotNull(emailContext.getContext());
        assertTrue(emailContext.getContext() instanceof Map);
        assertTrue(emailContext.getContext().isEmpty());
    }
}
