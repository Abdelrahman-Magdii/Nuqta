package com.spring.nuqta.donation.Services;

import com.spring.nuqta.donation.Entity.DonEntity;
import com.spring.nuqta.mail.Services.EmailService;
import com.spring.nuqta.mail.template.SendMailToDoner;
import com.spring.nuqta.mail.template.SendMailToDonerRejected;
import com.spring.nuqta.request.Entity.ReqEntity;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SendEmail {

    private final EmailService emailService;

    @Value("${token.base.url}")
    private String baseUrl;

    void sendMail(DonEntity donation, ReqEntity req) throws MessagingException {

        UserEntity donor = donation.getUser();
        if (donor == null || donor.getFcmToken() == null) {
            return;
        }

        SendMailToDoner context = new SendMailToDoner();

        if (req.getUser() != null) {
            context.init(req.getUser());
        } else if (req.getOrganization() != null) {
            context.init(req.getOrganization());
        }
        context.buildVerificationUrl(donor, donation.getId(), req.getId(), baseUrl);

        emailService.sendMail(context);

    }

    void sendMailRejected(DonEntity donation, ReqEntity req) throws MessagingException {

        UserEntity donor = donation.getUser();
        if (donor == null || donor.getFcmToken() == null) {
            return;
        }

        SendMailToDonerRejected context = new SendMailToDonerRejected();
        if (req.getUser() != null) {
            context.init(req.getUser());
        } else if (req.getOrganization() != null) {
            context.init(req.getOrganization());
        }
        context.buildVerificationUrl(donor);

        emailService.sendMail(context);

    }

}
