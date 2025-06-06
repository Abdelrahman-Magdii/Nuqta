package com.spring.nuqta.organization.Dto;

import com.spring.nuqta.base.Dto.BaseDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(name = "Add Organization DTO", description = "Represents the details required to add a new organization.")
@Getter
@Setter
public class OrgRequestReqDto extends BaseDto<Long> {

    @Schema(description = "Name of the organization", example = "Nuqta Technologies")
    private String orgName;

    @Schema(description = "City where the organization is located", example = "New York")
    private String city;

    @Schema(description = "Conservatism level of the organization", example = "Moderate")
    private String conservatism;

    @Schema(description = "Phone number of the organization", example = "+1234567890")
    private String phoneNumber;

    @Schema(description = "License number of the organization", example = "LIC-987654321")
    private String licenseNumber;

}
