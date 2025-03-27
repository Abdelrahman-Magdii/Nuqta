package com.spring.nuqta.chat.Repo;

import com.spring.nuqta.chat.Entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatRepo extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findBySenderIdAndReceiverIdOrReceiverIdAndSenderIdOrderByTimestampAsc(
            String senderId, String receiverId, String receiverId2, String senderId2);

    List<ChatMessage> findByReceiverIdAndReadFalseOrderByTimestampDesc(String receiverId);
} 