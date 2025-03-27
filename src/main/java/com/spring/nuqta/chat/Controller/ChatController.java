package com.spring.nuqta.chat.Controller;

import com.spring.nuqta.chat.Entity.ChatMessage;
import com.spring.nuqta.chat.Service.ChatService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@AllArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatService chatService;

    // WebSocket endpoints
    @MessageMapping("/chat.send")
    public void sendMessage(@Payload ChatMessage chatMessage) {
        ChatMessage savedMessage = chatService.saveMessage(chatMessage);
        messagingTemplate.convertAndSendToUser(
                chatMessage.getReceiverId(),
                "/topic/messages",
                savedMessage
        );
    }

    @MessageMapping("/chat.read")
    public void markMessageAsRead(@Payload Long messageId) {
        chatService.markMessageAsRead(messageId);
    }

    // REST API endpoints
    @GetMapping("/messages/{user1Id}/{user2Id}")
    public List<ChatMessage> getMessagesBetweenUsers(
            @PathVariable String user1Id,
            @PathVariable String user2Id) {
        return chatService.getMessagesBetweenUsers(user1Id, user2Id);
    }

    @GetMapping("/unread/{userId}")
    public List<ChatMessage> getUnreadMessages(@PathVariable String userId) {
        return chatService.getUnreadMessages(userId);
    }
}