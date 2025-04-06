package com.spring.nuqta.usermanagement.Controller;

import com.spring.nuqta.usermanagement.Dto.UserDto;
import com.spring.nuqta.usermanagement.Dto.UserInsertDto;
import com.spring.nuqta.usermanagement.Dto.UserUpdateDto;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import com.spring.nuqta.usermanagement.Mapper.UserInsertMapper;
import com.spring.nuqta.usermanagement.Mapper.UserMapper;
import com.spring.nuqta.usermanagement.Mapper.UserUpdateMapper;
import com.spring.nuqta.usermanagement.Services.UserServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @Mock
    private UserServices userServices;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserInsertMapper userInsertMapper;

    @Mock
    private UserUpdateMapper userUpdateMapper;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private UserController userController;

    private UserEntity userEntity;
    private UserDto userDto;
    private UserInsertDto userInsertDto;
    private UserUpdateDto userUpdateDto;

    @BeforeEach
    void setUp() {
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("testuser");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("testuser");

        userInsertDto = new UserInsertDto();
        userInsertDto.setUsername("testuser");

        userUpdateDto = new UserUpdateDto();
        userUpdateDto.setId(1L);
        userUpdateDto.setUsername("testuser");
    }

    // Test for getAllUsers()
    @Test
    void getAllUsers_ShouldReturnListOfUsers() {
        // Arrange
        List<UserEntity> entities = Arrays.asList(userEntity);
        List<UserDto> dtos = Arrays.asList(userDto);

        when(userServices.findAll()).thenReturn(entities);
        when(userMapper.map(entities)).thenReturn(dtos);

        // Act
        ResponseEntity<?> response = userController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtos, response.getBody());
        verify(userServices).findAll();
        verify(userMapper).map(entities);
    }

    @Test
    void getAllUsers_ShouldReturnEmptyListWhenNoUsersExist() {
        // Arrange
        when(userServices.findAll()).thenReturn(Collections.emptyList());
        when(userMapper.map(Collections.emptyList())).thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<?> response = userController.getAllUsers();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(((List<?>) response.getBody()).isEmpty());
    }

    // Test for getUserById()
    @Test
    void getUserById_ShouldReturnUser() {
        // Arrange
        when(userServices.findById(1L)).thenReturn(userEntity);
        when(userMapper.map(userEntity)).thenReturn(userDto);

        // Act
        ResponseEntity<UserDto> response = userController.getUserById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userDto, response.getBody());
        verify(userServices).findById(1L);
        verify(userMapper).map(userEntity);
    }

    @Test
    void getUserById_ShouldThrowExceptionWhenUserNotFound() {
        // Arrange
        when(userServices.findById(1L)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // Act & Assert
        assertThrows(ResponseStatusException.class, () -> userController.getUserById(1L));
    }

    // Test for updateUser()
    @Test
    void updateUser_ShouldReturnUpdatedUser() {
        // Arrange
        when(userInsertMapper.unMap(userInsertDto)).thenReturn(userEntity);
        when(userServices.update(userEntity)).thenReturn(userEntity);
        when(userUpdateMapper.map(userEntity)).thenReturn(userUpdateDto);

        // Act
        ResponseEntity<UserUpdateDto> response = userController.updateUser(userInsertDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(userUpdateDto, response.getBody());
        verify(userInsertMapper).unMap(userInsertDto);
        verify(userServices).update(userEntity);
        verify(userUpdateMapper).map(userEntity);
    }

    @Test
    void updateUser_ShouldThrowExceptionWhenMappingFails() {
        // Arrange
        when(userInsertMapper.unMap(userInsertDto)).thenThrow(new RuntimeException("Mapping failed"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userController.updateUser(userInsertDto));
    }

    // Test for deleteUserById()
    @Test
    void deleteUserById_ShouldReturnSuccessMessage() {
        // Arrange
        String message = "User deleted successfully";
        when(messageSource.getMessage(eq("error.user.delete.id"), isNull(), any(Locale.class))).thenReturn(message);

        // Act
        ResponseEntity<?> response = userController.deleteUserById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(message, ((Map<?, ?>) response.getBody()).get("message"));
        verify(userServices).deleteById(1L);
        verify(messageSource).getMessage(eq("error.user.delete.id"), isNull(), any(Locale.class));
    }

    @Test
    void deleteUserById_ShouldThrowExceptionWhenDeletionFails() {
        // Arrange
        doThrow(new RuntimeException("Deletion failed")).when(userServices).deleteById(1L);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userController.deleteUserById(1L));
    }

    // Test for changePassword()
    @Test
    void changePassword_ShouldReturnSuccessMessage() {
        // Arrange
        String message = "Password changed successfully";
        when(messageSource.getMessage(eq("error.user.password.change"), isNull(), any(Locale.class))).thenReturn(message);

        // Act
        ResponseEntity<Map<String, String>> response = userController.changePassword(
                1L, "oldPassword", "newPassword");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(message, response.getBody().get("message"));
        verify(userServices).changeUserPassword(1L, "oldPassword", "newPassword");
        verify(messageSource).getMessage(eq("error.user.password.change"), isNull(), any(Locale.class));
    }

    @Test
    void changePassword_ShouldThrowExceptionWhenPasswordChangeFails() {
        // Arrange
        doThrow(new RuntimeException("Password change failed"))
                .when(userServices).changeUserPassword(1L, "oldPassword", "newPassword");

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> userController.changePassword(1L, "oldPassword", "newPassword"));
    }

    // Test for updateFcmToken()
    @Test
    void updateFcmToken_ShouldReturnResponseFromService() {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("fcmToken", "newToken");
        ResponseEntity<?> expectedResponse = ResponseEntity.ok().build();

        when(userServices.updateFcmToken(1L, "newToken")).thenReturn(ResponseEntity.ok().build());

        // Act
        ResponseEntity<?> response = userController.updateFcmToken(1L, request);

        // Assert
        assertEquals(expectedResponse, response);
        verify(userServices).updateFcmToken(1L, "newToken");
    }

    @Test
    void updateFcmToken_ShouldThrowExceptionWhenUpdateFails() {
        // Arrange
        Map<String, String> request = new HashMap<>();
        request.put("fcmToken", "newToken");
        when(userServices.updateFcmToken(1L, "newToken"))
                .thenThrow(new RuntimeException("Update failed"));

        // Act & Assert
        assertThrows(RuntimeException.class,
                () -> userController.updateFcmToken(1L, request));
    }

    // Test for getMS()
    @Test
    void getMS_ShouldReturnMessage() {
        // Arrange
        String expectedMessage = "Test message";
        when(messageSource.getMessage(anyString(), isNull(), any(Locale.class))).thenReturn(expectedMessage);

        // Act
        String result = userController.getMS("test.key");

        // Assert
        assertEquals(expectedMessage, result);
    }

    @Test
    void getMS_ShouldThrowExceptionWhenMessageNotFound() {
        // Arrange
        when(messageSource.getMessage(anyString(), isNull(), any(Locale.class)))
                .thenThrow(new RuntimeException("Message not found"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> userController.getMS("invalid.key"));
    }
}