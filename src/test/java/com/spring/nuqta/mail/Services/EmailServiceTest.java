package com.spring.nuqta.mail.Services;

import com.spring.nuqta.mail.template.AbstractEmailContext;
import jakarta.mail.MessagingException;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        // Ensure emailContext provides non-null values
        lenient().when(emailContext.getTo()).thenReturn("recipient@example.com");
        lenient().when(emailContext.getSubject()).thenReturn("Test Subject");
        lenient().when(emailContext.getFrom()).thenReturn("sender@example.com");
        lenient().when(emailContext.getTemplateLocation()).thenReturn("templateLocation");
        lenient().when(emailContext.getContext()).thenReturn(new HashMap<>() {{
            put("key", "value");
        }});

        mimeMessage = mock(MimeMessage.class);
        when(emailSender.createMimeMessage()).thenReturn(mimeMessage);

        // Ensure templateEngine returns valid email content
        lenient().when(templateEngine.process(eq("templateLocation"), any(Context.class)))
                .thenReturn("<html><body>Email Content</body></html>");
    }


    @Test
    public void testSendMail() throws MessagingException {
        // Act
        emailService.sendMail(emailContext);

        // Assert
        verify(emailSender, times(1)).send(mimeMessage);
        verify(templateEngine, times(1)).process(eq("templateLocation"), any(Context.class));
    }

    @Test
    public void testSendMailWithMailException() {
        // Arrange: Simulate email send failure
        doThrow(new MailSendException("Failed to send email")).when(emailSender).send(mimeMessage);

        // Act & Assert
        assertThrows(MailException.class, () -> emailService.sendMail(emailContext));

        verify(emailSender, times(1)).send(mimeMessage);
        verify(templateEngine, times(1)).process(eq("templateLocation"), any(Context.class));
    }

    @Test
    void sendMail_ThrowsIllegalArgumentException_WhenTemplateIsNull() {
        // Arrange: Simulate null email content from template engine
        when(templateEngine.process(eq("templateLocation"), any(Context.class))).thenReturn(null);

        // Act & Assert: Expect IllegalArgumentException
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> emailService.sendMail(emailContext));

        assertEquals("Text must not be null", exception.getMessage());
        verify(emailSender, never()).send(any(MimeMessage.class)); // Ensure email is not sent
    }

    @Test
    void sendMail_ThrowsMailException_WhenSendFails() {
        // Arrange: Simulate mail send failure
        doThrow(new MailSendException("SMTP error")).when(emailSender).send(any(MimeMessage.class));

        // Act & Assert
        MailException exception = assertThrows(MailException.class, () -> emailService.sendMail(emailContext));

        assertEquals("SMTP error", exception.getMessage());
        verify(emailSender).send(mimeMessage);
    }
}
