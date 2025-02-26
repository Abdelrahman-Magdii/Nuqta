package com.spring.nuqta.usermanagement.Services;

import com.spring.nuqta.base.Services.BaseServices;
import com.spring.nuqta.enums.Scope;
import com.spring.nuqta.exception.GlobalException;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.organization.Repo.OrgRepo;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import com.spring.nuqta.usermanagement.Repo.UserRepo;
import com.spring.nuqta.verificationToken.General.GeneralVerification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServices extends BaseServices<UserEntity, Long> {

    private final UserRepo userRepository; // Repository for UserEntity operations
    private final OrgRepo organizationRepository; // Repository for OrgEntity operations
    private final PasswordEncoder passwordEncoder; // Password encoder for secure password handling
    private final GeneralVerification generalVerification; // Service for general verification tasks

    /**
     * Validates the required fields of a UserEntity.
     * Throws a GlobalException if any required field is missing or invalid.
     *
     * @param params The UserEntity to validate.
     */
    static void validateUserFields(UserEntity params) {
        if (Objects.isNull(params.getUsername())) {
            throw new GlobalException("Username is required.", HttpStatus.BAD_REQUEST);
        }
        if (Objects.isNull(params.getPassword())) {
            throw new GlobalException("Password is required.", HttpStatus.BAD_REQUEST);
        }
        if (Objects.isNull(params.getEmail())) {
            throw new GlobalException("Email is required.", HttpStatus.BAD_REQUEST);
        }
        if (Objects.isNull(params.getPhoneNumber())) {
            throw new GlobalException("Mobile phone number is required.", HttpStatus.BAD_REQUEST);
        }
        if (Objects.isNull(params.getScope())) {
            throw new GlobalException("Scope is required.", HttpStatus.BAD_REQUEST);
        }

        if (!(Scope.USER.equals(params.getScope()))) {
            throw new GlobalException("Invalid scope. Scope must be 'USER'.", HttpStatus.BAD_REQUEST);
        }

        if (Objects.isNull(params.getDonation().getLocation())) {
            throw new GlobalException("Location is required.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Retrieves all users from the database.
     * Caches the result for future requests.
     *
     * @return A list of all UserEntity objects.
     * @throws GlobalException If no users are found.
     */
    @Override
    @Cacheable(value = "users")
    public List<UserEntity> findAll() throws GlobalException {
        List<UserEntity> users = userRepository.findAll();
        if (users.isEmpty()) {
            throw new GlobalException("No users found", HttpStatus.NOT_FOUND);
        }
        return users;
    }

    /**
     * Retrieves a user by their ID.
     * Caches the result for future requests.
     *
     * @param id The ID of the user to retrieve.
     * @return The UserEntity object.
     * @throws GlobalException If the user is not found.
     */
    @Override
    @Cacheable(value = "users", key = "#id")
    public UserEntity findById(Long id) throws GlobalException {
        Optional<UserEntity> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new GlobalException("User not found with ID: " + id, HttpStatus.NOT_FOUND);
        }
        return user.get();
    }

    /**
     * Updates an existing user in the database.
     * Updates the cache with the modified user.
     *
     * @param entity The UserEntity with updated fields.
     * @return The updated UserEntity object.
     * @throws GlobalException If the user ID is null or the user is not found.
     */
    @Override
    @CachePut(value = "users", key = "#entity.id")
    public UserEntity update(UserEntity entity) throws GlobalException {
        if (entity == null || entity.getId() == null) {
            throw new GlobalException("User ID cannot be null", HttpStatus.BAD_REQUEST);
        }

        Optional<UserEntity> user = userRepository.findById(entity.getId());
        if (user.isEmpty()) {
            throw new GlobalException("User not found with ID: " + entity.getId(), HttpStatus.NOT_FOUND);
        }

        UserEntity existingUser = user.get();

        // Check if existingUser.getDonation() is null before accessing getId()
        if (existingUser.getDonation() == null) {
            throw new GlobalException("User does not have a donation record", HttpStatus.NOT_FOUND);
        }

        if (entity.getDonation() == null || entity.getDonation().getId() == null) {
            throw new GlobalException("Donation ID cannot be null", HttpStatus.BAD_REQUEST);
        }

        if (!existingUser.getDonation().getId().equals(entity.getDonation().getId())) {
            throw new GlobalException("Donation not found with ID: " + entity.getDonation().getId(), HttpStatus.NOT_FOUND);
        }

        existingUser.setUsername(entity.getUsername());
        existingUser.setGender(entity.getGender());
        existingUser.setPhoneNumber(entity.getPhoneNumber());
        existingUser.setScope(entity.getScope());
        existingUser.setDonation(entity.getDonation());
        existingUser.setModifiedDate(LocalDate.now());
        existingUser.setModifiedUser(entity.getUsername());

        return userRepository.save(existingUser);
    }


    /**
     * Deletes a user by their ID.
     * Evicts the user from the cache.
     *
     * @param id The ID of the user to delete.
     * @throws GlobalException If the user is not found.
     */
    @Override
    @CacheEvict(value = "users", key = "#id")
    public void deleteById(Long id) throws GlobalException {
        Optional<UserEntity> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new GlobalException("User not found with ID: " + id, HttpStatus.NOT_FOUND);
        }
        userRepository.deleteById(id);
    }

    /**
     * Saves a new user to the database.
     * Validates the user fields and checks for existing users or organizations with the same email or username.
     * Evicts all entries from the cache.
     *
     * @param entity The UserEntity to save.
     */
    @CacheEvict(value = "users", allEntries = true)
    public void saveUser(UserEntity entity) {
        validateUserFields(entity);

        Optional<UserEntity> existingUser =
                userRepository.findByUsernameOrEmail(entity.getUsername(), entity.getEmail());
        Optional<OrgEntity> existingOrganization =
                organizationRepository.findByEmail(entity.getEmail());

        if (existingUser.isPresent() || existingOrganization.isPresent()) {
            throw new GlobalException("Username and email is exist", HttpStatus.BAD_REQUEST);
        }

        UserEntity userCreation = new UserEntity();

        userCreation.setUsername(entity.getUsername());
        userCreation.setEmail(entity.getEmail());
        userCreation.setPassword(passwordEncoder.encode(entity.getPassword()));
        userCreation.setDonation(entity.getDonation());
        userCreation.setScope(entity.getScope());
        userCreation.setGender(entity.getGender());
        userCreation.setPhoneNumber(entity.getPhoneNumber());
        userCreation.setBirthDate(entity.getBirthDate());
        userCreation.setCreatedDate(LocalDate.now());
        userCreation.setCreatedUser(entity.getUsername());

        userCreation = userRepository.save(userCreation);

        generalVerification.sendOtpEmail(userCreation);
    }

    /**
     * Changes the password of an existing user.
     * Verifies the old password before updating to the new password.
     * Evicts the user from the cache.
     *
     * @param userId      The ID of the user.
     * @param oldPassword The old password to verify.
     * @param newPassword The new password to set.
     * @throws GlobalException If the user is not found or the old password is incorrect.
     */
    @CacheEvict(value = "users", key = "#userId")
    public void changeUserPassword(Long userId, String oldPassword, String newPassword) {
        UserEntity user = findById(userId);
        if (user == null) {
            throw new GlobalException("User not found with ID: " + userId, HttpStatus.NOT_FOUND);
        }

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new GlobalException("Old password is incorrect.", HttpStatus.BAD_REQUEST);
        }

        // Encode and update new password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    /**
     * Updates the FCM token for a user.
     * Updates the cache with the modified user.
     *
     * @param id       The ID of the user.
     * @param fcmToken The new FCM token to set.
     * @return A ResponseEntity indicating success or failure.
     */
    @CachePut(value = "users", key = "#id")
    public String updateFcmToken(Long id, String fcmToken) {
        Optional<UserEntity> userOptional = userRepository.findById(id);

        if (userOptional.isPresent()) {
            UserEntity user = userOptional.get();
            user.setFcmToken(fcmToken);
            userRepository.save(user);
            return "FCM token updated successfully";
        } else {
            return "User not found";
        }
    }
}