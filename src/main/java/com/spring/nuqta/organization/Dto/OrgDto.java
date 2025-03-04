package com.spring.nuqta.organization.Dto;

import com.spring.nuqta.base.Dto.BaseDto;
import com.spring.nuqta.enums.Scope;
import com.spring.nuqta.request.Dto.AddReqDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Schema(name = "Organization Entity", description = "Represents the details of an organization.")
@Getter
@Setter
public class OrgDto extends BaseDto<Long> {

    @Schema(description = "Name of the organization", example = "Nuqta")
    private String orgName;

    @Schema(description = "Email address of the organization", example = "info@nuqta.com")
    private String email;

    @Schema(description = "City of the organization", example = "New York")
    private String city;

    @Schema(description = "Conservatism level of the organization", example = "Moderate")
    private String conservatism;

    @Schema(description = "Phone number of the organization", example = "+1234567890")
    private String phoneNumber;

    @Schema(description = "License number of the organization", example = "LIC-987654321")
    private String licenseNumber;

    @Schema(description = "Operational scope of the organization", example = "ORGANIZATION")
    private Scope scope;

    @Schema(description = "Requests that organization uploaded it", example = "[]")
    private Set<AddReqDto> uploadedRequests;


}
