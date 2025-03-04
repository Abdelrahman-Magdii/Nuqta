package com.spring.nuqta.donation.Repo;

import com.spring.nuqta.base.Repo.BaseRepo;
import com.spring.nuqta.donation.Entity.DonEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DonRepo extends BaseRepo<DonEntity, Long> {


    @Query(value = "SELECT * FROM donation r " +
            "WHERE ST_DWithin(r.location, ST_SetSRID(ST_Point(:longitude, :latitude), 4326), 10.000) " +
            "ORDER BY ST_Distance(r.location, ST_SetSRID(ST_Point(:longitude, :latitude), 4326)) ASC",
            nativeQuery = true)
    List<DonEntity> findNearestLocationWithin100km(@Param("longitude") double longitude, @Param("latitude") double latitude);

    List<DonEntity> findTopByCity(String City);
}
