package com.spring.nuqta.donation.Services;

import com.spring.nuqta.base.Services.BaseServices;
import com.spring.nuqta.donation.Entity.DonEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class DonServices extends BaseServices<DonEntity, Long> {

    @Override
    public List<DonEntity> findAll() {
        return super.findAll();
    }

    @Override
    public DonEntity findById(Long id) {
        // Validate the ID before calling the parent method
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid " + id + ":" + id + " ID must be a positive non-null value.");
        }

        // Call the parent class's method
        DonEntity entity = super.findById(id);

        // Optional: Add custom logic for handling null results
        if (entity == null) {
            throw new IllegalStateException("Entity not found for ID: " + id);
        }

        return entity;
    }
    
    @Override
    public void deleteById(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Invalid " + id + ":" + id + " must be a positive non-null value.");
        }

        // Optional: Custom logic to check if the entity exists before deleting
        if (Objects.equals(super.findById(id), new DonEntity())) {
            throw new IllegalStateException("Cannot delete entity with the given ID :" + id + "does not exist.");
        }

        super.deleteById(id);
    }
}
