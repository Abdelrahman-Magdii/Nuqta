package com.spring.nuqta.request.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.spring.nuqta.base.Dto.BaseDto;
import com.spring.nuqta.enums.Level;
import com.spring.nuqta.enums.Status;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Schema(name = "Add Request DTO")
@Getter
@Setter
public class AddReqDto extends BaseDto<Long> {

    @NotNull(message = "Blood type is required.")
    @Schema(description = "Type of blood needed for the request", example = "O+")
    @JsonProperty("blood_type_needed")
    private String bloodTypeNeeded;

    @Schema(description = "Amount of blood needed for the request", example = "2")
    private Double amount;

    @Schema(description = "Date of the request", example = "2024-12-01")
    @JsonProperty("request_date")
    private LocalDate requestDate;

    @Schema(description = "Urgency level of the request", example = "HIGH")
    @JsonProperty("urgency_level")
    private Level urgencyLevel;

    @Schema(description = "Current status of the request", example = "OPEN")
    @JsonProperty("status")
    private Status status;

    @Schema(description = "Indicates if payment is available for this request", example = "true")
    @JsonProperty("payment_available")
    private Boolean paymentAvailable;

    @Schema(description = "City where the request is made", example = "New York")
    @JsonProperty("city")
    private String city;

    @Schema(description = "Conservatism level of the request", example = "LOW")
    @JsonProperty("conservatism")
    private String conservatism;

}
