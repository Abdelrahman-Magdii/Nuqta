package com.spring.nuqta.request.Services;

import com.spring.nuqta.base.Services.BaseServices;
import com.spring.nuqta.exception.GlobalException;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.organization.Repo.OrgRepo;
import com.spring.nuqta.request.Entity.ReqEntity;
import com.spring.nuqta.request.Repo.ReqRepo;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import com.spring.nuqta.usermanagement.Repo.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ReqServices extends BaseServices<ReqEntity, Long> {

    @Autowired
    private ReqRepo reqRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private OrgRepo orgRepo;

    @Override
    public List<ReqEntity> findAll() throws GlobalException {
        List<ReqEntity> requests = super.findAll();
        if (requests.isEmpty()) {
            throw new GlobalException("No requests found", HttpStatus.NOT_FOUND);
        }
        return requests;
    }

    @Override
    public ReqEntity findById(Long id) throws GlobalException {
        return reqRepo.findById(id)
                .orElseThrow(() -> new GlobalException("Request not found with ID: " + id, HttpStatus.NOT_FOUND));
    }

    @Override
    public void deleteById(Long id) throws GlobalException {
        ReqEntity request = reqRepo.findById(id)
                .orElseThrow(() -> new GlobalException("Request not found with ID: " + id, HttpStatus.NOT_FOUND));
        super.deleteById(id);
    }


    public ReqEntity addRequest(Long userId, ReqEntity reqEntity) throws GlobalException {
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new GlobalException("User not found with ID: " + userId, HttpStatus.NOT_FOUND));
        reqEntity.setUser(user);
        return reqRepo.save(reqEntity);
    }

    public ReqEntity addRequestForOrg(Long orgId, ReqEntity reqEntity) throws GlobalException {
        OrgEntity org = orgRepo.findById(orgId)
                .orElseThrow(() -> new GlobalException("Organization not found with ID: " + orgId, HttpStatus.NOT_FOUND));
        reqEntity.setOrganization(org);
        return reqRepo.save(reqEntity);
    }

    @Override
    public ReqEntity update(ReqEntity entity) throws GlobalException {
        if (entity == null || entity.getId() == null) {
            throw new GlobalException("Request ID cannot be null", HttpStatus.BAD_REQUEST);
        }

        // Fetch the existing entity from the database
        ReqEntity existingEntity = reqRepo.findById(entity.getId())
                .orElseThrow(() -> new GlobalException("Request not found with ID: " + entity.getId(), HttpStatus.NOT_FOUND));

        // Update only the non-relationship fields
        existingEntity.setBloodTypeNeeded(entity.getBloodTypeNeeded());
        existingEntity.setAmount(entity.getAmount());
        existingEntity.setLocation(entity.getLocation());
        existingEntity.setAddress(entity.getAddress());
        existingEntity.setRequestDate(entity.getRequestDate());
        existingEntity.setStatus(entity.getStatus());
        existingEntity.setUrgencyLevel(entity.getUrgencyLevel());
        existingEntity.setPaymentAvailable(entity.getPaymentAvailable());

        // Save the updated entity (relationships remain unchanged)
        return reqRepo.save(existingEntity);
    }
}