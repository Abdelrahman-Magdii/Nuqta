package com.spring.nuqta.chat.Entity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ChatMessageTest {

    private ChatMessage chatMessage;

    @BeforeEach
    void setUp() {
        chatMessage = new ChatMessage();
    }

    @Test
    void testEntityAttributes() {
        // Set values
        chatMessage.setId(1L);
        chatMessage.setSenderId("user1");
        chatMessage.setReceiverId("user2");
        chatMessage.setContent("Hello there!");
        chatMessage.setRead(true);

        // Verify values
        assertEquals(1L, chatMessage.getId());
        assertEquals("user1", chatMessage.getSenderId());
        assertEquals("user2", chatMessage.getReceiverId());
        assertEquals("Hello there!", chatMessage.getContent());
        assertTrue(chatMessage.isRead());
    }

    @Test
    void testTimestampAutoGeneration() {
        // Before save
        assertNull(chatMessage.getTimestamp());

        // Simulate @PrePersist
        chatMessage.onCreate();

        // After save
        assertNotNull(chatMessage.getTimestamp());
        assertTrue(chatMessage.getTimestamp().isBefore(LocalDateTime.now().plusSeconds(1)) ||
                chatMessage.getTimestamp().isAfter(LocalDateTime.now().minusSeconds(1)));
    }

    @Test
    void testReadStatusDefaultValue() {
        // Before save
        assertFalse(chatMessage.isRead());

        // Simulate @PrePersist
        chatMessage.onCreate();

        // After save (should still be false as it's the default)
        assertFalse(chatMessage.isRead());
    }

    @Test
    void testEntityEquality() {
        ChatMessage message1 = new ChatMessage();
        message1.setId(1L);
        message1.setSenderId("user1");
        message1.setReceiverId("user2");
        message1.setContent("Hello");

        ChatMessage message2 = new ChatMessage();
        message2.setId(1L);
        message2.setSenderId("user1");
        message2.setReceiverId("user2");
        message2.setContent("Hello");

        assertEquals(message1, message2);
        assertEquals(message1.hashCode(), message2.hashCode());
    }

    @Test
    void testEntityInequality() {
        ChatMessage message1 = new ChatMessage();
        message1.setId(1L);

        ChatMessage message2 = new ChatMessage();
        message2.setId(2L);

        assertNotEquals(message1, message2);
        assertNotEquals(message1.hashCode(), message2.hashCode());
    }

    @Test
    void testToString() {
        chatMessage.setId(1L);
        chatMessage.setSenderId("user1");
        chatMessage.setReceiverId("user2");
        chatMessage.setContent("Test message");

        String toStringResult = chatMessage.toString();
        assertTrue(toStringResult.contains("ChatMessage"));
        assertTrue(toStringResult.contains("id=1"));
        assertTrue(toStringResult.contains("senderId=user1"));
        assertTrue(toStringResult.contains("receiverId=user2"));
        assertTrue(toStringResult.contains("content=Test message"));
    }
}