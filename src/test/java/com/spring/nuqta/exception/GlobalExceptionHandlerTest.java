package com.spring.nuqta.exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    private final Locale testLocale = Locale.ENGLISH;
    private final String requestDescription = "test request description";
    private final String errorMessageCode = "error.code";
    private final String localizedMessage = "Localized error message";
    private final String rawMessage = "Raw error message";
    @Mock
    private MessageSource messageSource;
    @Mock
    private WebRequest webRequest;
    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        LocaleContextHolder.setLocale(testLocale);
        when(webRequest.getDescription(false)).thenReturn(requestDescription);
    }

    @Test
    void handleGlobalException_ShouldReturnLocalizedMessage_WhenMessageCodeExists() {
        // Arrange
        GlobalException ex = new GlobalException(errorMessageCode, HttpStatus.BAD_REQUEST);
        when(messageSource.getMessage(eq(errorMessageCode), isNull(), eq(testLocale)))
                .thenReturn(localizedMessage);

        // Act
        ResponseEntity<?> response = globalExceptionHandler.handleGlobalException(ex, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorDetails errorDetails = (ErrorDetails) response.getBody();
        assertNotNull(errorDetails);
        assertEquals(localizedMessage, errorDetails.getMessage());
        assertEquals(requestDescription, errorDetails.getDetails());
        assertNotNull(errorDetails.getTimestamp());
    }

    @Test
    void handleGlobalException_ShouldReturnRawMessage_WhenMessageCodeNotFound() {
        // Arrange
        GlobalException ex = new GlobalException(errorMessageCode, HttpStatus.BAD_REQUEST);
        when(messageSource.getMessage(eq(errorMessageCode), isNull(), eq(testLocale)))
                .thenThrow(new NoSuchMessageException(errorMessageCode));

        // Act
        ResponseEntity<?> response = globalExceptionHandler.handleGlobalException(ex, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorDetails errorDetails = (ErrorDetails) response.getBody();
        assertNotNull(errorDetails);
        assertEquals(errorMessageCode, errorDetails.getMessage());
        assertEquals(requestDescription, errorDetails.getDetails());
    }

    @Test
    void handleGlobalException_ShouldHandleNullMessage() {
        // Arrange
        GlobalException ex = new GlobalException(null, HttpStatus.BAD_REQUEST);

        // Act
        ResponseEntity<?> response = globalExceptionHandler.handleGlobalException(ex, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

        ErrorDetails errorDetails = (ErrorDetails) response.getBody();
        assertNotNull(errorDetails);
        assertNull(errorDetails.getMessage());
    }

    @Test
    void handleGenericException_ShouldReturnInternalServerError() {
        // Arrange
        Exception ex = new Exception(rawMessage);

        // Act
        ResponseEntity<?> response = globalExceptionHandler.handleGenericException(ex, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        ErrorDetails errorDetails = (ErrorDetails) response.getBody();
        assertNotNull(errorDetails);
        assertEquals(rawMessage, errorDetails.getMessage());
        assertEquals(requestDescription, errorDetails.getDetails());
        assertNotNull(errorDetails.getTimestamp());
    }

    @Test
    void handleGenericException_ShouldHandleNullMessage() {
        // Arrange
        Exception ex = new Exception();

        // Act
        ResponseEntity<?> response = globalExceptionHandler.handleGenericException(ex, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        ErrorDetails errorDetails = (ErrorDetails) response.getBody();
        assertNotNull(errorDetails);
        assertNull(errorDetails.getMessage());
    }

    @Test
    void handleGenericException_ShouldHandleNestedExceptionMessage() {
        // Arrange
        Exception cause = new Exception("Root cause");
        Exception ex = new Exception("Wrapper exception", cause);

        // Act
        ResponseEntity<?> response = globalExceptionHandler.handleGenericException(ex, webRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());

        ErrorDetails errorDetails = (ErrorDetails) response.getBody();
        assertNotNull(errorDetails);
        assertEquals("Wrapper exception", errorDetails.getMessage());
    }
}