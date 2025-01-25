package com.spring.nuqta.donation.Controller;

import com.spring.nuqta.donation.Dto.DonDto;
import com.spring.nuqta.donation.Mapper.DonMapper;
import com.spring.nuqta.donation.Services.DonServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Donation", description = "APIs for managing donations")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/donation")
public class DonController {

    private final DonServices donServices;
    private final DonMapper donMapper;


    @Operation(summary = "Get All Donations", description = "Retrieve a list of all donations")
    @ApiResponse(responseCode = "200", description = "Donations get successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DonDto.class)))
    @GetMapping()
    public ResponseEntity<?> getAllDonation() {
        List<DonDto> dtos = donMapper.map(donServices.findAll());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @Operation(summary = "Get Donation by ID", description = "Retrieve a donation by its unique ID")
    @ApiResponse(responseCode = "200", description = "Donations get successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DonDto.class)))
    @GetMapping("/{id}")
    public ResponseEntity<?> getDonationById(@PathVariable Long id) {
        DonDto result = donMapper.map(donServices.findById(id));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @Operation(summary = "Delete Donation by ID", description = "Delete a donation by its unique ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteDonationById(@PathVariable Long id) {
        donServices.deleteById(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}
