package com.spring.nuqta.donation.Services;

import com.google.firebase.messaging.FirebaseMessagingException;
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
        log.info("Fetching all donations from DB");
        return super.findAll();
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

        DonEntity entity = super.findById(id);
        if (entity == null) {
            throw new GlobalException("Donation not found for ID: " + id, HttpStatus.NOT_FOUND);
        }

        log.info("Fetching donation with ID: {}", id);
        return entity;
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
    @CacheEvict(value = "donation", key = "#dto.getDonationId()", allEntries = true)
    public DonEntity acceptDonationRequest(AcceptDonationRequestDto dto) {
        DonEntity donation = donRepository.findById(dto.getDonationId())
                .orElseThrow(() -> new GlobalException("Donation not found", HttpStatus.NOT_FOUND));

        ReqEntity request = reqRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new GlobalException("Request not found", HttpStatus.NOT_FOUND));

        donation.setRequest(request);
        DonEntity entity = donRepository.save(donation);

        sendNotificationIfApplicable(donation, request);

        log.info("Donation ID {} accepted for Request ID {}", dto.getDonationId(), dto.getRequestId());
        return entity;
    }

    /**
     * Sends a notification when a donation request is accepted.
     */
    private void sendNotificationIfApplicable(DonEntity donation, ReqEntity request) {
        String donorName = donation.getUser().getUsername();
        String message = "Your blood donation request has been accepted by " + donorName;

        Optional.ofNullable(request.getUser())
                .map(UserEntity::getFcmToken)
                .ifPresent(token -> {
                    try {
                        sendNotification(token, message);
                    } catch (FirebaseMessagingException e) {
                        throw new RuntimeException(e);
                    }
                });

        Optional.ofNullable(request.getOrganization())
                .map(OrgEntity::getFcmToken)
                .ifPresent(token -> {
                    try {
                        sendNotification(token, message);
                    } catch (FirebaseMessagingException e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    private void sendNotification(String fcmToken, String message) throws FirebaseMessagingException {
        notificationService.sendNotification(new NotificationRequest(fcmToken, "Request Accepted", message));
    }
}
