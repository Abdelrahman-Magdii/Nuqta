package com.spring.nuqta.exception;

import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.Locale;

//* Global exception handler with localization support
@RestControllerAdvice
@Hidden
@AllArgsConstructor
public class GlobalExceptionHandler {

    private final MessageSource messageSource;

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<?> handleGlobalException(GlobalException ex, WebRequest request) {
        Locale locale = LocaleContextHolder.getLocale();

        String localizedMessage;
        try {
            // Try to resolve as message code first
            localizedMessage = messageSource.getMessage(ex.getMessage(), null, locale);
        } catch (NoSuchMessageException e) {
            // Fallback to raw message if no code found
            localizedMessage = ex.getMessage();
        }

        ErrorDetails errorDetails = new ErrorDetails(new Date(), localizedMessage, request.getDescription(false));
        return ResponseEntity.status(ex.getStatus()).body(errorDetails);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(new Date(), ex.getMessage(), request.getDescription(false));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDetails);
    }
}