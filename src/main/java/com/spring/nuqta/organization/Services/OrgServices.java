package com.spring.nuqta.organization.Services;

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
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Service class for handling Organization-related operations.
 * Extends BaseServices to provide basic CRUD functionality.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrgServices extends BaseServices<OrgEntity, Long> {

    private final OrgRepo organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final GeneralVerification generalVerification;
    private final UserRepo userRepo;

    /**
     * Retrieves all organizations from the database.
     * Uses caching to optimize retrieval performance.
     *
     * @return List of all organizations.
     * @throws GlobalException if no organizations are found.
     */
    @Override
    @Cacheable(value = "org")
    public List<OrgEntity> findAll() throws GlobalException {
        List<OrgEntity> organizations = super.findAll();
        if (organizations.isEmpty()) {
            throw new GlobalException("No organizations found", HttpStatus.NOT_FOUND);
        }
        return organizations;
    }

    /**
     * Retrieves an organization by its ID.
     * Uses caching to improve performance.
     *
     * @param id Organization ID.
     * @return The organization entity.
     * @throws GlobalException if the organization is not found.
     */
    @Override
    @Cacheable(value = "org", key = "#id")
    public OrgEntity findById(Long id) throws GlobalException {
        OrgEntity organization = super.findById(id);
        if (organization == null) {
            throw new GlobalException("Organization not found with ID: " + id, HttpStatus.NOT_FOUND);
        }
        return organization;
    }

    /**
     * Updates an existing organization.
     * Uses caching to store the updated entity.
     *
     * @param entity Organization entity with updated details.
     * @return The updated organization.
     * @throws GlobalException if the entity or its ID is null, or if the organization is not found.
     */
    @Override
    @CachePut(value = "org", key = "#entity.id")
    public OrgEntity update(OrgEntity entity) throws GlobalException {
        if (entity == null || entity.getId() == null) {
            throw new GlobalException("Organization ID cannot be null", HttpStatus.BAD_REQUEST);
        }
        OrgEntity existingOrganization = super.findById(entity.getId());
        if (existingOrganization == null) {
            throw new GlobalException("Organization not found with ID: " + entity.getId(), HttpStatus.NOT_FOUND);
        }

        // Updating only selected fields to maintain data integrity
        existingOrganization.setOrgName(entity.getOrgName());
        existingOrganization.setLocation(entity.getLocation());
        existingOrganization.setPhoneNumber(entity.getPhoneNumber());
        existingOrganization.setScope(entity.getScope());
        existingOrganization.setModifiedDate(LocalDate.now());
        existingOrganization.setModifiedUser(entity.getOrgName());

        return super.update(existingOrganization);
    }

    /**
     * Deletes an organization by its ID.
     * Removes the entity from the cache upon deletion.
     *
     * @param id Organization ID.
     * @throws GlobalException if the organization is not found.
     */
    @Override
    @CacheEvict(value = "org", key = "#id")
    public void deleteById(Long id) throws GlobalException {
        OrgEntity organization = super.findById(id);
        if (organization == null) {
            throw new GlobalException("Organization not found with ID: " + id, HttpStatus.NOT_FOUND);
        }
        super.deleteById(id);
    }

    /**
     * Creates and saves a new organization.
     * Clears the organization cache to ensure fresh data is retrieved.
     *
     * @param params Organization entity containing new organization details.
     * @throws GlobalException if validation fails or the email/license number already exists.
     */
    @CacheEvict(value = "org", allEntries = true)
    public void saveOrg(OrgEntity params) {
        validateOrganizationFields(params);

        Optional<OrgEntity> existingOrganization =
                organizationRepository.findByLicenseNumberOrEmail(params.getLicenseNumber(), params.getEmail());

        Optional<UserEntity> existingUser =
                userRepo.findByEmail(params.getEmail());

        if (existingOrganization.isPresent() || existingUser.isPresent()) {
            throw new GlobalException("Organization Email or License Number already exists", HttpStatus.BAD_REQUEST);
        }

        OrgEntity organizationCreation = new OrgEntity();
        organizationCreation.setOrgName(params.getOrgName());
        organizationCreation.setEmail(params.getEmail());
        organizationCreation.setLocation(params.getLocation());
        organizationCreation.setPhoneNumber(params.getPhoneNumber());
        organizationCreation.setScope(params.getScope());
        organizationCreation.setPassword(passwordEncoder.encode(params.getPassword()));
        organizationCreation.setLicenseNumber(params.getLicenseNumber());
        organizationCreation.setModifiedDate(LocalDate.now());
        organizationCreation.setModifiedUser(params.getOrgName());
        organizationCreation.setCreatedDate(LocalDate.now());
        organizationCreation.setCreatedUser(params.getOrgName());

        organizationCreation = organizationRepository.save(organizationCreation);

        // Sending OTP verification email
        generalVerification.sendOtpEmail(organizationCreation);
    }

    /**
     * Changes the password of an organization.
     * Uses cache eviction to remove outdated password data.
     *
     * @param orgId       Organization ID.
     * @param oldPassword Current password.
     * @param newPassword New password.
     * @throws GlobalException if the organization is not found or the old password is incorrect.
     */
    @CacheEvict(value = "org", key = "#orgId")
    public void changeOrgPassword(Long orgId, String oldPassword, String newPassword) {
        OrgEntity org = findById(orgId);
        if (org == null) {
            throw new GlobalException("Organization not found with ID: " + orgId, HttpStatus.NOT_FOUND);
        }

        // Verify old password before changing it
        if (!passwordEncoder.matches(oldPassword, org.getPassword())) {
            throw new GlobalException("Old password is incorrect.", HttpStatus.BAD_REQUEST);
        }

        // Update and encode the new password
        org.setPassword(passwordEncoder.encode(newPassword));
        organizationRepository.save(org);
    }

    /**
     * Updates the Firebase Cloud Messaging (FCM) token for an organization.
     *
     * @param id       Organization ID.
     * @param fcmToken New FCM token.
     * @return ResponseEntity with success or failure message.
     */
    @CachePut(value = "org", key = "#id")
    public ResponseEntity<String> updateFcmToken(Long id, String fcmToken) {
        Optional<OrgEntity> orgOptional = organizationRepository.findById(id);

        if (orgOptional.isPresent()) {
            OrgEntity org = orgOptional.get();
            org.setFcmToken(fcmToken);
            organizationRepository.save(org);
            return ResponseEntity.ok("FCM token updated successfully");
        } else {
            return ResponseEntity.badRequest().body("Organization not found");
        }
    }

    /**
     * Validates the required fields of an organization entity.
     *
     * @param params Organization entity to validate.
     * @throws GlobalException if any required field is missing or incorrect.
     */
    private void validateOrganizationFields(OrgEntity params) {
        if (Objects.isNull(params.getLicenseNumber())) {
            throw new GlobalException("License Number is required.", HttpStatus.BAD_REQUEST);
        }
        if (Objects.isNull(params.getOrgName())) {
            throw new GlobalException("Organization name is required.", HttpStatus.BAD_REQUEST);
        }
        if (Objects.isNull(params.getPassword())) {
            throw new GlobalException("Password is required.", HttpStatus.BAD_REQUEST);
        }
        if (Objects.isNull(params.getPhoneNumber())) {
            throw new GlobalException("Mobile phone number is required.", HttpStatus.BAD_REQUEST);
        }
        if (Objects.isNull(params.getScope()) || !Scope.ORGANIZATION.equals(params.getScope())) {
            throw new GlobalException("Scope must be set to 'organization'.", HttpStatus.BAD_REQUEST);
        }
        if (Objects.isNull(params.getLocation())) {
            throw new GlobalException("Location is required.", HttpStatus.BAD_REQUEST);
        }
    }
}
