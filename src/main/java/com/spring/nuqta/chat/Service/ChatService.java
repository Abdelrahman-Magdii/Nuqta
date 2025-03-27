package com.spring.nuqta.chat.Service;

import com.spring.nuqta.chat.Entity.ChatMessage;
import com.spring.nuqta.chat.Repo.ChatRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatRepo chatRepository;

    public ChatMessage saveMessage(ChatMessage chatMessage) {
        return chatRepository.save(chatMessage);
    }

    public List<ChatMessage> getMessagesBetweenUsers(String user1Id, String user2Id) {
        return chatRepository.findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByTimestampAsc(
                user1Id, user2Id, user2Id, user1Id);
    }

    public List<ChatMessage> getUnreadMessages(String userId) {
        return chatRepository.findByReceiverIdAndReadFalseOrderByTimestampDesc(userId);
    }

    public void markMessageAsRead(Long messageId) {
        ChatMessage message = chatRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setRead(true);
        chatRepository.save(message);
    }
} 