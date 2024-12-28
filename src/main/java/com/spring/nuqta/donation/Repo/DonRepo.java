package com.spring.nuqta.donation.Repo;

import com.spring.nuqta.base.Repo.BaseRepo;
import com.spring.nuqta.donation.Entity.DonEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface DonRepo extends BaseRepo<DonEntity, Long> {


}
