package com.spring.nuqta.organization.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.spring.nuqta.base.Dto.BaseDto;
import com.spring.nuqta.enums.Scope;
import com.spring.nuqta.request.Dto.ReqDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Schema(name = "Organization Entity", description = "Represents the details of an organization.")
@Getter
@Setter
public class OrgDto extends BaseDto<Long> {
    
    @Schema(description = "Name of the organization", example = "Nuqta Technologies")
    private String org_name;

    @Schema(description = "Email address of the organization", example = "info@nuqta.com")
    private String email;

    @Schema(description = "Longitude coordinate of the donation request's location.", example = "-74.0060")
    private Double longitude;

    @Schema(description = "Latitude coordinate of the donation request's location.", example = "40.7128")
    private Double latitude;

    @Schema(description = "Phone number of the organization", example = "+1234567890")
    private String phone_number;

    @Schema(description = "License number of the organization", example = "LIC-987654321")
    private String license_number;

    @Schema(description = "Operational scope of the organization", example = "ORGANIZATION")
    private Scope scope;

    @Schema(description = "Requests details associated with the user")
    @JsonProperty("requests")
    private List<ReqDto> requests;


}
