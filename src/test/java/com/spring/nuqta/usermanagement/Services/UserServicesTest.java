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
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
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

        // Create a mock DonEntity
        DonEntity donation = new DonEntity();
        donation.setId(1L);
        donation.setLocation(new GeometryFactory().createPoint(new Coordinate(12.34, 56.78)));
        mockUser.setDonation(donation);
    }

    @Test
    void testFindAll_Success() {
        when(userRepository.findAll()).thenReturn(List.of(mockUser));

        List<UserEntity> users = userServices.findAll();

        assertFalse(users.isEmpty());
        assertEquals(1, users.size());
        assertEquals("testUser", users.get(0).getUsername());
        verify(userRepository, times(1)).findAll();
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
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        UserEntity result = userServices.findById(1L);
        assertNotNull(result);
        assertEquals("testUser", result.getUsername());

        verify(userRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_UserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> userServices.findById(1L));
        assertEquals("User not found with ID: 1", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verify(userRepository, times(1)).findById(1L);
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
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

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
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(userRepository.save(any(UserEntity.class))).thenReturn(mockUser);

        userServices.changeUserPassword(1L, "oldPassword", "newPassword");

        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    void testChangeUserPassword_IncorrectOldPassword() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        GlobalException exception = assertThrows(GlobalException.class,
                () -> userServices.changeUserPassword(1L, "wrongOldPassword", "newPassword"));

        assertEquals("Old password is incorrect.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        verify(userRepository, never()).save(any(UserEntity.class));
    }

    @Test
    void testUpdateFcmToken_Success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(any(UserEntity.class))).thenReturn(mockUser);

        assertEquals("FCM token updated successfully", userServices.updateFcmToken(1L, "newFcmToken"));

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

        DonEntity donation = new DonEntity();
        donation.setLocation(new GeometryFactory().createPoint(new Coordinate(12.34, 56.78)));
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
        donation.setLocation(new GeometryFactory().createPoint(new Coordinate(12.34, 56.78)));
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
        donation.setLocation(new GeometryFactory().createPoint(new Coordinate(12.34, 56.78)));
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
        donation.setLocation(new GeometryFactory().createPoint(new Coordinate(12.34, 56.78)));
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
        donation.setLocation(new GeometryFactory().createPoint(new Coordinate(12.34, 56.78)));
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
        assertEquals("Location is required.", ex.getMessage());
    }

    @Test
    void testValidateUserFields_NullPhoneNumber_ThrowsException() {
        UserEntity user = new UserEntity();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setEmail("test@example.com");
        user.setScope(Scope.USER);  // Valid scope

        DonEntity donation = new DonEntity();
        donation.setLocation(new GeometryFactory().createPoint(new Coordinate(12.34, 56.78)));
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
        donation.setLocation(new GeometryFactory().createPoint(new Coordinate(12.34, 56.78)));
        user.setDonation(donation);

        // scope is null
        GlobalException ex = assertThrows(GlobalException.class, () -> UserServices.validateUserFields(user));
        assertEquals("Scope is required.", ex.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, ex.getStatus());
    }
}