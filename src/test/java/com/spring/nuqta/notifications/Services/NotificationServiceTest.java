package com.spring.nuqta.notifications.Services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.spring.nuqta.notifications.Dto.NotificationRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.FileInputStream;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private FirebaseMessaging firebaseMessaging;

    @InjectMocks
    private NotificationService notificationService;

    private NotificationRequest notificationRequest;

    @BeforeEach
    void setUp() {
        notificationRequest = new NotificationRequest();
        notificationRequest.setTargetFcmToken("testToken");
        notificationRequest.setTitle("Test Title");
        notificationRequest.setMessage("Test Message");
    }

    @BeforeAll
    static void setup() throws Exception {
        if (FirebaseApp.getApps().isEmpty()) {
            FileInputStream serviceAccount = new FileInputStream("src/main/resources/firebase-service-account.json");

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            FirebaseApp.initializeApp(options);
        }
    }

    @Test
    void testSendNotification_Success() throws Exception {
        // Arrange
        NotificationRequest request = new NotificationRequest("sampleToken", "Test Title", "Test Message");

        Message message = Message.builder()
                .setToken(request.getTargetFcmToken())
                .setNotification(Notification.builder()
                        .setTitle(request.getTitle())
                        .setBody(request.getMessage())
                        .build())
                .build();


        when(firebaseMessaging.send(any(Message.class))).thenReturn("message_id_123");

        // Act
        notificationService.sendNotification(request);
        
        // Assert
        verify(firebaseMessaging, times(1)).send(any(Message.class));
    }

    @Test
    void testSendNotification_ExceptionHandling() throws Exception {
        // Arrange
        doThrow(new RuntimeException("Firebase error")).when(firebaseMessaging).send(any(Message.class));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> notificationService.sendNotification(notificationRequest));
        verify(firebaseMessaging, times(1)).send(any(Message.class));
    }


    @Test
    void testSendNotification_EmptyTitle() throws FirebaseMessagingException {
        // Arrange
        NotificationRequest request = new NotificationRequest("sampleToken", "", "Test Message");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> notificationService.sendNotification(request));
        verify(firebaseMessaging, never()).send(any(Message.class));
    }

    @Test
    void testSendNotification_EmptyMessage() throws FirebaseMessagingException {
        // Arrange
        NotificationRequest request = new NotificationRequest("sampleToken", "Test Title", "");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> notificationService.sendNotification(request));
        verify(firebaseMessaging, never()).send(any(Message.class));
    }

    @Test
    void testSendNotification_EmptyToken() throws FirebaseMessagingException {
        // Arrange
        NotificationRequest request = new NotificationRequest("", "Test Title", "Test Message");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> notificationService.sendNotification(request));
        verify(firebaseMessaging, never()).send(any(Message.class));
    }
}