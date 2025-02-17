package com.spring.nuqta.request.Services;

import com.spring.nuqta.base.Services.BaseServices;
import com.spring.nuqta.donation.Entity.DonEntity;
import com.spring.nuqta.donation.Repo.DonRepo;
import com.spring.nuqta.exception.GlobalException;
import com.spring.nuqta.notifications.Dto.NotificationRequest;
import com.spring.nuqta.notifications.Services.NotificationService;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.organization.Repo.OrgRepo;
import com.spring.nuqta.request.Entity.ReqEntity;
import com.spring.nuqta.request.Repo.ReqRepo;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import com.spring.nuqta.usermanagement.Repo.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Geometry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class ReqServices extends BaseServices<ReqEntity, Long> {

    private static final double SEARCH_RADIUS = 10.000; // 10 KM radius
    @Autowired
    private ReqRepo reqRepo;
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private OrgRepo orgRepo;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private DonRepo donRepo;

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

        this.SendNotification(reqEntity);
        reqEntity.setCreatedDate(LocalDate.now());
        reqEntity.setModifiedDate(LocalDate.now());
        reqEntity.setCreatedUser(user.getUsername());
        reqEntity.setModifiedUser(user.getUsername());
        reqEntity = reqRepo.save(reqEntity);
        return reqEntity;
    }

    public ReqEntity addRequestForOrg(Long orgId, ReqEntity reqEntity) throws GlobalException {
        OrgEntity org = orgRepo.findById(orgId)
                .orElseThrow(() -> new GlobalException("Organization not found with ID: " + orgId, HttpStatus.NOT_FOUND));
        reqEntity.setOrganization(org);

        this.SendNotification(reqEntity);
        reqEntity.setCreatedDate(LocalDate.now());
        reqEntity.setModifiedDate(LocalDate.now());
        reqEntity.setCreatedUser(org.getOrgName());
        reqEntity.setModifiedUser(org.getOrgName());

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

        existingEntity.setModifiedDate(LocalDate.now());
        existingEntity.setModifiedUser(entity.getUser().getUsername());
        // Save the updated entity (relationships remain unchanged)
        return reqRepo.save(existingEntity);
    }

    public void SendNotification(ReqEntity reqEntity) throws GlobalException {
        // Get nearby donors
        List<DonEntity> nearbyDonors = findNearbyDonors(reqEntity.getLocation());

        // Send notifications to nearby donors
        for (DonEntity donor : nearbyDonors) {
            if (donor.getUser().getFcmToken() != null) {
                notificationService.sendNotification(new NotificationRequest(
                        donor.getUser().getFcmToken(),
                        "Urgent Blood Request!",
                        "A new blood donation request has been posted near you from" + reqEntity.getUser().getUsername()
                ));
            }
        }
    }

    public List<DonEntity> findNearbyDonors(Geometry requestLocation) {
        return donRepo.findNearbyDonors(requestLocation, SEARCH_RADIUS);
    }

}