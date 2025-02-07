package com.spring.nuqta.verificationToken.Repo;

import com.spring.nuqta.base.Repo.BaseRepo;
import com.spring.nuqta.verificationToken.Entity.VerificationToken;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepo extends BaseRepo<VerificationToken, Long> {

    VerificationToken findByToken(String token);

}
