package com.spring.nuqta.usermanagement.Controller;

import com.spring.nuqta.usermanagement.Dto.UserDto;
import com.spring.nuqta.usermanagement.Dto.UserInsertDto;
import com.spring.nuqta.usermanagement.Dto.UserUpdateDto;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import com.spring.nuqta.usermanagement.Mapper.UserInsertMapper;
import com.spring.nuqta.usermanagement.Mapper.UserMapper;
import com.spring.nuqta.usermanagement.Mapper.UserUpdateMapper;
import com.spring.nuqta.usermanagement.Services.UserServices;
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

    @GetMapping("")
    public ResponseEntity<?> getAllUsers() {
        List<UserDto> dtos = userMapper.map(userServices.findAll());
        return new ResponseEntity<>(dtos, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        UserDto dto = userMapper.map(userServices.findById(id));
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @PutMapping("")
    public ResponseEntity<UserUpdateDto> updateUser(@RequestBody UserInsertDto userDto) {
        UserEntity entity = userInsertMapper.unMap(userDto);
        entity = userServices.update(entity);
        UserUpdateDto dto = userUpdateMapper.map(entity);
        return new ResponseEntity<>(dto, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long id) {
        userServices.deleteById(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", getMS("error.user.delete.id"));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

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

    @PutMapping("fcmToken/{id}")
    public ResponseEntity<?> updateFcmToken(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String fcmToken = request.get("fcmToken");
        return userServices.updateFcmToken(id, fcmToken);
    }

    public String getMS(String messageKey) {
        return ms.getMessage(messageKey, null, LocaleContextHolder.getLocale());
    }
}
