package com.spring.nuqta.organization.Services;

import com.spring.nuqta.base.Services.BaseServices;
import com.spring.nuqta.enums.Scope;
import com.spring.nuqta.exception.GlobalException;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.organization.Repo.OrgRepo;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import com.spring.nuqta.usermanagement.Repo.UserRepo;
import com.spring.nuqta.verificationToken.General.GeneralVerification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrgServices extends BaseServices<OrgEntity, Long> {

    private final OrgRepo organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final GeneralVerification generalVerification;
    private final UserRepo userRepo;

    @Override
    public List<OrgEntity> findAll() throws GlobalException {
        List<OrgEntity> organizations = super.findAll();
        if (organizations.isEmpty()) {
            throw new GlobalException("No organizations found", HttpStatus.NOT_FOUND);
        }
        return organizations;
    }

    @Override
    public OrgEntity findById(Long id) throws GlobalException {
        OrgEntity organization = super.findById(id);
        if (organization == null) {
            throw new GlobalException("Organization not found with ID: " + id, HttpStatus.NOT_FOUND);
        }
        return organization;
    }

    @Override
    public OrgEntity update(OrgEntity entity) throws GlobalException {
        if (entity == null || entity.getId() == null) {
            throw new GlobalException("Organization ID cannot be null", HttpStatus.BAD_REQUEST);
        }
        OrgEntity existingOrganization = super.findById(entity.getId());
        if (existingOrganization == null) {
            throw new GlobalException("Organization not found with ID: " + entity.getId(), HttpStatus.NOT_FOUND);
        }
        existingOrganization.setId(entity.getId());
//        existingOrganization.setEmail(entity.getEmail());
//        existingOrganization.setLicenseNumber(entity.getLicenseNumber());
        existingOrganization.setOrgName(entity.getOrgName());
//        existingOrganization.setPassword(passwordEncoder.encode(entity.getPassword()));
        existingOrganization.setLocation(entity.getLocation());
        existingOrganization.setPhoneNumber(entity.getPhoneNumber());
        existingOrganization.setScope(entity.getScope());
        existingOrganization.setModifiedDate(LocalDate.now());
        existingOrganization.setModifiedUser(entity.getOrgName());
        return super.update(existingOrganization);
    }

    @Override
    public void deleteById(Long aLong) throws GlobalException {
        OrgEntity organization = super.findById(aLong);
        if (organization == null) {
            throw new GlobalException("Organization not found with ID: " + aLong, HttpStatus.NOT_FOUND);
        }
        super.deleteById(aLong);
    }

    @Transactional
    public void saveOrg(OrgEntity params) {
        validateOrganizationFields(params);

        Optional<OrgEntity> existingOrganization =
                organizationRepository.findByLicenseNumberOrEmail(params.getLicenseNumber(), params.getEmail());

        Optional<UserEntity> existingUser =
                userRepo.findByEmail(params.getEmail());

        if (existingOrganization.isPresent() || existingUser.isPresent()) {
            throw new GlobalException("Organization Email or License Number is exist", HttpStatus.BAD_REQUEST);
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


        generalVerification.sendOtpEmail(organizationCreation);

    }

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
        if (Objects.isNull(params.getScope())) {
            throw new GlobalException("Scope is required.", HttpStatus.BAD_REQUEST);
        }

        if (!(Scope.ORGANIZATION.equals(params.getScope()))) {
            throw new GlobalException("Scope must be set to 'organization'.", HttpStatus.BAD_REQUEST);
        }

        if (Objects.isNull(params.getLocation())) {
            throw new GlobalException("Location is required.", HttpStatus.BAD_REQUEST);
        }

    }

    @Transactional
    public void changeOrgPassword(Long orgId, String oldPassword, String newPassword) {
        OrgEntity org = findById(orgId);
        if (org == null) {
            throw new GlobalException("Organization not found with ID: " + orgId, HttpStatus.NOT_FOUND);
        }

        // Verify old password
        if (!passwordEncoder.matches(oldPassword, org.getPassword())) {
            throw new GlobalException("Old password is incorrect.", HttpStatus.BAD_REQUEST);
        }

        // Encode and update new password
        org.setPassword(passwordEncoder.encode(newPassword));
        organizationRepository.save(org);
    }

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

}