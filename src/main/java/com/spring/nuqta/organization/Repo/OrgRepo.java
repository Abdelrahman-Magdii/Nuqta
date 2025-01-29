package com.spring.nuqta.organization.Repo;

import com.spring.nuqta.base.Repo.BaseRepo;
import com.spring.nuqta.organization.Entity.OrgEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrgRepo extends BaseRepo<OrgEntity, Long> {

    Optional<OrgEntity> findByEmail(String email);

    Optional<OrgEntity> findByLicenseNumber(String licenseNumber);

    Optional<OrgEntity> findByLicenseNumberOrEmail(@NotBlank(message = "License number cannot be blank") @Size(max = 50, message = "License number cannot exceed 50 characters") String licenseNumber, @NotBlank(message = "Email cannot be blank") @Email(message = "Email should be valid") String email);

}
