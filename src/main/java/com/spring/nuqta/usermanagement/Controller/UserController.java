package com.spring.nuqta.usermanagement.Controller;

import com.spring.nuqta.usermanagement.Dto.UserDto;
import com.spring.nuqta.usermanagement.Dto.UserInsertDto;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import com.spring.nuqta.usermanagement.Mapper.UserInsertMapper;
import com.spring.nuqta.usermanagement.Mapper.UserMapper;
import com.spring.nuqta.usermanagement.Services.UserServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "User", description = "APIs for managing users")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserServices userServices;
    private final UserMapper userMapper;
    private final UserInsertMapper userInsertMapper;

    @Operation(summary = "Get All Users", description = "Retrieve a list of all users")
    @GetMapping("")
    public ResponseEntity<?> getAllUsers() {
        List<UserDto> dtos = userMapper.map(userServices.findAll());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @Operation(summary = "Get User by ID", description = "Retrieve details of a specific user by its ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        UserDto dto = userMapper.map(userServices.findById(id));
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Operation(summary = "Add New User", description = "Create a new user")
    @PostMapping()
    public ResponseEntity<?> addUser(@RequestBody UserInsertDto userDto) {
        UserEntity entity = userInsertMapper.unMap(userDto);
        userServices.insert(entity);
        UserDto dto = userMapper.map(entity);
        return new ResponseEntity<>(dto, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an Existing User", description = "Update the details of an existing user")
    @PutMapping()
    public ResponseEntity<?> updateUser(@RequestBody UserInsertDto userDto) {
        UserEntity entity = userInsertMapper.unMap(userDto);
        userServices.update(entity);
        UserDto dto = userMapper.map(entity);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @Operation(summary = "Delete User by ID", description = "Delete a user by its ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        userServices.deleteById(id);
        return new ResponseEntity<>(id, HttpStatus.OK);
    }
}
