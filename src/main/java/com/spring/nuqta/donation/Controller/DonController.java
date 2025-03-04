package com.spring.nuqta.donation.Controller;

import com.spring.nuqta.donation.Dto.AcceptDonationRequestDto;
import com.spring.nuqta.donation.Dto.DonDto;
import com.spring.nuqta.donation.Dto.DonResponseDto;
import com.spring.nuqta.donation.Entity.DonEntity;
import com.spring.nuqta.donation.Mapper.DonMapper;
import com.spring.nuqta.donation.Mapper.DonResponseMapper;
import com.spring.nuqta.donation.Services.DonServices;
import com.spring.nuqta.exception.GlobalException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Tag(name = "Donation", description = "APIs for managing donations")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/donation")
public class DonController {

    private final DonServices donServices;
    private final DonMapper donMapper;
    private final DonResponseMapper donResponseMapper;


    @Operation(summary = "Get All Donations", description = "Retrieve a list of all donations")
    @ApiResponse(responseCode = "200", description = "Donations get successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DonDto.class)))
    @GetMapping()
    public ResponseEntity<?> getAllDonation() {
        List<DonResponseDto> dtos = donResponseMapper.map(donServices.findAll());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @Operation(summary = "Get Donation by ID", description = "Retrieve a donation by its unique ID")
    @ApiResponse(responseCode = "200", description = "Donations get successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DonDto.class)))
    @GetMapping("/{id}")
    public ResponseEntity<?> getDonationById(@PathVariable Long id) {
        DonResponseDto result = donResponseMapper.map(donServices.findById(id));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @Operation(summary = "Get Nearest Donations", description = "Retrieve a list of donations nearest to the provided coordinates")
    @ApiResponse(responseCode = "200", description = "Donations retrieved successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = DonDto.class)))
    @ApiResponse(responseCode = "400", description = "Invalid input parameters (e.g., missing latitude or longitude)")
    @GetMapping("/nearest")
    public ResponseEntity<?> getNearestDonations(@RequestParam double latitude, @RequestParam double longitude) {

        // Fetch nearest donations from the service layer
        List<DonEntity> nearestDonations = donServices.findNearestLocations(latitude, longitude);

        // Map entities to DTOs
        List<DonDto> dtos = donMapper.map(nearestDonations);

        // Return the DTOs with a 200 OK status
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }


    @PostMapping("/acceptRequest")
    public ResponseEntity<?> acceptDonationRequest(@RequestBody AcceptDonationRequestDto dto) {
        DonEntity updatedDonation = donServices.acceptDonationRequest(dto);
        DonResponseDto entity = donResponseMapper.map(updatedDonation);
        return ResponseEntity.ok(entity);
    }

    @DeleteMapping("/deleteRequest")
    public ResponseEntity<Map<String, Object>> deleteAcceptedDonationRequest(@RequestBody AcceptDonationRequestDto dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            donServices.deleteAcceptedDonationRequest(dto);
            response.put("message", "Donation request successfully deleted.");
            return ResponseEntity.ok(response);
        } catch (GlobalException e) {
            response.put("message", e.getMessage());
            return ResponseEntity.status(e.getStatus()).body(response);
        } catch (Exception e) {
            response.put("message", "An error occurred: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
