package com.spring.nuqta.notifications.Services;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.spring.nuqta.exception.GlobalException;
import com.spring.nuqta.notifications.Dto.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {

    private final FirebaseMessaging firebaseMessaging;

    public NotificationService(FirebaseMessaging firebaseMessaging) {
        this.firebaseMessaging = firebaseMessaging;
    }

    public String sendNotification(NotificationRequest request) {
        // Validate input
        if (request.getTitle().isEmpty()) {
            throw new GlobalException("notification.title.empty", HttpStatus.BAD_REQUEST);
        }
        if (request.getMessage().isEmpty()) {
            throw new GlobalException("notification.message.empty", HttpStatus.BAD_REQUEST);
        }
        if (request.getTargetFcmToken().isEmpty()) {
            throw new GlobalException("notification.fcm.empty", HttpStatus.BAD_REQUEST);
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
            String messageId = firebaseMessaging.send(message);
            return messageId;
        } catch (FirebaseMessagingException e) {
            log.error("Failed to send notification" + e.getMessage());
            throw new GlobalException("notification.failed.send", HttpStatus.BAD_REQUEST);

        }
    }
}
