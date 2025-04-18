package com.spring.nuqta.usermanagement.Controller;

import com.spring.nuqta.usermanagement.Dto.UserDto;
import com.spring.nuqta.usermanagement.Dto.UserInsertDto;
import com.spring.nuqta.usermanagement.Dto.UserUpdateDto;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import com.spring.nuqta.usermanagement.Mapper.UserInsertMapper;
import com.spring.nuqta.usermanagement.Mapper.UserMapper;
import com.spring.nuqta.usermanagement.Mapper.UserUpdateMapper;
import com.spring.nuqta.usermanagement.Services.UserServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User", description = "APIs for managing users")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserServices userServices;
    private final UserMapper userMapper;
    private final UserInsertMapper userInsertMapper;
    private final UserUpdateMapper userUpdateMapper;
    private final MessageSource ms;

    @Operation(summary = "Get All Users", description = "Retrieve a list of all users")
    @ApiResponse(responseCode = "200", description = "User get successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDto.class)))
    @GetMapping("")
    public ResponseEntity<?> getAllUsers() {
        List<UserDto> dtos = userMapper.map(userServices.findAll());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @Operation(summary = "Get User by ID", description = "Retrieve details of a specific user by its ID")
    @ApiResponse(responseCode = "200", description = "User get successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDto.class)))
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto dto = userMapper.map(userServices.findById(id));
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }


    @Operation(summary = "Update an Existing User", description = "Update the details of an existing user")
    @ApiResponse(responseCode = "200", description = "User updated successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserInsertDto.class)))
    @PutMapping("")
    public ResponseEntity<UserUpdateDto> updateUser(@RequestBody UserInsertDto userDto) {
        UserEntity entity = userInsertMapper.unMap(userDto);
        entity = userServices.update(entity);
        UserUpdateDto dto = userUpdateMapper.map(entity);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Operation(summary = "Delete User by ID", description = "Delete a user by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        userServices.deleteById(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", getMS("error.user.delete.id"));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @Operation(summary = "Change User Password", description = "Allow users to change their password")
    @ApiResponse(responseCode = "200", description = "Password changed successfully")
    @PostMapping("/changePassword")
    public ResponseEntity<Map<String, String>> changePassword(
            @RequestParam Long userId,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {

        userServices.changeUserPassword(userId, oldPassword, newPassword);

        Map<String, String> response = new HashMap<>();
        response.put("message", getMS("error.user.password.change"));

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Change User Fcm Token", description = "Allow users to change Fcm Token")
    @PutMapping("fcmToken/{id}")
    public ResponseEntity<?> updateFcmToken(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String fcmToken = request.get("fcmToken");
        return userServices.updateFcmToken(id, fcmToken);
    }

    public String getMS(String messageKey) {
        return ms.getMessage(messageKey, null, LocaleContextHolder.getLocale());
    }
}
