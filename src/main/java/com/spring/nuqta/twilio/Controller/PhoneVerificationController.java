package com.spring.nuqta.twilio.Controller;


import com.spring.nuqta.twilio.Entity.PhoneVerification;
import com.spring.nuqta.twilio.Repo.PhoneVerificationRepository;
import com.spring.nuqta.twilio.Services.PhoneVerificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/phone")
public class PhoneVerificationController {

    private final PhoneVerificationService service;
    private final PhoneVerificationRepository repository;

    @Autowired
    public PhoneVerificationController(PhoneVerificationService service, PhoneVerificationRepository repository) {
        this.service = service;
        this.repository = repository;
    }

    @PostMapping("/send-otp")
    public ResponseEntity<String> sendOtp(@RequestParam String phoneNumber) {
        service.sendOtp(phoneNumber);
        return ResponseEntity.ok("OTP sent successfully");
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<String> verifyOtp(@RequestParam String phoneNumber, @RequestParam String otp) {
        PhoneVerification verification = repository.findByPhoneNumber(phoneNumber);

        if (verification == null || !verification.getOtp().equals(otp)) {
            return ResponseEntity.badRequest().body("Invalid OTP");
        }

        if (Duration.between(verification.getCreatedAt(), LocalDateTime.now()).toMinutes() > 5) {
            return ResponseEntity.badRequest().body("OTP expired");
        }

        verification.setVerified(true);
        repository.save(verification);

        return ResponseEntity.ok("Phone number verified successfully");
    }
}

