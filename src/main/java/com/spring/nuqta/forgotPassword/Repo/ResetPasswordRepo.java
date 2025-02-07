package com.spring.nuqta.forgotPassword.Repo;

import com.spring.nuqta.forgotPassword.Entity.ResetPasswordEntity;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetPasswordRepo extends JpaRepository<ResetPasswordEntity, Long> {

    ResetPasswordEntity findByOtp(String otp);

    ResetPasswordEntity findByOrganization(OrgEntity organization);

    ResetPasswordEntity findByUser(UserEntity user);
}
