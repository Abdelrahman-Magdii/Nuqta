package com.spring.nuqta.authentication.Dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VerificationRequest {
    @Size(min = 10, max = 100)
    private String token;

    @Email
    private String mail;

}

