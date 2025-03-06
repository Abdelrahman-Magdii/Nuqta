package com.spring.nuqta.usermanagement.Services;

import com.spring.nuqta.donation.Entity.DonEntity;
import com.spring.nuqta.enums.Scope;
import com.spring.nuqta.exception.GlobalException;
import com.spring.nuqta.organization.Repo.OrgRepo;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import com.spring.nuqta.usermanagement.Repo.UserRepo;
import com.spring.nuqta.verificationToken.General.GeneralVerification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

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

    @BeforeEach
    void setUp() {
        mockUser = new UserEntity();
        mockUser.setId(1L);
        mockUser.setUsername("testUser");
        mockUser.setEmail("test@example.com");
        mockUser.setPhoneNumber("1234567890");
        mockUser.setPassword("encodedPassword");
        mockUser.setScope(Scope.USER);
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

        assertEquals("No users found", exception.getMessage());
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
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> userServices.findById(1L));
        assertEquals("User not found with ID: 1", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verify(userRepository, times(1)).findByIdAndEnabledTrue(1L);
    }

    @Test
    void testUpdate_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(UserEntity.class))).thenReturn(mockUser);

        UserEntity updatedUser = new UserEntity();
        updatedUser.setId(1L);
        updatedUser.setUsername("updatedUser");
        updatedUser.setPhoneNumber("9876543210");

        // Set the donation entity properly
        DonEntity updatedDonation = new DonEntity();
        updatedDonation.setId(1L);
        updatedUser.setDonation(updatedDonation);

        // Perform update
        UserEntity result = userServices.update(updatedUser);

        assertNotNull(result);
        assertEquals("updatedUser", result.getUsername());

        verify(userRepository, times(1)).findById(1L);
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }


    @Test
    void testUpdate_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        UserEntity updatedUser = new UserEntity();
        updatedUser.setId(1L);
        updatedUser.setUsername("updatedUser");

        GlobalException exception = assertThrows(GlobalException.class, () -> userServices.update(updatedUser));

        assertEquals("User not found with ID: 1", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void updateUser_WhenUserIdIsNull_ShouldThrowException() {
        mockUser.setId(null);

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            userServices.update(mockUser);
        });

        assertEquals("User ID cannot be null", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    /**
     * Test Case: User not found in the repository
     */
    @Test
    void updateUser_WhenUserNotFound_ShouldThrowException() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            userServices.update(mockUser);
        });

        assertEquals("User not found with ID: 1", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
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

        assertEquals("User does not have a donation record", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    /**
     * Test Case: Provided donation ID is null
     */
    @Test
    void updateUser_WhenDonationIdIsNull_ShouldThrowException() {
        mockUser.setDonation(new DonEntity()); // Donation exists but has no ID

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        GlobalException exception = assertThrows(GlobalException.class, () -> {
            userServices.update(mockUser);
        });

        assertEquals("Donation ID cannot be null", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
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
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> userServices.deleteById(1L));

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

        assertEquals("Username and email is exist", exception.getMessage());
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

        assertEquals("Old password is incorrect.", exception.getMessage());
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

        String result = userServices.updateFcmToken(1L, "newFcmToken");

        assertEquals("FCM token updated successfully", result);
        verify(userRepository, times(1)).save(mockUser);
    }


    @Test
    void testUpdateFcmToken_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertEquals("User not found", userServices.updateFcmToken(1L, "newFcmToken"));
    }


    @Test
    void testValidateUserFields_AllValidFields_NoException() {
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        user.setPhoneNumber("1234567890");
        user.setScope(Scope.USER);
        user.setFcmToken("oldFcmToken");

        DonEntity donation = new DonEntity();
        donation.setCity("city");
        donation.setConservatism("conservatism");
        user.setDonation(donation);

        assertDoesNotThrow(() -> UserServices.validateUserFields(user));
    }

    @Test
    void testValidateUserFields_NullUsername_ThrowsException() {
        UserEntity user = new UserEntity();
        user.setPassword("password123");
        user.setEmail("test@example.com");
        user.setPhoneNumber("1234567890");
        user.setScope(Scope.USER);

        DonEntity donation = new DonEntity();
        donation.setCity("city");
        donation.setConservatism("conservatism");
        user.setDonation(donation);

        GlobalException ex = assertThrows(GlobalException.class, () -> UserServices.validateUserFields(user));
        assertEquals("Username is required.", ex.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void testValidateUserFields_NullPassword_ThrowsException() {
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPhoneNumber("1234567890");
        user.setScope(Scope.USER);

        DonEntity donation = new DonEntity();
        donation.setCity("city");
        donation.setConservatism("conservatism");
        user.setDonation(donation);

        GlobalException ex = assertThrows(GlobalException.class, () -> UserServices.validateUserFields(user));
        assertEquals("Password is required.", ex.getMessage());
    }

    @Test
    void testValidateUserFields_NullEmail_ThrowsException() {
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setPhoneNumber("1234567890");
        user.setScope(Scope.USER);

        DonEntity donation = new DonEntity();
        donation.setCity("city");
        donation.setConservatism("conservatism");
        user.setDonation(donation);

        GlobalException ex = assertThrows(GlobalException.class, () -> UserServices.validateUserFields(user));
        assertEquals("Email is required.", ex.getMessage());
    }

    @Test
    void testValidateUserFields_InvalidScope_ThrowsException() {
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        user.setPhoneNumber("1234567890");
        user.setScope(Scope.ORGANIZATION);  // Invalid scope

        DonEntity donation = new DonEntity();
        donation.setCity("city");
        donation.setConservatism("conservatism");
        user.setDonation(donation);

        GlobalException ex = assertThrows(GlobalException.class, () -> UserServices.validateUserFields(user));
        assertEquals("Invalid scope. Scope must be 'USER'.", ex.getMessage());
    }

    @Test
    void testValidateUserFields_NullLocation_ThrowsException() {
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        user.setPhoneNumber("1234567890");
        user.setScope(Scope.USER);

        DonEntity donation = new DonEntity();
        user.setDonation(donation); // No location set

        GlobalException ex = assertThrows(GlobalException.class, () -> UserServices.validateUserFields(user));
        assertEquals("Conservatism is required.", ex.getMessage());
    }

    @Test
    void testValidateUserFields_NullPhoneNumber_ThrowsException() {
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        user.setScope(Scope.USER);  // Valid scope

        DonEntity donation = new DonEntity();
        donation.setCity("city");
        donation.setConservatism("conservatism");
        user.setDonation(donation);

        // phoneNumber is null
        GlobalException ex = assertThrows(GlobalException.class, () -> UserServices.validateUserFields(user));
        assertEquals("Mobile phone number is required.", ex.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void testValidateUserFields_NullScope_ThrowsException() {
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        user.setPhoneNumber("1234567890"); // Valid phone number

        DonEntity donation = new DonEntity();
        donation.setCity("city");
        donation.setConservatism("conservatism");
        user.setDonation(donation);

        // scope is null
        GlobalException ex = assertThrows(GlobalException.class, () -> UserServices.validateUserFields(user));
        assertEquals("Scope is required.", ex.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }

    @Test
    void testDonationCityIsNull_ThrowsException() {
        // Mock the request parameters
        UserEntity mockUser = mock(UserEntity.class);
        DonEntity mockDonation = mock(DonEntity.class);

        // Ensure all required fields before city validation are valid
        when(mockUser.getUsername()).thenReturn("validUsername");
        when(mockUser.getPassword()).thenReturn("validPassword");
        when(mockUser.getEmail()).thenReturn("valid@email.com");
        when(mockUser.getPhoneNumber()).thenReturn("1234567890");
        when(mockUser.getScope()).thenReturn(Scope.USER);
        when(mockUser.getDonation()).thenReturn(mockDonation);
        when(mockDonation.getCity()).thenReturn(null);
        when(mockDonation.getConservatism()).thenReturn("conservatism");

        // Verify that the exception is thrown
        GlobalException exception = assertThrows(GlobalException.class,
                () -> userServices.validateUserFields(mockUser));

        assertEquals("City is required.", exception.getMessage());
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

        assertEquals("User name already exists", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());

        // Verify that user existence check was performed
        verify(userRepository).existsByUsernameAndIdNot("newUsername", 1L);
    }

    @Test
    void testChangeUserPassword_WhenUserNotFound_ThrowsNotFoundException() {
        // Arrange: Mock repository to return empty (user not found)
        Long userId = 1L;
        when(userRepository.findByIdAndEnabledTrue(userId)).thenReturn(Optional.empty());

        // Act & Assert: Expect GlobalException due to user not being found
        GlobalException exception = assertThrows(GlobalException.class,
                () -> userServices.changeUserPassword(userId, "oldPass", "newPass"));

        assertEquals("User not found with ID: " + userId, exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        // Verify repository call
        verify(userRepository).findByIdAndEnabledTrue(userId);
        verifyNoMoreInteractions(userRepository); // Ensures no further operations were attempted
    }

    @Test
    void testUpdate_WhenUserIdIsNull_ThrowsBadRequestException() {
        // Arrange: Create a user entity with null ID
        UserEntity entity = new UserEntity();
        entity.setId(null); // User ID is null
        // Act & Assert: Expect GlobalException due to null User ID
        GlobalException exception = assertThrows(GlobalException.class,
                () -> userServices.update(entity));

        assertEquals("User ID cannot be null", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        verifyNoInteractions(userRepository); // Ensure repository was never called
    }

    @Test
    void testUpdate_WhenDonationIdIsNull_ThrowsBadRequestException() {
        // Arrange
        UserEntity entity = new UserEntity();
        entity.setId(1L); // Valid User ID
        entity.setDonation(new DonEntity()); // Donation exists but ID is null

        UserEntity existingUser = new UserEntity();
        existingUser.setId(1L);
        existingUser.setDonation(new DonEntity()); // Mock user with a donation record

        when(userRepository.findById(1L)).thenReturn(Optional.of(existingUser)); // Simulate user exists

        // Act & Assert
        GlobalException exception = assertThrows(GlobalException.class,
                () -> userServices.update(entity));

        assertEquals("Donation ID cannot be null", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        verify(userRepository).findById(1L); // Ensure findById() was called
    }

    @Test
    void testInvalidIdThrowsException() {
        Exception exception = assertThrows(GlobalException.class, () -> {
            userServices.validId(null);
        });
        assertEquals("Invalid ID: null", exception.getMessage());

        exception = assertThrows(GlobalException.class, () -> {
            userServices.validId(0L);
        });
        assertEquals("Invalid ID: 0", exception.getMessage());

        exception = assertThrows(GlobalException.class, () -> {
            userServices.validId(-1L);
        });
        assertEquals("Invalid ID: -1", exception.getMessage());
    }
}