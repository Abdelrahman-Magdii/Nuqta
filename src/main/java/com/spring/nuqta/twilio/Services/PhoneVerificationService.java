package com.spring.nuqta.twilio.Services;

import com.spring.nuqta.twilio.Entity.PhoneVerification;
import com.spring.nuqta.twilio.Repo.PhoneVerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class PhoneVerificationService {

    private final PhoneVerificationRepository repository;
    private final SmsService smsService;

    @Autowired
    public PhoneVerificationService(PhoneVerificationRepository repository, SmsService smsService) {
        this.repository = repository;
        this.smsService = smsService;
    }

    public void sendOtp(String phoneNumber) {
        String otp = generateOtp();
        PhoneVerification verification = new PhoneVerification();
        verification.setPhoneNumber(phoneNumber);
        verification.setOtp(otp);
        verification.setCreatedAt(LocalDateTime.now());
        verification.setVerified(false);
        repository.save(verification);

        smsService.sendSms(phoneNumber);
//        smsService.sendSmsToFrom(phoneNumber, otp);
    }

    private String generateOtp() {
        return String.valueOf(new Random().nextInt(900000) + 100000); // 6-digit OTP
    }
}

