package com.spring.nuqta.chat.Config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebSocketConfigTest {

    @Mock
    private StompEndpointRegistry stompEndpointRegistry;

    @Mock
    private StompWebSocketEndpointRegistration endpointRegistration;

    @Mock
    private MessageBrokerRegistry messageBrokerRegistry;

    @Mock
    private SessionDisconnectEvent sessionDisconnectEvent;

    @InjectMocks
    private WebSocketConfig webSocketConfig;

    @Test
    void registerStompEndpoints_shouldConfigureCorrectEndpoints() {
        // Arrange
        when(stompEndpointRegistry.addEndpoint("/ws")).thenReturn(endpointRegistration);
        when(endpointRegistration.setAllowedOriginPatterns("*")).thenReturn(endpointRegistration);

        // Act
        webSocketConfig.registerStompEndpoints(stompEndpointRegistry);

        // Assert
        verify(stompEndpointRegistry, times(2)).addEndpoint("/ws");
        verify(endpointRegistration).setAllowedOriginPatterns("*");
        verify(endpointRegistration).withSockJS();
    }

    @Test
    void configureMessageBroker_shouldConfigureBrokerCorrectly() {
        // Act
        webSocketConfig.configureMessageBroker(messageBrokerRegistry);

        // Assert
        verify(messageBrokerRegistry).setApplicationDestinationPrefixes("/app");
        verify(messageBrokerRegistry).enableSimpleBroker("/topic", "/queue");
    }

    @Test
    void onDisconnect_shouldLogSessionDisconnect() {
        // Arrange
        String sessionId = "test-session-123";
        when(sessionDisconnectEvent.getSessionId()).thenReturn(sessionId);

        // Act
        webSocketConfig.onDisconnect(sessionDisconnectEvent);

        // Assert
        // Normally we would verify logging, but since Logger is static,
        // we'll just verify the event interaction
        verify(sessionDisconnectEvent).getSessionId();
    }
}