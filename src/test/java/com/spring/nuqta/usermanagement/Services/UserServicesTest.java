package com.spring.nuqta.usermanagement.Services;

import com.spring.nuqta.donation.Entity.DonEntity;
import com.spring.nuqta.enums.Gender;
import com.spring.nuqta.enums.Scope;
import com.spring.nuqta.exception.GlobalException;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.organization.Repo.OrgRepo;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import com.spring.nuqta.usermanagement.Repo.UserRepo;
import com.spring.nuqta.verificationToken.General.GeneralVerification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
class UserServicesTest {

    @Mock
    private UserRepo userRepository;

    @Mock
    private OrgRepo organizationRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private GeneralVerification generalVerification;

    @InjectMocks
    private UserServices userServices;

    private UserEntity mockUser;

    @Mock
    private MessageSource messageSource;

    @BeforeEach
    void setUp() {
        mockUser = new UserEntity();
        mockUser.setId(1L);
        mockUser.setUsername("testUser");
        mockUser.setEmail("test@example.com");
        mockUser.setPhoneNumber("1234567890");
        mockUser.setPassword("encodedPassword");
        mockUser.setScope(Scope.USER);
        mockUser.setGender(Gender.MALE);
        mockUser.setBirthDate(LocalDate.now().minusYears(20));
        mockUser.setCreatedDate(LocalDate.now());
        mockUser.setFcmToken("token");

        // Create a mock DonEntity
        DonEntity donation = new DonEntity();
        donation.setId(1L);
        donation.setCity("city");
        donation.setConservatism("conservatism");
        mockUser.setDonation(donation);
    }

    @Test
    void testFindAll_Success() {
        when(userRepository.findAllByEnabledTrue()).thenReturn(List.of(mockUser)); // FIX HERE

        List<UserEntity> users = userServices.findAll();

        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
        assertEquals("testUser", users.get(0).getUsername());

        verify(userRepository, times(1)).findAllByEnabledTrue(); // FIX HERE
    }

