package com.spring.nuqta.donation.Services;

import com.spring.nuqta.base.Services.BaseServices;
import com.spring.nuqta.donation.Entity.DonEntity;
import com.spring.nuqta.donation.Repo.DonRepo;
import com.spring.nuqta.exception.GlobalException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DonServices extends BaseServices<DonEntity, Long> {

    private final DonRepo donRepository;

    @Override
    public List<DonEntity> findAll() {
        return super.findAll();
    }

    @Override
    public DonEntity findById(Long id) {
        // Validate the ID before calling the parent method
        if (id == null || id <= 0) {
            throw new GlobalException("Invalid " + id + ":" + id + " ID must be a positive non-null value.", HttpStatus.BAD_REQUEST);
        }

        // Call the parent class's method
        DonEntity entity = super.findById(id);

        // Optional: Add custom logic for handling null results
        if (entity == null) {
            throw new GlobalException("Entity not found for ID: " + id, HttpStatus.NOT_FOUND);
        }

        return entity;
    }

    public List<DonEntity> findNearestLocations(double latitude, double longitude) {
        // Query the nearest locations within the specified distance
        return donRepository.findNearestLocationWithin100km(latitude, longitude);
    }
}
