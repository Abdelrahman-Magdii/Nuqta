package com.spring.nuqta.organization.Services;

import com.spring.nuqta.base.Services.BaseServices;
import com.spring.nuqta.organization.Entity.OrgEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrgServices extends BaseServices<OrgEntity, Long> {


    @Override
    public List<OrgEntity> findAll() {
        return super.findAll();
    }

    @Override
    public OrgEntity findById(Long aLong) {
        return super.findById(aLong);
    }

    @Override
    public OrgEntity insert(OrgEntity entity) {
        return super.insert(entity);
    }


    @Override
    public OrgEntity update(OrgEntity entity) {
        return super.update(entity);
    }

    @Override
    public void deleteById(Long aLong) {
        super.deleteById(aLong);
    }
}
