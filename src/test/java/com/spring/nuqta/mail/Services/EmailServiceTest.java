package com.spring.nuqta.mail.Services;

import com.spring.nuqta.mail.template.AbstractEmailContext;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender emailSender;

    @Mock
    private SpringTemplateEngine templateEngine;

    @Mock
    private AbstractEmailContext emailContext;

    @InjectMocks
    private EmailService emailService;

    private MimeMessage mimeMessage;

    @BeforeEach
    public void setUp() {
        // Mock behavior for emailContext
        when(emailContext.getTo()).thenReturn("recipient@example.com");
        when(emailContext.getSubject()).thenReturn("Test Subject");
        when(emailContext.getFrom()).thenReturn("sender@example.com");
        when(emailContext.getTemplateLocation()).thenReturn("templateLocation");
        when(emailContext.getContext()).thenReturn(new HashMap<>() {{
            put("key", "value");
        }});

        mimeMessage = mock(MimeMessage.class);
        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);
    }

    @Test
    public void testSendMail() throws Exception {
        // Arrange
        when(templateEngine.process(eq("templateLocation"), any(Context.class))).thenReturn("Email Content");

        // Act
        emailService.sendMail(emailContext);

        // Assert
        verify(emailSender, times(1)).send(mimeMessage);
        verify(templateEngine, times(1)).process(eq("templateLocation"), any(Context.class));
    }

    @Test
    public void testSendMailWithMailException() {
        // Arrange
        when(templateEngine.process(eq("templateLocation"), any(Context.class))).thenReturn("Email Content");
        doThrow(new MailSendException("Failed to send email")).when(emailSender).send(mimeMessage);

        // Act & Assert
        assertThrows(MailException.class, () -> {
            emailService.sendMail(emailContext);
        });

        verify(emailSender, times(1)).send(mimeMessage);
        verify(templateEngine, times(1)).process(eq("templateLocation"), any(Context.class));
    }
}