    @Test
    void testFindAll_NoUsersFound() {
        when(userRepository.findAll()).thenReturn(List.of());

        GlobalException exception = assertThrows(GlobalException.class, () -> userServices.findAll());

        assertEquals("error.user.notFound", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testFindById_Success() {
        when(userRepository.findByIdAndEnabledTrue(1L)).thenReturn(Optional.of(mockUser)); // FIX HERE

        UserEntity result = userServices.findById(1L);
        assertNotNull(result);
        assertEquals("testUser", result.getUsername());

        verify(userRepository, times(1)).findByIdAndEnabledTrue(1L); // FIX HERE
    }

    @Test
    void testFindById_UserNotFound() {
        // Mock the repository to return an empty Optional (user not found)
        when(userRepository.findByIdAndEnabledTrue(1L)).thenReturn(Optional.empty());

        // Mock the MessageSource to return the expected message
        when(messageSource.getMessage(eq("error.user.notFound.id"), any(), any()))
                .thenReturn("User not found with ID: 1");

        // Call the method under test and expect a GlobalException
        GlobalException exception = assertThrows(GlobalException.class, () -> userServices.findById(1L));

        // Verify the exception message and status
        assertEquals("User not found with ID: 1", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        // Verify that the repository method was called
        verify(userRepository, times(1)).findByIdAndEnabledTrue(1L);
    }


    @Test
    void testUpdate_UserNotFound() {
        // Mock the repository to return an empty Optional (user not found)
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Mock the MessageSource to return the expected message
        when(messageSource.getMessage(eq("error.user.notFound.id"), any(), any()))
                .thenReturn("User not found with ID: 1");

        // Create a UserEntity to update
        UserEntity updatedUser = new UserEntity();
        updatedUser.setId(1L);
        updatedUser.setUsername("updatedUser");

        // Call the method under test and expect a GlobalException
        GlobalException exception = assertThrows(GlobalException.class, () -> userServices.update(updatedUser));

        // Verify the exception message and status
        assertEquals("User not found with ID: 1", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void updateUser_WhenUserIdIsNull_ShouldThrowException() {
        mockUser.setId(null);

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            userServices.update(mockUser);
        });

        assertEquals("error.user.id.null", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    /**
     * Test Case: User not found in the repository
     */
    @Test
    void updateUser_WhenUserNotFound_ShouldThrowException() {
        // Mock the repository to return an empty Optional (user not found)
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Mock the messageParam method to return the expected message
        when(messageSource.getMessage(eq("error.user.notFound.id"), any(), any()))
                .thenReturn("User not found with ID: 1");

        // Call the method under test and expect a GlobalException
        GlobalException exception = assertThrows(GlobalException.class, () -> userServices.update(mockUser));

        // Verify the exception message
        assertEquals("User not found with ID: 1", exception.getMessage());
    }

    /**
     * Test Case: Existing user has no donation record
     */
    @Test
    void updateUser_WhenUserHasNoDonation_ShouldThrowException() {
        mockUser.setDonation(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            userServices.update(mockUser);
        });

        assertEquals("error.user.donation.notFound", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }


    /**
     * Test Case: Donation ID mismatch between existing and provided user
     */
    @Test
    void updateUser_WhenDonationIdMismatch_ShouldThrowException() {
        // Create an updated user with a different donation ID (Mismatch scenario)
        UserEntity updatedUser = new UserEntity();
        updatedUser.setId(1L);
        updatedUser.setUsername("updatedUser");
        updatedUser.setPhoneNumber("0987654321");
        updatedUser.setScope(Scope.USER);

        DonEntity newDonation = new DonEntity();
        newDonation.setId(2L); // Different ID
        updatedUser.setDonation(newDonation);

        // Mock repository to return the existing user
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        // Mock the MessageSource to return the expected message
        when(messageSource.getMessage(eq("error.user.donation.notFound.id"), any(), any()))
                .thenReturn("Donation not found with ID: 2");

        // Debugging output
        System.out.println("Existing Donation ID: " + mockUser.getDonation().getId());
        System.out.println("New Donation ID: " + updatedUser.getDonation().getId());

        // Expect GlobalException to be thrown
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            userServices.update(updatedUser);
        });

        // Ensure correct exception message and status
        assertEquals("Donation not found with ID: 2", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }


    @Test
    void testDeleteById_Success() {
        when(userRepository.existsById(1L)).thenReturn(true); // FIX HERE

        userServices.deleteById(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }


    @Test
    void testDeleteById_UserNotFound() {
        // Mock the repository to return an empty Optional (user not found)
        when(userRepository.existsById(1L)).thenReturn(false);

        // Mock the MessageSource to return the expected message
        when(messageSource.getMessage(eq("error.user.notFound.id"), any(), any()))
                .thenReturn("User not found with ID: 1");

        // Call the method under test and expect a GlobalException
        GlobalException exception = assertThrows(GlobalException.class, () -> userServices.deleteById(1L));

        // Verify the exception message and status
        assertEquals("User not found with ID: 1", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }


    @Test
    void testSaveUser_Success() {
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.empty());
        when(organizationRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(mockUser);


        assertDoesNotThrow(() -> userServices.saveUser(mockUser));

        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(generalVerification, times(1)).sendOtpEmail(any(UserEntity.class));
    }

    @Test
    void testSaveUser_UsernameOrEmailExists() {
        when(userRepository.findByUsernameOrEmail(anyString(), anyString())).thenReturn(Optional.of(mockUser));

        GlobalException exception = assertThrows(GlobalException.class, () -> userServices.saveUser(mockUser));

        assertEquals("error.user.username.email.exist", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testSaveUser_UsernameOrEmailOrgExists() {
        when(organizationRepository.findByEmail(anyString())).thenReturn(Optional.of(new OrgEntity()));


        GlobalException exception = assertThrows(GlobalException.class, () -> userServices.saveUser(mockUser));

        assertEquals("error.user.username.email.exist", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }


    @Test
    void testChangeUserPassword_Success() {
        when(userRepository.findByIdAndEnabledTrue(1L)).thenReturn(Optional.of(mockUser)); // FIX HERE
        when(passwordEncoder.matches("oldPassword", mockUser.getPassword())).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("newEncodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(mockUser);

        userServices.changeUserPassword(1L, "oldPassword", "newPassword");

        verify(userRepository, times(1)).save(any(UserEntity.class));
    }


    @Test
    void testChangeUserPassword_IncorrectOldPassword() {
        UserEntity mockUser = new UserEntity();
        mockUser.setId(1L);
        mockUser.setPassword("hashedOldPassword"); // Ensure password is set

        // FIXED: Mock the correct method that is actually being called
        when(userRepository.findByIdAndEnabledTrue(1L)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches("wrongOldPassword", "hashedOldPassword")).thenReturn(false);

        GlobalException exception = assertThrows(GlobalException.class,
                () -> userServices.changeUserPassword(1L, "wrongOldPassword", "newPassword"));

        assertEquals("error.user.old.password.incorrect", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testUpdateFcmToken_Success() {
        UserEntity mockUser = new UserEntity();
        mockUser.setId(1L);
        mockUser.setFcmToken("oldFcmToken");

        // FIX: Mock the correct repository method
        when(userRepository.findByIdAndEnabledTrue(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(UserEntity.class))).thenReturn(mockUser);

        ResponseEntity<?> result = userServices.updateFcmToken(1L, "newFcmToken");

//        assertEquals("FCM token updated successfully", result);
        verify(userRepository, times(1)).save(mockUser);
    }

    @Test
    void testUpdateFcmToken_UserNotFound() {
        // Mock the repository to return an empty Optional (user not found)
        when(userRepository.findByIdAndEnabledTrue(1L)).thenReturn(Optional.empty());

        // Call the method under test
        ResponseEntity<?> response = userServices.updateFcmToken(1L, "newFcmToken");

        // Verify the response status code
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

        // Extract the response body and verify the message
        Map<String, String> responseBody = (Map<String, String>) response.getBody();
        assertNotNull(responseBody); // Ensure the response body is not null
        assertEquals(userServices.getMS("error.user.notFound"), responseBody.get("message")); // Verify the message
    }

    @Test
    void testValidateUserFields_NullUsername_ThrowsException() {
        mockUser.setUsername(null);

        GlobalException ex = assertThrows(GlobalException.class, () -> userServices.validateUserFields(mockUser));
        assertEquals("error.user.username", ex.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void testValidateUserFields_NullPassword_ThrowsException() {
        mockUser.setPassword(null);

        GlobalException ex = assertThrows(GlobalException.class, () -> userServices.validateUserFields(mockUser));
        assertEquals("error.user.password", ex.getMessage());
    }

    @Test
    void testValidateUserFields_NullEmail_ThrowsException() {
        mockUser.setEmail(null);

        GlobalException ex = assertThrows(GlobalException.class, () -> userServices.validateUserFields(mockUser));
        assertEquals("error.user.email", ex.getMessage());
    }

    @Test
    void testValidateUserFields_InvalidScope_ThrowsException() {

        mockUser.setScope(Scope.ORGANIZATION);  // Invalid scope

        GlobalException ex = assertThrows(GlobalException.class, () -> userServices.validateUserFields(mockUser));
        assertEquals("error.user.invalid.scope", ex.getMessage());
    }

    @Test
    void testValidateUserFields_NullLocation_ThrowsException() {
        mockUser.getDonation().setCity("city");
        mockUser.getDonation().setConservatism(null);


        GlobalException ex = assertThrows(GlobalException.class, () -> userServices.validateUserFields(mockUser));
        assertEquals("error.user.conservatism", ex.getMessage());
    }

    @Test
    void testValidateUserFields_NullPhoneNumber_ThrowsException() {
        mockUser.setPhoneNumber(null);

        // phoneNumber is null
        GlobalException ex = assertThrows(GlobalException.class, () -> userServices.validateUserFields(mockUser));
        assertEquals("error.user.phone", ex.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void testValidateUserFields_NullScope_ThrowsException() {
        mockUser.setScope(null);

        // scope is null
        GlobalException ex = assertThrows(GlobalException.class, () -> userServices.validateUserFields(mockUser));
        assertEquals("error.user.scope", ex.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void testDonationCityIsNull_ThrowsException() {
        mockUser.getDonation().setCity(null);

        // Verify that the exception is thrown
        GlobalException exception = assertThrows(GlobalException.class,
                () -> userServices.validateUserFields(mockUser));

        assertEquals("error.user.city", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testUpdate_WhenUsernameExists_ThrowsConflictException() {
        // Arrange: Mock user data
        UserEntity existingUser = new UserEntity();
        existingUser.setId(1L);
        existingUser.setUsername("oldUsername");

        UserEntity updateUser = new UserEntity();
        updateUser.setId(1L);
        updateUser.setUsername("newUsername");

        // Mock user retrieval
        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser));

        // Mock duplicate username check (Simulating another user with same username)
        when(userRepository.existsByUsernameAndIdNot("newUsername", 1L)).thenReturn(true);

        // Act & Assert: Expect GlobalException due to username conflict
        GlobalException exception = assertThrows(GlobalException.class,
                () -> userServices.update(updateUser));

        assertEquals("error.user.username.already.exist", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());

        // Verify that user existence check was performed
        verify(userRepository).existsByUsernameAndIdNot("newUsername", 1L);
    }

    @Test
    void testUpdate_WhenUserIdIsNull_ThrowsBadRequestException() {
        // Arrange: Create a user entity with null ID
        UserEntity entity = new UserEntity();
        entity.setId(null); // User ID is null
        // Act & Assert: Expect GlobalException due to null User ID
        GlobalException exception = assertThrows(GlobalException.class,
                () -> userServices.update(entity));

        assertEquals("error.user.id.null", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        verifyNoInteractions(userRepository); // Ensure repository was never called
    }

    @Test
    void testValidateUserFields() {

        // Validate the user fields
        assertDoesNotThrow(() -> userServices.validateUserFields(mockUser));
    }

    @Test
    void validateUserFields_WithNullFcmToken_ShouldThrowException() {
        // Given
        mockUser.setFcmToken(null);

        // When / Then
        GlobalException exception = assertThrows(GlobalException.class,
                () -> userServices.validateUserFields(mockUser));
        assertEquals("error.user.fcmToken", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void validateUserFields_WithNullBirthDate_ShouldThrowException() {
        // Given
        mockUser.setBirthDate(null);

        // When / Then
        GlobalException exception = assertThrows(GlobalException.class,
                () -> userServices.validateUserFields(mockUser));
        assertEquals("error.user.birthDate", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void validateUserFields_WithNullGender_ShouldThrowException() {
        // Given
        mockUser.setGender(null);

        // When / Then
        GlobalException exception = assertThrows(GlobalException.class,
                () -> userServices.validateUserFields(mockUser));
        assertEquals("error.user.gender", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testValidateUserFields_ThrowsException() {
        UserEntity invalidUser = new UserEntity();
        assertThrows(GlobalException.class, () -> userServices.validateUserFields(invalidUser));
    }

    @Test
    void testFindAll() {
        when(userRepository.findAllByEnabledTrue()).thenReturn(Arrays.asList(mockUser));
        List<UserEntity> users = userServices.findAll();
        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
    }

    @Test
    void testFindAll_ThrowsException() {
        when(userRepository.findAllByEnabledTrue()).thenReturn(Collections.emptyList());
        assertThrows(GlobalException.class, () -> userServices.findAll());
    }

    @Test
    void testFindById() {
        when(userRepository.findByIdAndEnabledTrue(1L)).thenReturn(Optional.of(mockUser));
        UserEntity foundUser = userServices.findById(1L);
        assertNotNull(foundUser);
        assertEquals("testUser", foundUser.getUsername());
    }

    @Test
    void testFindById_ThrowsException() {
        when(userRepository.findByIdAndEnabledTrue(1L)).thenReturn(Optional.empty());
        assertThrows(GlobalException.class, () -> userServices.findById(1L));
    }

    @Test
    void testUpdate_ThrowsException() {
        mockUser.setId(null);
        assertThrows(GlobalException.class, () -> userServices.update(mockUser));
    }

    @Test
    void testDeleteById() {
        when(userRepository.existsById(1L)).thenReturn(true);
        userServices.deleteById(1L);
        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteById_ThrowsException() {
        when(userRepository.existsById(1L)).thenReturn(false);
        assertThrows(GlobalException.class, () -> userServices.deleteById(1L));
    }

    @Test
    void testSaveUser() {
        when(userRepository.findByUsernameOrEmail("testUser", "test@example.com")).thenReturn(Optional.empty());
        when(organizationRepository.findByEmail("test@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(mockUser);

        userServices.saveUser(mockUser);

        verify(userRepository, times(1)).save(any(UserEntity.class));
        verify(generalVerification, times(1)).sendOtpEmail(any(UserEntity.class));
    }

    @Test
    void testSaveUser_ThrowsException() {
        when(userRepository.findByUsernameOrEmail("testUser", "test@example.com")).thenReturn(Optional.of(mockUser));
        assertThrows(GlobalException.class, () -> userServices.saveUser(mockUser));
    }


    @Test
    void testChangeUserPassword_ThrowsException() {
        when(userRepository.findByIdAndEnabledTrue(1L)).thenReturn(Optional.empty());
        assertThrows(GlobalException.class, () -> userServices.changeUserPassword(1L, "oldPassword", "newPassword"));
    }

    @Test
    void testUpdateFcmToken() {
        when(userRepository.findByIdAndEnabledTrue(1L)).thenReturn(Optional.of(mockUser));
        ResponseEntity<?> response = userServices.updateFcmToken(1L, "newFcmToken");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void testUpdateFcmToken_ThrowsException() {
        when(userRepository.findByIdAndEnabledTrue(1L)).thenReturn(Optional.empty());
        ResponseEntity<?> response = userServices.updateFcmToken(1L, "newFcmToken");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testValidId() {
        assertDoesNotThrow(() -> userServices.validId(1L));
    }

    @Test
    void testValidId_ThrowsException() {
        assertThrows(GlobalException.class, () -> userServices.validId(null));
        assertThrows(GlobalException.class, () -> userServices.validId(0L));
    }

    @Test
    void testMessageParam() {
        when(messageSource.getMessage(anyString(), any(), any())).thenReturn("Test Message");
        String message = userServices.messageParam(1L, "error.user.invalid.id");
        assertNotNull(message);
    }
}