package com.spring.nuqta.request.Services;

import com.spring.nuqta.base.Services.BaseServices;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.organization.Repo.OrgRepo;
import com.spring.nuqta.request.Entity.ReqEntity;
import com.spring.nuqta.request.Repo.ReqRepo;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import com.spring.nuqta.usermanagement.Repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReqServices extends BaseServices<ReqEntity, Long> {

    @Autowired
    private ReqRepo reqRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private OrgRepo orgRepo;


    // Add a new request for a specific user
    public ReqEntity addRequest(Long userId, ReqEntity reqEntity) {
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + userId));
        reqEntity.setUser(user);
        return reqRepo.save(reqEntity);
    }

    // Add a new request for a specific user
    public ReqEntity addRequestForOrg(Long OrgId, ReqEntity reqEntity) {
        OrgEntity org = orgRepo.findById(OrgId)
                .orElseThrow(() -> new RuntimeException("Organization not found with ID: " + OrgId));
        reqEntity.setOrganization(org);
        return reqRepo.save(reqEntity);
    }

}
