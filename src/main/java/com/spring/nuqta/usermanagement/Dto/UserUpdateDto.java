package com.spring.nuqta.usermanagement.Dto;

import com.spring.nuqta.base.Dto.BaseDto;
import com.spring.nuqta.donation.Dto.DonResponseUserUpdateDto;
import com.spring.nuqta.enums.Gender;
import com.spring.nuqta.enums.Scope;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Schema(name = "User Entity", description = "Represents the details of a user.")
@Getter
@Setter
//@JsonInclude(JsonInclude.Include.NON_NULL)  // Exclude null fields from serialization
public class UserUpdateDto extends BaseDto<Long> {

    @Schema(description = "Unique username of the user", example = "john_doe")
    private String username;

    @Schema(description = "Email address of the user", example = "john.doe@example.com")
    private String email;

    @Schema(description = "Age of the user", example = "30")
    private Integer age;

    @Schema(description = "Phone number of the user", example = "+11234567890")
    private String phoneNumber;

    @Schema(description = "Gender of user", example = "male")
    private Gender gender;

    @Schema(description = "Scope of the user, indicating their operational access", example = "USER")
    private Scope scope;

    @Schema(description = "Donation details associated with the user", example = "{}")
    private DonResponseUserUpdateDto donation;

}
