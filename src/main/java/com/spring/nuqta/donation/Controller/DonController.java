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

import java.util.List;

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

        List<DonEntity> nearestDonations = donServices.findTopConservatism(conservatism);

        List<DonDto> dtos = donMapper.map(nearestDonations);

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/nearest/city")
    public ResponseEntity<?> getNearestDonationsCity(@RequestParam String city) {

        List<DonEntity> nearestDonations = donServices.findTopCity(city);

        List<DonDto> dtos = donMapper.map(nearestDonations);

        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @PostMapping("/acceptRequest")
    public void acceptDonationRequest(@RequestBody AcceptDonationRequestDto dto) throws MessagingException {
        donServices.acceptDonationRequest(dto);
        throw new GlobalException("donation.request.accepted.email.sent", HttpStatus.OK);

    }

    @DeleteMapping("/deleteRequest")
    public void deleteAcceptedDonationRequest(@RequestBody AcceptDonationRequestDto dto) throws MessagingException {
        donServices.deleteAcceptedDonationRequest(dto);
        throw new GlobalException("success.donation.requestDeleted", HttpStatus.OK);
    }

    private String getMS(String messageKey) {
        return ms.getMessage(messageKey, null, LocaleContextHolder.getLocale());
    }
}
