package com.spring.nuqta.donation.Repo;

import com.spring.nuqta.base.Repo.BaseRepo;
import com.spring.nuqta.donation.Entity.DonEntity;
import com.spring.nuqta.enums.DonStatus;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonRepo extends BaseRepo<DonEntity, Long> {

    List<DonEntity> findTopByCityOrConservatism(String city, String conservatism);

    List<DonEntity> findFirstByCityContainingIgnoreCase(String city);

    List<DonEntity> findFirstByConservatismContainingIgnoreCase(String conservatism);

    List<DonEntity> findByStatusAndConfirmDonate(DonStatus status, Boolean confirmDonate);
}
