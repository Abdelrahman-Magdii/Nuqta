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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

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

    private final MessageSource ms;


    /**
     * Retrieves all organizations from the database.
     * Uses caching to optimize retrieval performance.
     *
     * @return List of all organizations.
     * @throws GlobalException if no organizations are found.
     */
    @Override
    @Cacheable(value = "org", key = "'allOrg'")
    public List<OrgEntity> findAll() throws GlobalException {
        List<OrgEntity> organizations = organizationRepository.findAllByEnabledTrue();
        if (organizations.isEmpty()) {
            throw new GlobalException("org.no.organizations", HttpStatus.NOT_FOUND);
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
        Optional<OrgEntity> organization = organizationRepository.findByIdAndEnabledTrue(id);
        if (organization.isEmpty()) {
            String msg = messageParam(id, "org.not.found");
            throw new GlobalException(msg, HttpStatus.NOT_FOUND);
        }
        return organization.get();
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
    @CacheEvict(value = "org", allEntries = true) // Clear all cached users
    public OrgEntity update(OrgEntity entity) throws GlobalException {
        if (entity == null || entity.getId() == null) {
            throw new GlobalException("org.id.null", HttpStatus.BAD_REQUEST);
        }
        Optional<OrgEntity> organizationOptional = organizationRepository.findById(entity.getId());

        if (organizationOptional.isEmpty()) {
            String msg = messageParam(entity.getId(), "org.not.found");
            throw new GlobalException(msg, HttpStatus.NOT_FOUND);
        }

        if (organizationRepository.existsByOrgNameAndIdNot(entity.getOrgName(), entity.getId())) {
            throw new GlobalException("org.name.exists", HttpStatus.CONFLICT);
        }


        OrgEntity existingOrganization = organizationOptional.get();
        
        // Updating only selected fields to maintain data integrity
        existingOrganization.setOrgName(entity.getOrgName());
        existingOrganization.setCity(entity.getCity());
        existingOrganization.setConservatism(entity.getConservatism());
        existingOrganization.setPhoneNumber(entity.getPhoneNumber());

        existingOrganization.setModifiedDate(LocalDate.now());
        existingOrganization.setModifiedUser(entity.getOrgName());

        return organizationRepository.save(existingOrganization);
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
        boolean organization = organizationRepository.existsById(id);
        if (!organization) {
            String msg = messageParam(id, "org.not.found");

            throw new GlobalException(msg, HttpStatus.NOT_FOUND);
        }
        organizationRepository.deleteById(id);
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
            throw new GlobalException("org.email.license.exists", HttpStatus.BAD_REQUEST);
        }

        OrgEntity organizationCreation = new OrgEntity();
        organizationCreation.setOrgName(params.getOrgName());
        organizationCreation.setEmail(params.getEmail());
        organizationCreation.setCity(params.getCity());
        organizationCreation.setConservatism(params.getConservatism());
        organizationCreation.setPhoneNumber(params.getPhoneNumber());
        organizationCreation.setScope(params.getScope());
        organizationCreation.setPassword(passwordEncoder.encode(params.getPassword()));
        organizationCreation.setLicenseNumber(params.getLicenseNumber());
        organizationCreation.setFcmToken(params.getFcmToken());
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
        Optional<OrgEntity> org = organizationRepository.findById(orgId);

        if (org.isEmpty()) {
            String msg = messageParam(orgId, "org.not.found");
            throw new GlobalException(msg, HttpStatus.NOT_FOUND);
        }

        // Verify old password before changing it
        if (!passwordEncoder.matches(oldPassword, org.get().getPassword())) {
            throw new GlobalException("org.old.password.incorrect", HttpStatus.BAD_REQUEST);
        }

        // Update and encode the new password
        org.get().setPassword(passwordEncoder.encode(newPassword));
        organizationRepository.save(org.get());
    }

    /**
     * Updates the Firebase Cloud Messaging (FCM) token for an organization.
     *
     * @param id       Organization ID.
     * @param fcmToken New FCM token.
     * @return ResponseEntity with success or failure message.
     */
    @CacheEvict(value = "org", key = "#id")
    public ResponseEntity<?> updateFcmToken(Long id, String fcmToken) {
        Optional<OrgEntity> orgOptional = organizationRepository.findById(id);
        Map<String, String> response = new HashMap<>();

        if (orgOptional.isPresent()) {
            OrgEntity org = orgOptional.get();
            org.setFcmToken(fcmToken);
            organizationRepository.save(org);
            response.put("message", getMS("error.user.fcmToken.update"));
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            response.put("message", getMS("error.user.notFound"));
            return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Validates the required fields of an organization entity.
     *
     * @param params Organization entity to validate.
     * @throws GlobalException if any required field is missing or incorrect.
     */
    void validateOrganizationFields(OrgEntity params) {
        if (Objects.isNull(params.getLicenseNumber())) {
            throw new GlobalException("org.license.required", HttpStatus.BAD_REQUEST);
        }
        if (Objects.isNull(params.getOrgName())) {
            throw new GlobalException("org.name.required", HttpStatus.BAD_REQUEST);
        }
        if (Objects.isNull(params.getPassword())) {
            throw new GlobalException("org.password.required", HttpStatus.BAD_REQUEST);
        }
        if (Objects.isNull(params.getPhoneNumber())) {
            throw new GlobalException("org.phone.required", HttpStatus.BAD_REQUEST);
        }
        if (Objects.isNull(params.getScope()) || !Scope.ORGANIZATION.equals(params.getScope())) {
            throw new GlobalException("org.scope.required", HttpStatus.BAD_REQUEST);
        }
        if (Objects.isNull(params.getCity())) {
            throw new GlobalException("org.city.required", HttpStatus.BAD_REQUEST);
        }
        if (Objects.isNull(params.getConservatism())) {
            throw new GlobalException("org.conservatism.required", HttpStatus.BAD_REQUEST);
        }
        if (Objects.isNull(params.getFcmToken())) {
            throw new GlobalException("org.fcm.token.required", HttpStatus.BAD_REQUEST);
        }
    }

    public String messageParam(Long id, String message) {
        String[] msParam = {id != null ? id.toString() : "null"};
        return ms.getMessage(message, msParam, LocaleContextHolder.getLocale());
    }

    private String getMS(String messageKey) {
        return ms.getMessage(messageKey, null, LocaleContextHolder.getLocale());
    }
}
