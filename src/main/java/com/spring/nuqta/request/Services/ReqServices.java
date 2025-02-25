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
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Geometry;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * Service class to handle CRUD operations for blood donation requests.
 * Extends BaseServices to inherit common service functionalities.
 */
@Slf4j
@Service
@AllArgsConstructor
public class ReqServices extends BaseServices<ReqEntity, Long> {

    private static final double SEARCH_RADIUS = 10.000; // Defines a 10 KM search radius for nearby donors
    private final ReqRepo reqRepo;
    private final UserRepo userRepo;
    private final OrgRepo orgRepo;
    private final NotificationService notificationService;
    private final DonRepo donRepo;
    private final CacheManager cacheManager;

    /**
     * Retrieves all requests from the database.
     * Uses caching to improve performance.
     *
     * @return List of all requests
     * @throws GlobalException if no requests are found
     */
    @Override
    @Cacheable(value = "requests")
    public List<ReqEntity> findAll() throws GlobalException {
        List<ReqEntity> requests = super.findAll();
        if (requests.isEmpty()) {
            throw new GlobalException("No requests found", HttpStatus.NOT_FOUND);
        }
        return requests;
    }

    /**
     * Finds a request by its ID.
     * Uses caching to store request details.
     *
     * @param id Request ID
     * @return ReqEntity object corresponding to the given ID
     * @throws GlobalException if the request is not found
     */
    @Override
    @Cacheable(value = "requests", key = "#id")
    public ReqEntity findById(Long id) throws GlobalException {
        return reqRepo.findById(id)
                .orElseThrow(() -> new GlobalException("Request not found with ID: " + id, HttpStatus.NOT_FOUND));
    }

    /**
     * Deletes a request by its ID.
     * Ensures associated user and organization fields are set to null before deletion.
     * Evicts the cache entry for the deleted request.
     *
     * @param id Request ID
     * @throws GlobalException if the request is not found
     */
    @Override
    @Transactional
    public void deleteById(Long id) throws GlobalException {
        if (!reqRepo.existsById(id)) {
            throw new GlobalException("Request not found with ID: " + id, HttpStatus.NOT_FOUND);
        }
        reqRepo.hardDeleteById(id);
        Objects.requireNonNull(cacheManager.getCache("requests")).clear();
    }


    public void ReCache(Long id) throws GlobalException {
        ReqEntity request = reqRepo.findById(id)
                .orElseThrow(() -> new GlobalException("Request not found with ID: " + id, HttpStatus.NOT_FOUND));

        request.setUser(null);
        request.setOrganization(null);
        reqRepo.save(request);
        log.warn("Removed relations in ReCache for request ID: " + request.getId());

        // Manually clear the cache
        Objects.requireNonNull(cacheManager.getCache("requests")).evict(id);
        Objects.requireNonNull(cacheManager.getCache("users")).clear();
        Objects.requireNonNull(cacheManager.getCache("org")).clear();
    }

    /**
     * Creates a new request and links it to a user.
     * Sends notifications to nearby donors.
     * Clears cache after adding a new request.
     *
     * @param userId    ID of the user creating the request
     * @param reqEntity The request entity to be saved
     * @return The saved request entity
     * @throws GlobalException if the user is not found
     */
    @Caching(
            evict = {
                    @CacheEvict(value = "requests", allEntries = true),
                    @CacheEvict(value = "users", allEntries = true)

            },
            put = {
                    @CachePut(value = "requests", key = "#result.id"),

            } // Caches the new request by ID
    )
    public ReqEntity addRequest(Long userId, ReqEntity reqEntity) throws GlobalException {
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new GlobalException("User not found with ID: " + userId, HttpStatus.NOT_FOUND));
        reqEntity.setUser(user);

        // Send notifications to nearby donors
        this.SendNotification(reqEntity);

        // Set timestamps and user details
        reqEntity.setCreatedDate(LocalDate.now());
        reqEntity.setModifiedDate(LocalDate.now());
        reqEntity.setCreatedUser(user.getUsername());
        reqEntity.setModifiedUser(user.getUsername());

