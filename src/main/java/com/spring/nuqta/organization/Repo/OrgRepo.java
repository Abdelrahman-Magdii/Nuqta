package com.spring.nuqta.organization.Repo;

import com.spring.nuqta.base.Repo.BaseRepo;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.organization.Projection.OrgAuthProjection;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrgRepo extends BaseRepo<OrgEntity, Long> {

    List<OrgEntity> findAllByEnabledTrue();

    Optional<OrgEntity> findByIdAndEnabledTrue(Long id);

    Optional<OrgEntity> findByEmail(String email);

    Optional<OrgEntity> findByLicenseNumberOrEmail(@NotBlank(message = "License number cannot be blank") @Size(max = 50, message = "License number cannot exceed 50 characters") String licenseNumber, @NotBlank(message = "Email cannot be blank") @Email(message = "Email should be valid") String email);

    Optional<OrgAuthProjection> findOrgAuthProjectionByEmail(String email);

    Optional<OrgAuthProjection> findOrgAuthProjectionByLicenseNumber(String licenseNumber);

    boolean existsByEmail(String mail);

    boolean existsById(Long id);

    boolean existsByOrgNameAndIdNot(@NotBlank(message = "Organization name cannot be blank") @Size(max = 100, message = "Organization name cannot exceed 100 characters") String orgName, Long id);

    Optional<OrgAuthProjection> findOrgAuthProjectionByLicenseNumberOrEmail(@NotBlank(message = "License number cannot be blank") @Size(max = 50, message = "License number cannot exceed 50 characters") String licenseNumber, @NotBlank(message = "Email cannot be blank") @Email(message = "Email should be valid") String email);
}
