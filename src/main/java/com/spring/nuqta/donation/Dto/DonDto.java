package com.spring.nuqta.donation.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.spring.nuqta.base.Dto.BaseDto;
import com.spring.nuqta.enums.DonStatus;
import com.spring.nuqta.request.Dto.ReqDto;
import com.spring.nuqta.usermanagement.Dto.UserDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Schema(name = "Donation Entity", description = "Represents a donation request or record with details about the blood type, donation status, and other attributes.")
@Getter
@Setter
//@JsonInclude(JsonInclude.Include.NON_NULL)  // Exclude null fields from serialization
public class DonDto extends BaseDto<Long> {

    @Schema(description = "Type of blood being donated, e.g., A+, O-, etc.", example = "O+")
    @JsonProperty("blood_type")
    private String bloodType;

    @Schema(description = "The date of the current donation.", example = "2024-12-03")
    @JsonProperty("donation_date")
    private LocalDate donationDate;

    @Schema(description = "The date of the last donation made by the donor.", example = "2024-10-01")
    @JsonProperty("last_donation")
    private LocalDate lastDonation;

    @Schema(description = "Amount of blood donated in liters.", example = "0.5")
    private Double amount;

    @Schema(description = "Indicates if payment was offered for the donation.", example = "true")
    @JsonProperty("payment_offered")
    private Boolean paymentOffered;

    @Schema(description = "Current status of the donation process.", example = "PENDING")
    private DonStatus status;

    @Schema(description = "Weight of the donor", example = "80")
    private Double weight;

    @Schema(description = "City where the donation is happening", example = "New York")
    private String city;

    @Schema(description = "Conservatism level of the donation", example = "Moderate")
    private String conservatism;

    @Schema(description = "User details associated with the donation.", example = "[]")
    private UserDto user;

    @Schema(description = "All Requests that donation Accepts", example = "[]")
    private Set<ReqDto> acceptedRequests;
}
