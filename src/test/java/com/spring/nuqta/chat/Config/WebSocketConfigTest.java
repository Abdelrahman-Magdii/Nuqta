package com.spring.nuqta.chat.Config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.StompWebSocketEndpointRegistration;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = WebSocketConfig.class)
public class WebSocketConfigTest {

    @Autowired
    private WebSocketConfig webSocketConfig;

    @Test
    public void testRegisterStompEndpoints() {
        // Arrange
        StompEndpointRegistry registry = mock(StompEndpointRegistry.class);
        StompWebSocketEndpointRegistration endpointRegistration = mock(StompWebSocketEndpointRegistration.class);

        // Mock the behavior of addEndpoint to return a valid StompWebSocketEndpointRegistration
        when(registry.addEndpoint("/ws")).thenReturn(endpointRegistration);
        when(endpointRegistration.setAllowedOriginPatterns("*")).thenReturn(endpointRegistration);

        // Act
        webSocketConfig.registerStompEndpoints(registry);

        // Assert
        // Verify that addEndpoint is called twice
        verify(registry, times(2)).addEndpoint("/ws");

        // Verify that setAllowedOriginPatterns and withSockJS are called once
        verify(endpointRegistration, times(1)).setAllowedOriginPatterns("*");
        verify(endpointRegistration, times(1)).withSockJS();
    }

    @Test
    public void testConfigureMessageBroker() {
        // Arrange
        MessageBrokerRegistry registry = mock(MessageBrokerRegistry.class);

        // Act
        webSocketConfig.configureMessageBroker(registry);

        // Assert
        verify(registry, times(1)).enableSimpleBroker("/topic");
        verify(registry, times(1)).setApplicationDestinationPrefixes("/app");
    }

    @Test
    public void testOnDisconnect() {
        // Arrange
        SessionDisconnectEvent event = mock(SessionDisconnectEvent.class);
        when(event.getSessionId()).thenReturn("session-123");

        // Act
        webSocketConfig.onDisconnect(event);

        // Assert
        verify(event, times(1)).getSessionId();
        // You can also verify logging behavior if needed
    }
}