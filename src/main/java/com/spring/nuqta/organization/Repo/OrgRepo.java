package com.spring.nuqta.organization.Repo;

import com.spring.nuqta.base.Repo.BaseRepo;
import com.spring.nuqta.organization.Entity.OrgEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrgRepo extends BaseRepo<OrgEntity, Long> {

    Optional<OrgEntity> findByEmail(String email);

    Optional<OrgEntity> findByLicenseNumber(String licenseNumber);

    Optional<OrgEntity> findByLicenseNumberOrEmail(String email, String licenseNumber);
}
