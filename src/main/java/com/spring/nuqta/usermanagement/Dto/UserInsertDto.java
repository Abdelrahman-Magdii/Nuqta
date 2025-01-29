package com.spring.nuqta.usermanagement.Dto;

import com.spring.nuqta.base.Dto.BaseDto;
import com.spring.nuqta.donation.Dto.DonDto;
import com.spring.nuqta.enums.Gender;
import com.spring.nuqta.enums.Scope;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Schema(name = "User Entity", description = "Represents the details of a user.")
@Getter
@Setter
//@JsonInclude(JsonInclude.Include.NON_NULL)  // Exclude null fields from serialization
public class UserInsertDto extends BaseDto<Long> {

    @Schema(description = "Unique username of the user", example = "john_doe")
    private String username;

    @Schema(description = "Email address of the user", example = "john.doe@example.com")
    private String email;

    @Schema(description = "password of the user", example = "*******")
    private String password;

    @Schema(description = "Birth date of the user", example = "2003-10-03")
    private LocalDate birthDate;

    @Schema(description = "Phone number of the user", example = "+11234567890")
    private String phoneNumber;

    @Schema(description = "Gender of user", example = "male")
    private Gender gender;
    
    @Schema(description = "Scope of the user, indicating their operational access", example = "USER")
    private Scope scope;

    @Schema(description = "Donation details associated with the user")
    private DonDto donation;
}
