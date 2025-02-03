package com.spring.nuqta.authentication.Controller;

import com.spring.nuqta.OtpMail.General.GeneralVerify;
import com.spring.nuqta.authentication.Dto.AuthOrgDto;
import com.spring.nuqta.authentication.Dto.AuthUserDto;
import com.spring.nuqta.authentication.Services.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.SystemException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@Tag(name = "Authentication")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final GeneralVerify generalVerify;

    @GetMapping("/login/user")
    public ResponseEntity<AuthUserDto> loginUser(@RequestBody Map<String, Object> input) throws SystemException {
        return ResponseEntity.ok(authService.authUser(input));
    }

    @GetMapping("/login/organization")
    public ResponseEntity<AuthOrgDto> loginOrganization(@RequestBody Map<String, Object> input) throws SystemException {
        return ResponseEntity.ok(authService.authOrganization(input));
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyRegistration(@RequestParam("email") String email, @RequestParam("otp") String otp) {

        boolean verified = generalVerify.verifyEmail(email, otp);
        Map<String, String> response = new HashMap<>();

        if (verified) {
            response.put("messing", "Email verified successfully!");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("messing", "Invalid verification code.");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }


    @GetMapping("/resend")
    public ResponseEntity<Map<String, String>> resendVerificationEmail(@RequestParam("email") String email) {
        String result = generalVerify.resendOtp(email);
        Map<String, String> response = new HashMap<>();

        return switch (result) {
            case "A new OTP has been sent to your email." -> {
                response.put("message", result);
                yield ResponseEntity.ok(response);
            }
            case "Error sending OTP email. Please try again later." -> {
                response.put("message", result);
                yield ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
            case "Email not found. Please check the email address and try again." -> {
                response.put("message", result);
                yield ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            case "Email already verified." -> {
                response.put("message", result);
                yield ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
            default -> {
                response.put("message", "Unexpected error occurred.");
                yield ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }
        };


    }
}