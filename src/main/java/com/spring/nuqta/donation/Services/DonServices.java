package com.spring.nuqta.donation.Services;

import com.spring.nuqta.base.Services.BaseServices;
import com.spring.nuqta.donation.Dto.AcceptDonationRequestDto;
import com.spring.nuqta.donation.Entity.DonEntity;
import com.spring.nuqta.donation.Repo.DonRepo;
import com.spring.nuqta.exception.GlobalException;
import com.spring.nuqta.notifications.Dto.NotificationRequest;
import com.spring.nuqta.notifications.Services.NotificationService;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.request.Entity.ReqEntity;
import com.spring.nuqta.request.Repo.ReqRepo;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DonServices extends BaseServices<DonEntity, Long> {

    private final DonRepo donRepository;
    private final ReqRepo reqRepository;
    private final NotificationService notificationService;

    /**
     * Retrieves all donations with caching.
     */
    @Override
    @Cacheable(value = "donation")
    public List<DonEntity> findAll() {
        return donRepository.findAll();
    }

    /**
     * Retrieves a donation by ID with caching.
     */
    @Override
    @Cacheable(value = "donation", key = "#id")
    public DonEntity findById(Long id) {
        if (id == null || id <= 0) {
            throw new GlobalException("Invalid ID: " + id, HttpStatus.BAD_REQUEST);
        }

        Optional<DonEntity> entity = donRepository.findById(id);
        if (entity.isEmpty()) {
            throw new GlobalException("Donation not found for ID: " + id, HttpStatus.NOT_FOUND);
        }

        log.info("Fetching donation with ID: {}", id);
        return entity.get();
    }

    /**
     * Retrieves the nearest donation locations with caching.
     */
    @Cacheable(value = "nearestLocations", key = "#latitude + '-' + #longitude")
    public List<DonEntity> findNearestLocations(double latitude, double longitude) {
        return donRepository.findNearestLocationWithin100km(latitude, longitude);
    }

    /**
     * Accepts a donation request and updates the cache.
     */
    @Transactional
    @CacheEvict(value = "donation", key = "#dto.donationId", allEntries = true)
    public DonEntity acceptDonationRequest(AcceptDonationRequestDto dto) {
        DonEntity donation = donRepository.findById(dto.getDonationId())
                .orElseThrow(() -> new GlobalException("Donation not found", HttpStatus.NOT_FOUND));

        ReqEntity request = reqRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new GlobalException("Request not found", HttpStatus.NOT_FOUND));

        if (donation.getAcceptedRequests().contains(request) || request.getDonations().contains(donation)) {
            throw new GlobalException("Request already accept", HttpStatus.CONFLICT);
        }

        donation.getAcceptedRequests().add(request);
        request.getDonations().add(donation);

        // Save both entities to persist the relationship
        reqRepository.save(request);
        DonEntity entity = donRepository.save(donation);

//        sendNotificationIfApplicable(donation, request);

        return entity;
    }

    @Transactional
    @CacheEvict(value = "donation", key = "#dto.donationId", allEntries = true)
    public void deleteAcceptedDonationRequest(AcceptDonationRequestDto dto) {
        DonEntity donation = donRepository.findById(dto.getDonationId())
                .orElseThrow(() -> new GlobalException("Donation not found", HttpStatus.NOT_FOUND));

        ReqEntity request = reqRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new GlobalException("Request not found", HttpStatus.NOT_FOUND));

        if (!donation.getAcceptedRequests().contains(request)) {
            throw new GlobalException("Request already deleted", HttpStatus.CONFLICT);
        }

        // Remove the relationship
        donation.getAcceptedRequests().remove(request);
        request.getDonations().remove(donation);

        // Save both entities to persist the relationship removal
        reqRepository.save(request);
        donRepository.save(donation);

        log.info("Donation ID {} removed for Request ID {}", dto.getDonationId(), dto.getRequestId());
    }

    /**
     * Sends a notification when a donation request is accepted.
     */
    void sendNotificationIfApplicable(DonEntity donation, ReqEntity request) {
        if (donation == null || request == null) {
            log.warn("Donation or request is null. Notification not sent.");
            return;
        }

        UserEntity donor = donation.getUser();
        if (donor == null || donor.getFcmToken() == null) {
            log.warn("Donor or FCM token is null. Notification not sent.");
            return;
        }

        String donorName = donor.getUsername();
        String message = "Your blood donation request has been accepted by " + donorName;

        // Send notification to the request user (if user exists and has an FCM token)
        Optional.ofNullable(request.getUser())
                .map(UserEntity::getFcmToken)
                .ifPresent(token -> {
                    notificationService.sendNotification(new NotificationRequest(token, "Request Accepted", message));
                });

        // Send notification to the request organization (if organization exists and has an FCM token)
        Optional.ofNullable(request.getOrganization())
                .map(OrgEntity::getFcmToken)
                .ifPresent(token -> {
                    notificationService.sendNotification(new NotificationRequest(token, "Request Accepted", message));
                });
    }
}
