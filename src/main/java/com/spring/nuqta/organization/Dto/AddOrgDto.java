package com.spring.nuqta.organization.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.spring.nuqta.base.Dto.BaseDto;
import com.spring.nuqta.enums.Scope;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(name = "Add Organization DTO", description = "Represents the details required to add a new organization.")
@Getter
@Setter
public class AddOrgDto extends BaseDto<Long> {

    @Schema(description = "Name of the organization", example = "Nuqta Technologies")
    private String orgName;

    @Schema(description = "Email address of the organization", example = "info@nuqta.com")
    private String email;

    @Schema(description = "Password of the organization", example = "*******")
    private String password;

    @Schema(description = "City where the organization is located", example = "New York")
    private String city;

    @Schema(description = "Conservatism level of the organization", example = "Moderate")
    private String conservatism;

    @Schema(description = "Phone number of the organization", example = "+1234567890")
    private String phoneNumber;

    @Schema(description = "License number of the organization", example = "LIC-987654321")
    private String licenseNumber;

    @Schema(description = "Operational scope of the organization", example = "ORGANIZATION")
    private Scope scope;

    @Schema(description = "FCM Token for push notifications", example = "abc123xyz")
    @JsonProperty("fcmToken")
    private String fcmToken;

}
