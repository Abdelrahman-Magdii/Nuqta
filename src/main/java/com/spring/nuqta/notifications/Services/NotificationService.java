package com.spring.nuqta.notifications.Services;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.spring.nuqta.notifications.Dto.NotificationRequest;
import org.springframework.stereotype.Service;
    

@Service
public class NotificationService {

    public void sendNotification(NotificationRequest request) {
        try {
            Message message = Message.builder()
                    .setToken(request.getTargetFcmToken())
                    .setNotification(Notification.builder()
                            .setTitle(request.getTitle())
                            .setBody(request.getMessage())
                            .build())
                    .build();

            FirebaseMessaging.getInstance().send(message);
            System.out.println("Notification sent successfully");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
