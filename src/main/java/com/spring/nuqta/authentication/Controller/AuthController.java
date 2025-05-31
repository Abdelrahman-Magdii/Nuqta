package com.spring.nuqta.authentication.Controller;

import com.spring.nuqta.authentication.Dto.AuthOrgDto;
import com.spring.nuqta.authentication.Dto.AuthUserDto;
import com.spring.nuqta.authentication.Services.AuthService;
import com.spring.nuqta.donation.Services.DonServices;
import com.spring.nuqta.forgotPassword.General.GeneralReset;
import com.spring.nuqta.organization.Dto.AddOrgDto;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.organization.Mapper.AddOrgMapper;
import com.spring.nuqta.organization.Services.OrgServices;
import com.spring.nuqta.usermanagement.Dto.UserInsertDto;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import com.spring.nuqta.usermanagement.Mapper.UserInsertMapper;
import com.spring.nuqta.usermanagement.Services.UserServices;
import com.spring.nuqta.verificationToken.General.GeneralVerification;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.SystemException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Tag(name = "Authentication")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final GeneralReset generalReset;
    private final GeneralVerification generalVerification;
    private final UserServices userServices;
    private final DonServices donationService;
    private final UserInsertMapper userInsertMapper;
    private final OrgServices orgServices;
    private final AddOrgMapper addOrgMapper;
    private final MessageSource ms;

    @PostMapping("/register/user")
    public ResponseEntity<Map<String, String>> registerUser(@RequestBody UserInsertDto userDto) {
        UserEntity entity = userInsertMapper.unMap(userDto);
        userServices.saveUser(entity);

        Map<String, String> response = new HashMap<>();

        response.put("message", getMS("user.register.success"));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/org")
    public ResponseEntity<Map<String, String>> registerOrg(@RequestBody AddOrgDto addOrgDto) {
        OrgEntity entity = addOrgMapper.unMap(addOrgDto);
        orgServices.saveOrg(entity);

        Map<String, String> response = new HashMap<>();
        response.put("message", getMS("organization.register.success"));

        return ResponseEntity.ok(response);
    }

    @PostMapping("/login/user")
    public ResponseEntity<AuthUserDto> loginUser(@RequestBody Map<String, Object> input) throws SystemException {
        return ResponseEntity.ok(authService.authUser(input));
    }

    @PostMapping("/login/org")
    public ResponseEntity<AuthOrgDto> loginOrganization(@RequestBody Map<String, Object> input) throws SystemException {
        return ResponseEntity.ok(authService.authOrganization(input));
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyRegistration(
            @RequestParam("token") String token,
            @RequestParam("mail") String mail,
            HttpServletRequest servletRequest) {

        boolean verified = generalVerification.verifyRegistration(token, mail);

        String redirectPath = verified ? "/verification-success.html" : "/verification-failed.html";
        String redirectUrl = ServletUriComponentsBuilder.fromRequest(servletRequest)
                .replacePath(redirectPath)
                .scheme("https")
                .build()
                .toUriString();

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(redirectUrl))
                .build();
    }


    @PostMapping("/forgotPassword")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestParam("email") String email) {
        return generalReset.sendOtpEmail(email);
    }

    @PostMapping("/verifyOtp")
    public ResponseEntity<?> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        boolean valid = generalReset.validateOtp(email, otp);
        Map<String, String> response = new HashMap<>();
        if (!valid) {
            response.put("message", getMS("otp.invalid"));
            return ResponseEntity.badRequest().body(response);
        } else {
            response.put("message", getMS("otp.verified"));
            return ResponseEntity.ok(response);
        }
    }

    @PostMapping("/resetPassword")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestParam String email, @RequestParam String otp, @RequestParam String newPassword) {

        boolean verified = generalReset.resetPassword(email, otp, newPassword);
        Map<String, String> response = new HashMap<>();

        if (verified) {
            response.put("message", getMS("password.reset.success"));
            return ResponseEntity.ok(response);
        } else {
            response.put("message", getMS("password.reset.invalid"));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    private String getMS(String messageKey) {
        return ms.getMessage(messageKey, null, LocaleContextHolder.getLocale());
    }


    @GetMapping("/accept")
    public ResponseEntity<?> verifyAccept(
            @RequestParam("accept") boolean accept,
            @RequestParam("donationId") Long donationId,
            @RequestParam("requestId") Long requestId,
            @RequestParam("name") String name,
            @RequestParam("phoneNumber") String phoneNumber,
            @RequestParam("bloodType") String bloodType,
            @RequestParam(value = "encoded", defaultValue = "false") boolean isEncoded,
            HttpServletRequest request) {
//https://nuqta-02f0fc9e8c38.herokuapp.com/api/auth/accept?accept=true
// &donationId=2&name=bW9oYW1lZCBoYWJpYiA%3D&phoneNumber=MDEyMTE1NjU1Mjg%3D&bloodType=QS0%3D&encoded=true
        try {
            // Decode parameters if they were encoded
            String decodedName = name;
            String decodedPhone = phoneNumber;
            String decodedBloodType = bloodType;

            if (isEncoded) {
                decodedName = new String(Base64.getUrlDecoder().decode(name), StandardCharsets.UTF_8);
                decodedPhone = new String(Base64.getUrlDecoder().decode(phoneNumber), StandardCharsets.UTF_8);
                decodedBloodType = new String(Base64.getUrlDecoder().decode(bloodType), StandardCharsets.UTF_8);
            }

            if (accept) {
                donationService.markAsAccepted(donationId, requestId);
            }

            String redirectPath = accept ? "/accept-success.html" : "/accept-failed.html";

            // Re-encode for URL parameters to ensure safe transmission
            String encodedNameForUrl = URLEncoder.encode(decodedName, StandardCharsets.UTF_8);
            String encodedPhoneForUrl = URLEncoder.encode(decodedPhone, StandardCharsets.UTF_8);
            String encodedBloodTypeForUrl = URLEncoder.encode(decodedBloodType, StandardCharsets.UTF_8);

            String redirectUrl = UriComponentsBuilder.fromPath(redirectPath)
                    .scheme("https")
                    .host(request.getServerName())
                    .port(request.getServerPort())
                    .queryParam("donationId", donationId)
                    .queryParam("name", encodedNameForUrl)
                    .queryParam("phoneNumber", encodedPhoneForUrl)
                    .queryParam("bloodType", encodedBloodTypeForUrl)
                    .build(true)
                    .toUriString();

            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(redirectUrl))
                    .build();

        } catch (Exception e) {
            log.error("Error processing accept request: ", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("error", "Invalid request parameters"));
        }
    }

}