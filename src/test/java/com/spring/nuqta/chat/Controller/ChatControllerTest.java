package com.spring.nuqta.chat.Controller;

import com.spring.nuqta.chat.Entity.ChatMessage;
import com.spring.nuqta.chat.Service.ChatService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    private final String user1Id = "user1";
    private final String user2Id = "user2";
    private final String userId = "user1";
    private final Long messageId = 1L;
    @Mock
    private SimpMessagingTemplate messagingTemplate;
    @Mock
    private ChatService chatService;
    @InjectMocks
    private ChatController chatController;
    private ChatMessage chatMessage;

    @BeforeEach
    void setUp() {
        chatMessage = new ChatMessage();
        chatMessage.setId(messageId);
        chatMessage.setSenderId(user1Id);
        chatMessage.setReceiverId(user2Id);
        chatMessage.setContent("Hello");
        chatMessage.setRead(false);
    }

    @Test
    void sendMessage_ShouldSaveAndSendMessage() {
        // Arrange
        when(chatService.saveMessage(any(ChatMessage.class))).thenReturn(chatMessage);

        // Act
        chatController.sendMessage(chatMessage);

        // Assert
        verify(chatService).saveMessage(chatMessage);
        verify(messagingTemplate).convertAndSendToUser(
                eq(user2Id),
                eq("/topic/messages"),
                eq(chatMessage)
        );
    }

    @Test
    void markMessageAsRead_ShouldCallService() {
        // Act
        chatController.markMessageAsRead(messageId);

        // Assert
        verify(chatService).markMessageAsRead(messageId);
    }

    @Test
    void getMessagesBetweenUsers_ShouldReturnMessages() {
        // Arrange
        List<ChatMessage> expectedMessages = Arrays.asList(chatMessage);
        when(chatService.getMessagesBetweenUsers(user1Id, user2Id)).thenReturn(expectedMessages);

        // Act
        List<ChatMessage> result = chatController.getMessagesBetweenUsers(user1Id, user2Id);

        // Assert
        assertEquals(expectedMessages, result);
        verify(chatService).getMessagesBetweenUsers(user1Id, user2Id);
    }

    @Test
    void getUnreadMessages_ShouldReturnUnreadMessages() {
        // Arrange
        List<ChatMessage> expectedMessages = Arrays.asList(chatMessage);
        when(chatService.getUnreadMessages(userId)).thenReturn(expectedMessages);

        // Act
        List<ChatMessage> result = chatController.getUnreadMessages(userId);

        // Assert
        assertEquals(expectedMessages, result);
        verify(chatService).getUnreadMessages(userId);
    }
}