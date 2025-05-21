package com.spring.nuqta.donation.Controller;

import com.spring.nuqta.donation.Dto.AcceptDonationRequestDto;
import com.spring.nuqta.donation.Dto.DonDto;
import com.spring.nuqta.donation.Dto.DonResponseDto;
import com.spring.nuqta.donation.Entity.DonEntity;
import com.spring.nuqta.donation.Mapper.DonMapper;
import com.spring.nuqta.donation.Mapper.DonResponseMapper;
import com.spring.nuqta.donation.Services.DonServices;
import com.spring.nuqta.exception.GlobalException;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
    private final MessageSource ms;


    @GetMapping("/{id}")
    public ResponseEntity<?> getDonationById(@PathVariable Long id) {
        DonResponseDto result = donResponseMapper.map(donServices.findById(id));
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/nearest/conservatism")
    public ResponseEntity<?> getNearestDonations(@RequestParam String conservatism) {

        // Fetch nearest donations from the service layer
        List<DonEntity> nearestDonations = donServices.findTopConservatism(conservatism);

        // Map entities to DTOs
        List<DonDto> dtos = donMapper.map(nearestDonations);

        // Return the DTOs with a 200 OK status
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/nearest/city")
    public ResponseEntity<?> getNearestDonationsCity(@RequestParam String city) {

        // Fetch nearest donations from the service layer
        List<DonEntity> nearestDonations = donServices.findTopCity(city);

        // Map entities to DTOs
        List<DonDto> dtos = donMapper.map(nearestDonations);

        // Return the DTOs with a 200 OK status
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @PostMapping("/acceptRequest")
    public ResponseEntity<?> acceptDonationRequest(@RequestBody AcceptDonationRequestDto dto) throws MessagingException {
        DonEntity updatedDonation = donServices.acceptDonationRequest(dto);
        DonResponseDto entity = donResponseMapper.map(updatedDonation);
        return ResponseEntity.ok(entity);
    }

    @DeleteMapping("/deleteRequest")
    public ResponseEntity<Map<String, Object>> deleteAcceptedDonationRequest(@RequestBody AcceptDonationRequestDto dto) {
        Map<String, Object> response = new HashMap<>();
        try {
            donServices.deleteAcceptedDonationRequest(dto);
            response.put("message", getMS("success.donation.requestDeleted"));
            return ResponseEntity.ok(response);
        } catch (GlobalException e) {
            response.put("message", getMS("error.globalException"));
            return ResponseEntity.status(e.getStatus()).body(response);
        } catch (Exception e) {
            response.put("message", getMS("error.unknown"));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    private String getMS(String messageKey) {
        return ms.getMessage(messageKey, null, LocaleContextHolder.getLocale());
    }
}
