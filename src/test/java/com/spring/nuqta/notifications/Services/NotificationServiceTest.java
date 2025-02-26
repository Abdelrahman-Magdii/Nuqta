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

import static org.junit.jupiter.api.Assertions.*;
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


    // ✅ Test: Title validation
    @Test
    void testSendNotification_TitleIsEmpty_ShouldThrowException() {
        NotificationRequest request = new NotificationRequest("", "Message", "token");
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            notificationService.sendNotification(request);
        });

        assertEquals("FCM token cannot be empty", thrown.getMessage());
    }

    // ✅ Test: Message validation
    @Test
    void testSendNotification_MessageIsEmpty_ShouldThrowException() {
        NotificationRequest request = new NotificationRequest("Title", "", "token");
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            notificationService.sendNotification(request);
        });

        assertEquals("Title cannot be empty", thrown.getMessage());
    }

    // ✅ Test: FCM token validation
    @Test
    void testSendNotification_TokenIsEmpty_ShouldThrowException() {
        NotificationRequest request = new NotificationRequest("Title", "Message", "");
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            notificationService.sendNotification(request);
        });

        assertEquals("Message cannot be empty", thrown.getMessage());
    }


    // ✅ Test: Successful notification sending
    @Test
    void testSendNotification_SuccessfulSend_ShouldReturnMessageString() throws FirebaseMessagingException {
        // Mock Firebase response
        String mockMessageId = "mock-message-id";
        when(firebaseMessaging.send(any())).thenReturn(mockMessageId);

        // Call method under test
        String response = notificationService.sendNotification(notificationRequest);

        // Log the response to debug
        System.out.println("Response from sendNotification: " + response);

        // Check if response is not null and matches the expected mock message ID
        assertNotNull(response, "Response should not be null");
        assertEquals(mockMessageId, response, "Response should match the mocked message ID");

        // Verify that send() was called exactly once
        verify(firebaseMessaging, times(1)).send(any());
    }


}