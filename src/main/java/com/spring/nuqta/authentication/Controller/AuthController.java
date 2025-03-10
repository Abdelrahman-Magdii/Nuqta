package com.spring.nuqta.authentication.Controller;

import com.spring.nuqta.authentication.Dto.AuthOrgDto;
import com.spring.nuqta.authentication.Dto.AuthUserDto;
import com.spring.nuqta.authentication.Services.AuthService;
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
    private final GeneralReset generalReset;
    private final GeneralVerification generalVerification;
    private final UserServices userServices;
    private final UserInsertMapper userInsertMapper;
    private final OrgServices orgServices;
    private final AddOrgMapper addOrgMapper;

    @PostMapping("/user/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody UserInsertDto userDto) {
        UserEntity entity = userInsertMapper.unMap(userDto);
        userServices.saveUser(entity);

        // Create a map with the message
        Map<String, String> response = new HashMap<>();
        response.put("message", "user.register.success");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/org/register")
    public ResponseEntity<?> register(@RequestBody AddOrgDto addOrgDto) {
        OrgEntity entity = addOrgMapper.unMap(addOrgDto);
        orgServices.saveOrg(entity);

        // Create a map for the JSON response
        Map<String, String> response = new HashMap<>();
        response.put("message", "organization.register.success");

        // Return the response as JSON
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login/user")
    public ResponseEntity<AuthUserDto> loginUser(@RequestBody Map<String, Object> input) throws SystemException {
        return ResponseEntity.ok(authService.authUser(input));
    }

    @PostMapping("/login/organization")
    public ResponseEntity<AuthOrgDto> loginOrganization(@RequestBody Map<String, Object> input) throws SystemException {
        return ResponseEntity.ok(authService.authOrganization(input));
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyRegistration(@RequestParam("token") String token, @RequestParam("mail") String mail) {

        boolean verified = generalVerification.verifyRegistration(token, mail);
        Map<String, String> response = new HashMap<>();

        if (verified) {
            response.put("messing", "email.verify.success");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("messing", "email.verify.invalid");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping("/forgotPassword")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestParam("email") String email) {
        return generalReset.sendOtpEmail(email);
    }


    @PostMapping("/resetPassword")
    public ResponseEntity<?> resetPassword(@RequestParam String email, @RequestParam String otp, @RequestParam String newPassword) {

        boolean verified = generalReset.resetPassword(email, otp, newPassword);

        Map<String, String> response = new HashMap<>();

        if (verified) {
            response.put("messing", "password.reset.success");
            return ResponseEntity.ok(response);
        } else {
            response.put("messing", "password.reset.invalid");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

}