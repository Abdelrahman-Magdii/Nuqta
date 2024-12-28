package com.spring.nuqta.usermanagement.Dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.spring.nuqta.base.Dto.BaseDto;
import com.spring.nuqta.donation.Dto.DonDto;
import com.spring.nuqta.enums.Scope;
import com.spring.nuqta.request.Dto.ReqDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Schema(name = "User Entity", description = "Represents the details of a user.")
@Getter
@Setter
//@JsonInclude(JsonInclude.Include.NON_NULL)  // Exclude null fields from serialization
public class UserDto extends BaseDto<Long> {

    @Schema(description = "Unique username of the user", example = "john_doe")
    private String username;

    @Schema(description = "Email address of the user", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Age of the user", example = "30")
    private Integer age;


    @Schema(description = "Phone number of the user", example = "+11234567890")
    private String phone_number;

    @Schema(description = "Scope of the user, indicating their operational access", example = "USER")
    private Scope scope;

    @Schema(description = "Donation details associated with the user", example = "[]")
    @JsonProperty("donation")
    private DonDto donation;

    @Schema(description = "Requests details associated with the user", example = "[]")
    @JsonProperty("requests")
    @JsonIgnore
    private List<ReqDto> requests;

}
