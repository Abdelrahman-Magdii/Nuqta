package com.spring.nuqta.organization.Services;

import com.spring.nuqta.authentication.Dto.AuthOrgDto;
import com.spring.nuqta.authentication.Jwt.JwtUtilsOrganization;
import com.spring.nuqta.base.Services.BaseServices;
import com.spring.nuqta.enums.Scope;
import com.spring.nuqta.exception.GlobalException;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.organization.Repo.OrgRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrgServices extends BaseServices<OrgEntity, Long> {

    private final OrgRepo organizationRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtilsOrganization jwtUtilsOrganization;

    @Override
    public List<OrgEntity> findAll() throws GlobalException {
        List<OrgEntity> organizations = super.findAll();
        if (organizations.isEmpty()) {
            throw new GlobalException("No organizations found", HttpStatus.NOT_FOUND);
        }
        return organizations;
    }

    @Override
    public OrgEntity findById(Long aLong) throws GlobalException {
        OrgEntity organization = super.findById(aLong);
        if (organization == null) {
            throw new GlobalException("Organization not found with ID: " + aLong, HttpStatus.NOT_FOUND);
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
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        return super.update(entity);
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
    public AuthOrgDto create(OrgEntity params) {
        validateOrganizationFields(params);

        Optional<OrgEntity> organization =
                organizationRepository.findByLicenseNumberOrEmail(params.getEmail(), params.getLicenseNumber());

        log.info("Creating organization with : " + params.getLicenseNumber());

        if (organization.isPresent()) {
            throw new GlobalException("Organization Email is exist", HttpStatus.BAD_REQUEST);
        }

        OrgEntity organizationCreation = new OrgEntity(params.getOrgName(),
                params.getEmail(), passwordEncoder.encode(params.getPassword()), params.getLocation(), params.getPhoneNumber(), params.getLicenseNumber(), params.getScope());

        organizationCreation = organizationRepository.save(organizationCreation);


        //         create token
        String token = jwtUtilsOrganization.generateToken(organizationCreation);

        AuthOrgDto orgDto = new AuthOrgDto(organizationCreation.getId(), token,
                String.valueOf(jwtUtilsOrganization.getExpireAt(token)),
                jwtUtilsOrganization.createRefreshToken(organizationCreation),
                organizationCreation.getScope());

        return orgDto;
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


}