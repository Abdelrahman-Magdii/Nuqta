package com.spring.nuqta.chat.Service;

import com.spring.nuqta.chat.Entity.ChatMessage;
import com.spring.nuqta.chat.Repo.ChatRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    private final String user1Id = "user1";
    private final String user2Id = "user2";
    private final Long messageId = 1L;
    @Mock
    private ChatRepo chatRepository;
    @InjectMocks
    private ChatService chatService;
    private ChatMessage chatMessage1;
    private ChatMessage chatMessage2;

    @BeforeEach
    void setUp() {
        chatMessage1 = new ChatMessage();
        chatMessage1.setId(1L);
        chatMessage1.setSenderId(user1Id);
        chatMessage1.setReceiverId(user2Id);
        chatMessage1.setContent("Hello");
        chatMessage1.setRead(false);

        chatMessage2 = new ChatMessage();
        chatMessage2.setId(2L);
        chatMessage2.setSenderId(user2Id);
        chatMessage2.setReceiverId(user1Id);
        chatMessage2.setContent("Hi there");
        chatMessage2.setRead(true);
    }

    @Test
    void saveMessage_ShouldReturnSavedMessage() {
        // Arrange
        when(chatRepository.save(any(ChatMessage.class))).thenReturn(chatMessage1);

        // Act
        ChatMessage result = chatService.saveMessage(chatMessage1);

        // Assert
        assertNotNull(result);
        assertEquals(chatMessage1, result);
        verify(chatRepository).save(chatMessage1);
    }

    @Test
    void getMessagesBetweenUsers_ShouldReturnConversation() {
        // Arrange
        List<ChatMessage> expectedMessages = Arrays.asList(chatMessage1, chatMessage2);
        when(chatRepository.findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByTimestampAsc(
                user1Id, user2Id, user2Id, user1Id))
                .thenReturn(expectedMessages);

        // Act
        List<ChatMessage> result = chatService.getMessagesBetweenUsers(user1Id, user2Id);

        // Assert
        assertEquals(2, result.size());
        assertTrue(result.containsAll(expectedMessages));
        verify(chatRepository).findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByTimestampAsc(
                user1Id, user2Id, user2Id, user1Id);
    }

    @Test
    void getUnreadMessages_ShouldReturnOnlyUnreadMessages() {
        // Arrange
        List<ChatMessage> unreadMessages = Arrays.asList(chatMessage1);
        when(chatRepository.findByReceiverIdAndReadFalseOrderByTimestampDesc(user1Id))
                .thenReturn(unreadMessages);

        // Act
        List<ChatMessage> result = chatService.getUnreadMessages(user1Id);

        // Assert
        assertEquals(1, result.size());
        assertEquals(chatMessage1, result.get(0));
        verify(chatRepository).findByReceiverIdAndReadFalseOrderByTimestampDesc(user1Id);
    }

    @Test
    void markMessageAsRead_ShouldUpdateMessageStatus() {
        // Arrange
        ChatMessage unreadMessage = new ChatMessage();
        unreadMessage.setId(messageId);
        unreadMessage.setRead(false);

        ChatMessage readMessage = new ChatMessage();
        readMessage.setId(messageId);
        readMessage.setRead(true);

        when(chatRepository.findById(messageId)).thenReturn(Optional.of(unreadMessage));
        when(chatRepository.save(any(ChatMessage.class))).thenReturn(readMessage);

        // Act
        chatService.markMessageAsRead(messageId);

        // Assert
        verify(chatRepository).findById(messageId);
        verify(chatRepository).save(argThat(message ->
                message.getId().equals(messageId) && message.isRead()));
    }

    @Test
    void markMessageAsRead_ShouldThrowExceptionWhenMessageNotFound() {
        // Arrange
        when(chatRepository.findById(messageId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            chatService.markMessageAsRead(messageId);
        });
        verify(chatRepository).findById(messageId);
        verify(chatRepository, never()).save(any());
    }
}