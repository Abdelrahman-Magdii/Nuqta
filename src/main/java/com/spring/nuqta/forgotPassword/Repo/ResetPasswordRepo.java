package com.spring.nuqta.forgotPassword.Repo;

import com.spring.nuqta.forgotPassword.Entity.ResetPasswordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetPasswordRepo extends JpaRepository<ResetPasswordEntity, Long> {

    ResetPasswordEntity findByOtp(String otp);

    ResetPasswordEntity findByOrganization_Email(String email);

    ResetPasswordEntity findByUser_Email(String email);
}
