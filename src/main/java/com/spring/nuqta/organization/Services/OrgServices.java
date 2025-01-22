package com.spring.nuqta.organization.Services;

import com.spring.nuqta.base.Services.BaseServices;
import com.spring.nuqta.exception.GlobalException;
import com.spring.nuqta.organization.Entity.OrgEntity;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrgServices extends BaseServices<OrgEntity, Long> {

    @Override
    public List<OrgEntity> findAll() throws GlobalException {
        List<OrgEntity> organizations = super.findAll();
        if (organizations.isEmpty()) {
            throw new GlobalException("No organizations found", HttpStatus.NOT_FOUND);
        }
        return organizations;
    }

    @Override
    public OrgEntity findById(Long aLong) throws GlobalException {
        OrgEntity organization = super.findById(aLong);
        if (organization == null) {
            throw new GlobalException("Organization not found with ID: " + aLong, HttpStatus.NOT_FOUND);
        }
        return organization;
    }


    @Override
    public OrgEntity insert(OrgEntity entity) throws GlobalException {
        if (entity == null || entity.getOrg_name() == null || entity.getOrg_name().isEmpty()) {
            throw new GlobalException("Organization name cannot be empty", HttpStatus.BAD_REQUEST);
        }
        return super.insert(entity);
    }

    @Override
    public OrgEntity update(OrgEntity entity) throws GlobalException {
        if (entity == null || entity.getId() == null) {
            throw new GlobalException("Organization ID cannot be null", HttpStatus.BAD_REQUEST);
        }
        OrgEntity existingOrganization = super.findById(entity.getId());
        if (existingOrganization == null) {
            throw new GlobalException("Organization not found with ID: " + entity.getId(), HttpStatus.NOT_FOUND);
        }
        return super.update(entity);
    }

    @Override
    public void deleteById(Long aLong) throws GlobalException {
        OrgEntity organization = super.findById(aLong);
        if (organization == null) {
            throw new GlobalException("Organization not found with ID: " + aLong, HttpStatus.NOT_FOUND);
        }
        super.deleteById(aLong);
    }
}