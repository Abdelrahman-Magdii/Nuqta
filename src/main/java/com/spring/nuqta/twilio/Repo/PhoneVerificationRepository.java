package com.spring.nuqta.twilio.Repo;

import com.spring.nuqta.twilio.Entity.PhoneVerification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhoneVerificationRepository extends JpaRepository<PhoneVerification, Long> {

    PhoneVerification findByPhoneNumber(String phoneNumber);
}

