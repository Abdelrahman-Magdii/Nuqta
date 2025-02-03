package com.spring.nuqta.twilio.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "Phone_Verification")
@NoArgsConstructor
@AllArgsConstructor
public class PhoneVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String phoneNumber;
    private String otp;
    private LocalDateTime createdAt;
    private boolean verified;

}

