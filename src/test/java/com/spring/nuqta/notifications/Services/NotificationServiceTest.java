package com.spring.nuqta.notifications.Services;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.spring.nuqta.exception.GlobalException;
import com.spring.nuqta.notifications.Dto.NotificationRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private FirebaseMessaging firebaseMessaging;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void sendNotification_ValidRequest_ReturnsMessageId() throws Exception {
        NotificationRequest request = new NotificationRequest("Title", "Message", "token");
        when(firebaseMessaging.send(any())).thenReturn("message-id");

        String result = notificationService.sendNotification(request);
        assertEquals("message-id", result);
    }

    @Test
    void sendNotification_EmptyTitle_ThrowsLast() {
        // Service checks title last
        NotificationRequest request = new NotificationRequest("valid-token", "", "valid-message");
        GlobalException exception = assertThrows(GlobalException.class,
                () -> notificationService.sendNotification(request));
        assertEquals("notification.title.empty", exception.getMessage());
    }

    @Test
    void sendNotification_FirebaseFailure_ThrowsException() throws Exception {
        NotificationRequest request = new NotificationRequest("Title", "Message", "token");
        FirebaseMessagingException mockException = mock(FirebaseMessagingException.class);
        when(mockException.getMessage()).thenReturn("Firebase error");
        when(firebaseMessaging.send(any())).thenThrow(mockException);

        GlobalException exception = assertThrows(GlobalException.class,
                () -> notificationService.sendNotification(request));

        assertEquals("notification.failed.send", exception.getMessage());
    }

    @Test
    void sendNotification_AllFieldsNull_ThrowsTitleExceptionFirst() {
        NotificationRequest request = new NotificationRequest(null, null, null);

        GlobalException exception = assertThrows(GlobalException.class,
                () -> notificationService.sendNotification(request));

        assertEquals("notification.title.empty", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void sendNotification_AllFieldsEmpty_ThrowsTitleExceptionFirst() {
        NotificationRequest request = new NotificationRequest("", "", "");

        GlobalException exception = assertThrows(GlobalException.class,
                () -> notificationService.sendNotification(request));

        assertEquals("notification.title.empty", exception.getMessage());
    }

    @Test
    void sendNotification_NullTitle_ThrowsException() {
        NotificationRequest request = new NotificationRequest("token", null, "message");

        GlobalException exception = assertThrows(GlobalException.class,
                () -> notificationService.sendNotification(request));

        assertEquals("notification.title.empty", exception.getMessage());
    }

    @Test
    void sendNotification_EmptyTitle_ThrowsException() {
        NotificationRequest request = new NotificationRequest("token", "", "message");

        GlobalException exception = assertThrows(GlobalException.class,
                () -> notificationService.sendNotification(request));

        assertEquals("notification.title.empty", exception.getMessage());
    }

    @Test
    void sendNotification_NullMessage_ThrowsException() {
        // Valid title, null message, valid token
        NotificationRequest request = new NotificationRequest("token", "title", null);

        GlobalException exception = assertThrows(GlobalException.class,
                () -> notificationService.sendNotification(request));

        assertEquals("notification.message.empty", exception.getMessage());
    }

    @Test
    void sendNotification_EmptyMessage_ThrowsException() {
        // Valid title, empty message, valid token
        NotificationRequest request = new NotificationRequest("token", "title", "");

        GlobalException exception = assertThrows(GlobalException.class,
                () -> notificationService.sendNotification(request));

        assertEquals("notification.message.empty", exception.getMessage());
    }

    @Test
    void sendNotification_NullToken_ThrowsException() {
        // Valid title and message, null token
        NotificationRequest request = new NotificationRequest(null, "title", "message");

        GlobalException exception = assertThrows(GlobalException.class,
                () -> notificationService.sendNotification(request));

        assertEquals("notification.fcm.empty", exception.getMessage());
    }

    @Test
    void sendNotification_EmptyToken_ThrowsException() {
        // Valid title and message, empty token
        NotificationRequest request = new NotificationRequest("", "title", "message");

        GlobalException exception = assertThrows(GlobalException.class,
                () -> notificationService.sendNotification(request));

        assertEquals("notification.fcm.empty", exception.getMessage());
    }

    @Test
    void sendNotification_ValidRequest_DoesNotThrow() throws Exception {
        NotificationRequest request = new NotificationRequest("title", "message", "token");
        when(firebaseMessaging.send(any())).thenReturn("message-id");

        assertDoesNotThrow(() -> notificationService.sendNotification(request));
    }

}