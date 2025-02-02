package com.spring.nuqta.usermanagement.Controller;

import com.spring.nuqta.usermanagement.Dto.UserDto;
import com.spring.nuqta.usermanagement.Dto.UserInsertDto;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import com.spring.nuqta.usermanagement.Mapper.UserInsertMapper;
import com.spring.nuqta.usermanagement.Mapper.UserMapper;
import com.spring.nuqta.usermanagement.Services.UserServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "User", description = "APIs for managing users")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserServices userServices;
    private final UserMapper userMapper;
    private final UserInsertMapper userInsertMapper;

    @Operation(summary = "Get All Users", description = "Retrieve a list of all users")
    @ApiResponse(responseCode = "200", description = "User get successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserDto.class)))
    @GetMapping("")
    public ResponseEntity<List<UserDto>> getAllUsers() {
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

    @Operation(summary = "Sign in User", description = "Sign in a new user")
    @ApiResponse(responseCode = "201", description = "User Sign in successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserInsertDto.class)))
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody UserInsertDto userDto) {
        UserEntity entity = userInsertMapper.unMap(userDto);
        userServices.saveUser(entity);

        // Create a map with the message
        Map<String, String> response = new HashMap<>();
        response.put("message", "Check Email verification sent!");

        return ResponseEntity.ok(response);
    }


    @Operation(summary = "Update an Existing User", description = "Update the details of an existing user")
    @ApiResponse(responseCode = "200", description = "User updated successfully",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = UserInsertDto.class)))
    @PutMapping("")
    public ResponseEntity<UserDto> updateUser(@RequestBody UserInsertDto userDto) {
        UserEntity entity = userInsertMapper.unMap(userDto);
        entity = userServices.update(entity);
        UserDto dto = userMapper.map(entity);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Operation(summary = "Delete User by ID", description = "Delete a user by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        userServices.deleteById(id);
        return new ResponseEntity<>("Success Delete User ", HttpStatus.OK);
    }
}
