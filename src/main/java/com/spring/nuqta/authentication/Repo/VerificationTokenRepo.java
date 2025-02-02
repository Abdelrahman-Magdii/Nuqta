package com.spring.nuqta.authentication.Repo;

import com.spring.nuqta.authentication.Entity.VerificationToken;
import com.spring.nuqta.base.Repo.BaseRepo;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepo extends BaseRepo<VerificationToken, Long> {

    VerificationToken findByToken(String token);

}
