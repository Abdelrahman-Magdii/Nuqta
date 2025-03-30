package com.spring.nuqta.exception;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.WebRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    @Mock
    public MessageSource messageSource;

    GlobalExceptionHandler exceptionHandler = new GlobalExceptionHandler(messageSource);

    @Test
    void testHandleGlobalException() {
        // Mock WebRequest
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("Request Details");

        // Create custom exception
        GlobalException globalException = new GlobalException("Custom Error", HttpStatus.BAD_REQUEST);

        // Call exception handler
        ResponseEntity<?> response = exceptionHandler.handleGlobalException(globalException, request);

        // Assertions
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorDetails);

        ErrorDetails errorDetails = (ErrorDetails) response.getBody();
        assertEquals("Custom Error", errorDetails.getMessage());
        assertEquals("Request Details", errorDetails.getDetails());
        assertNotNull(errorDetails.getTimestamp());
    }

    @Test
    void testHandleGenericException() {
        // Mock WebRequest
        WebRequest request = mock(WebRequest.class);
        when(request.getDescription(false)).thenReturn("Request Details");

        // Create generic exception
        Exception genericException = new Exception("Internal Server Error");

        // Call exception handler
        ResponseEntity<?> response = exceptionHandler.handleGenericException(genericException, request);

        // Assertions
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody() instanceof ErrorDetails);

        ErrorDetails errorDetails = (ErrorDetails) response.getBody();
        assertEquals("Internal Server Error", errorDetails.getMessage());
        assertEquals("Request Details", errorDetails.getDetails());
        assertNotNull(errorDetails.getTimestamp());
    }
}