        return reqRepo.save(reqEntity);
    }

    /**
     * Creates a new request for an organization.
     * Sends notifications to nearby donors.
     * Clears cache after adding a new request.
     *
     * @param orgId     ID of the organization creating the request
     * @param reqEntity The request entity to be saved
     * @return The saved request entity
     * @throws GlobalException if the organization is not found
     */

    @Caching(
            evict = {
                    @CacheEvict(value = "requests", allEntries = true),
                    @CacheEvict(value = "org", allEntries = true)
            },
            put = {
                    @CachePut(value = "requests", key = "#result.id"),
            }// Caches the new request by ID
    )
    public ReqEntity addRequestForOrg(Long orgId, ReqEntity reqEntity) throws GlobalException {
        OrgEntity org = orgRepo.findById(orgId)
                .orElseThrow(() -> new GlobalException("Organization not found with ID: " + orgId, HttpStatus.NOT_FOUND));
        reqEntity.setOrganization(org);

        // Send notifications to nearby donors
        this.SendNotification(reqEntity);

        // Set timestamps and organization details
        reqEntity.setCreatedDate(LocalDate.now());
        reqEntity.setModifiedDate(LocalDate.now());
        reqEntity.setCreatedUser(org.getOrgName());
        reqEntity.setModifiedUser(org.getOrgName());

        return reqRepo.save(reqEntity);
    }

    /**
     * Updates an existing request with new details.
     * Does not update relationships (user and organization remain unchanged).
     * Updates cache with new request details.
     *
     * @param entity The updated request entity
     * @return The updated request entity
     * @throws GlobalException if the request ID is null or the request is not found
     */
    @Override
    @CachePut(value = "requests", key = "#entity.id")
    public ReqEntity update(ReqEntity entity) throws GlobalException {
        if (entity == null || entity.getId() == null) {
            throw new GlobalException("Request ID cannot be null", HttpStatus.BAD_REQUEST);
        }

        // Fetch the existing request entity
        ReqEntity existingEntity = reqRepo.findById(entity.getId())
                .orElseThrow(() -> new GlobalException("Request not found with ID: " + entity.getId(), HttpStatus.NOT_FOUND));

        // Update request details (excluding relationships)
        existingEntity.setBloodTypeNeeded(entity.getBloodTypeNeeded());
        existingEntity.setAmount(entity.getAmount());
        existingEntity.setLocation(entity.getLocation());
        existingEntity.setAddress(entity.getAddress());
        existingEntity.setRequestDate(entity.getRequestDate());
        existingEntity.setStatus(entity.getStatus());
        existingEntity.setUrgencyLevel(entity.getUrgencyLevel());
        existingEntity.setPaymentAvailable(entity.getPaymentAvailable());

        existingEntity.setModifiedDate(LocalDate.now());

        // Set modified user based on existing user or organization
        if (existingEntity.getUser() != null) {
            existingEntity.setModifiedUser(existingEntity.getUser().getUsername());
        } else {
            existingEntity.setModifiedUser(existingEntity.getOrganization().getOrgName());
        }

        return reqRepo.save(existingEntity);
    }

    /**
     * Sends notifications to nearby donors about a new request.
     *
     * @param reqEntity The request entity
     * @throws GlobalException if any error occurs while sending notifications
     */
    public void SendNotification(ReqEntity reqEntity) throws GlobalException {
        List<DonEntity> nearbyDonors = findNearbyDonors(reqEntity.getLocation());

        for (DonEntity donor : nearbyDonors) {
            if (donor.getUser().getFcmToken() != null) {
                notificationService.sendNotification(new NotificationRequest(
                        donor.getUser().getFcmToken(),
                        "Urgent Blood Request!",
                        "A new blood donation request has been posted near you from " + reqEntity.getUser().getUsername()
                ));
            }
        }
    }

    /**
     * Finds nearby donors within a given radius.
     *
     * @param requestLocation The location of the request
     * @return List of nearby donors
     */
    public List<DonEntity> findNearbyDonors(Geometry requestLocation) {
        return donRepo.findNearbyDonors(requestLocation, SEARCH_RADIUS);
    }

}
