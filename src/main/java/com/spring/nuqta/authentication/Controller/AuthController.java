package com.spring.nuqta.authentication.Controller;

import com.spring.nuqta.authentication.Dto.AuthOrgDto;
import com.spring.nuqta.authentication.Dto.AuthUserDto;
import com.spring.nuqta.authentication.Services.AuthService;
import com.spring.nuqta.usermanagement.Services.UserServices;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.transaction.SystemException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final UserServices userServices;

    @GetMapping("/login/user")
    public ResponseEntity<AuthUserDto> loginUser(@RequestBody Map<String, Object> input) throws SystemException {
        return ResponseEntity.ok(authService.authUser(input));
    }

    @GetMapping("/login/organization")
    public ResponseEntity<AuthOrgDto> loginOrganization(@RequestBody Map<String, Object> input) throws SystemException {
        return ResponseEntity.ok(authService.authOrganization(input));
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyRegistration(@RequestParam("token") String token, @RequestParam("mail") String mail) {


        boolean verified = userServices.verifyRegistration(token, mail);

        Map<String, String> response = new HashMap<>();

        if (verified) {
            response.put("messing", "Email verified successfully! You can now log in.");
            return ResponseEntity.ok(response);
        } else {
            response.put("messing", "Invalid or expired verification token.");
            return ResponseEntity.badRequest().body(response);
        }
    }

}
