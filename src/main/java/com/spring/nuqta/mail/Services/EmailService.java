package com.spring.nuqta.mail.Services;

import com.spring.nuqta.mail.template.AbstractEmailContext;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;

    @Autowired
    public EmailService(JavaMailSender emailSender, SpringTemplateEngine templateEngine) {
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
    }

    public void sendMail(AbstractEmailContext email) throws MessagingException {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message,
                    MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED,
                    StandardCharsets.UTF_8.name());

            Context context = new Context();
            context.setVariables(email.getContext());
            String emailContent = templateEngine.process(email.getTemplateLocation(), context);

            mimeMessageHelper.setTo(email.getTo());
            mimeMessageHelper.setSubject(email.getSubject());
            mimeMessageHelper.setFrom(email.getFrom());
            mimeMessageHelper.setText(emailContent, true);

            // Attach optional logo if exists
            ClassPathResource resource = new ClassPathResource("static/nuqta.png");
            if (resource.exists()) {
                mimeMessageHelper.addInline("nuqtaLogo", resource);
            }

            emailSender.send(message);
            log.info("Email sent successfully to {}", email.getTo());
        } catch (MessagingException e) {
            log.error("Failed to send email to {}: {}", email.getTo(), e.getMessage());
            throw new MessagingException("Failed to send email" + e.getMessage());
        }
    }
}
