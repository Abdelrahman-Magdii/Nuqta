package com.spring.nuqta.donation.Dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AcceptDonationRequestDto {

    @NotNull(message = "Donation ID is required.")
    private Long donationId;

    @NotNull(message = "Request ID is required.")
    private Long requestId;
}

