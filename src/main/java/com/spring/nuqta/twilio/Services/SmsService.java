package com.spring.nuqta.twilio.Services;

import com.twilio.rest.api.v2010.account.Message;
import com.twilio.rest.verify.v2.service.Verification;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SmsService {


    @Value("${twilio.phoneNumber}")
    private String fromPhoneNumber;

    public void sendSms(String toPhoneNumber) {

        Verification verification = Verification.creator(
                        "VAfeba9f06a6863d459a2a25850dc58c60",
                        toPhoneNumber,
                        "sms")
                .create();


        log.info("📩 Message Sent Successfully! : " + verification.getSid());
    }

    public void sendSmsToFrom(String toPhoneNumber, String otp) {
        Message message = Message.creator(
                new PhoneNumber(toPhoneNumber),
                new PhoneNumber("+201024855747"),
                "Your Nuqta verification code is: " + otp
        ).create();
        log.info("📩 Message Sent Successfully! SID: " + message.getBody());

    }

}

