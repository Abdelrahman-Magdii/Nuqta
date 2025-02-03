package com.spring.nuqta.OtpMail.Repo;

import com.spring.nuqta.OtpMail.Entity.OtpVerifyEntity;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OtpVerifyRepo extends JpaRepository<OtpVerifyEntity, Long> {

    OtpVerifyEntity findByOtp(String otp);

    OtpVerifyEntity findByUser(UserEntity user);

    OtpVerifyEntity findByOrganization(OrgEntity organization);
}
