package com.spring.nuqta.notifications.Services;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.spring.nuqta.notifications.Dto.NotificationRequest;
import org.springframework.stereotype.Service;

@Service
public class NotificationService {

    private FirebaseMessaging firebaseMessaging;


    public void sendNotification(NotificationRequest request) {
        // Validate input
        if (request.getTitle() == null || request.getTitle().isEmpty()) {
            throw new IllegalArgumentException("Title cannot be empty");
        }
        if (request.getMessage() == null || request.getMessage().isEmpty()) {
            throw new IllegalArgumentException("Message cannot be empty");
        }
        if (request.getTargetFcmToken() == null || request.getTargetFcmToken().isEmpty()) {
            throw new IllegalArgumentException("FCM token cannot be empty");
        }

        // Build and send the message
        Message message = Message.builder()
                .setToken(request.getTargetFcmToken())
                .setNotification(Notification.builder()
                        .setTitle(request.getTitle())
                        .setBody(request.getMessage())
                        .build())
                .build();

        try {
            firebaseMessaging.send(message);
        } catch (FirebaseMessagingException e) {
            throw new RuntimeException("Failed to send notification", e);
        }
    }
}
