package com.spring.nuqta.request.Services;

import com.google.firebase.messaging.FirebaseMessagingException;
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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Service class to handle CRUD operations for blood donation requests.
 * Extends BaseServices to inherit common service functionalities.
 */
@Slf4j
@Service
@AllArgsConstructor
public class ReqServices extends BaseServices<ReqEntity, Long> {

    private final ReqRepo reqRepo;
    private final UserRepo userRepo;
    private final OrgRepo orgRepo;
    private final NotificationService notificationService;
    private final DonRepo donRepo;
    private final CacheManager cacheManager;
    private final MessageSource ms;

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
        List<ReqEntity> requests = reqRepo.findAll();
        if (requests.isEmpty()) {
            throw new GlobalException("error.request.no_requests", HttpStatus.NOT_FOUND);
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
        validId(id);
        String msg = messageParam(id, "error.request.notfound");
        return reqRepo.findById(id)
                .orElseThrow(() -> new GlobalException(msg, HttpStatus.NOT_FOUND));
    }

    /**
     * Get all requests by user ID
     */
    public List<ReqEntity> getRequestsByUserId(Long userId) {
        validId(userId);
        return reqRepo.findByUserId(userId);
    }

    /**
     * Get all requests by organization ID
     */
    public List<ReqEntity> getRequestsByOrgId(Long orgId) {
        validId(orgId);
        return reqRepo.findByOrganizationId(orgId);
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
        validId(id);
        if (!reqRepo.existsById(id)) {
            String msg = messageParam(id, "error.request.notfound");

            throw new GlobalException(msg, HttpStatus.NOT_FOUND);
        }
        reqRepo.hardDeleteById(id);

        // Invalidate caches
        if (cacheManager != null) {
            Cache requestsCache = cacheManager.getCache("requests");
            Cache usersCache = cacheManager.getCache("users");
            Cache orgCache = cacheManager.getCache("org");

            if (requestsCache != null) requestsCache.clear();
            if (usersCache != null) usersCache.clear();
            if (orgCache != null) orgCache.clear();
        }
    }


    public void ReCache(Long id) throws GlobalException {
        // Fetch the request entity
        String msg = messageParam(id, "error.request.notfound");

        ReqEntity request = reqRepo.findById(id)
                .orElseThrow(() -> new GlobalException(msg, HttpStatus.NOT_FOUND));

        // Remove relations
        request.setUser(null);
        request.setOrganization(null);
        reqRepo.save(request);
    }

    @Caching(
            evict = {
                    @CacheEvict(value = "requests", allEntries = true),
                    @CacheEvict(value = "users", allEntries = true, condition = "!#isOrg"),
                    @CacheEvict(value = "org", allEntries = true, condition = "#isOrg")
            },
            put = {
                    @CachePut(value = "requests", key = "#result.id")
            }
    )
    public ReqEntity addRequest(ReqEntity reqEntity, Long id, boolean isOrg)
            throws GlobalException, FirebaseMessagingException {

        // Common validation and setup
        validId(id);
        reqEntity.setCreatedDate(LocalDate.now());
        reqEntity.setModifiedDate(LocalDate.now());

        if (isOrg) {
            handleOrgRequest(reqEntity, id);
        } else {
            handleUserRequest(reqEntity, id);
        }

        // Common notification logic
        // this.SendNotification(reqEntity);

        return reqRepo.save(reqEntity);
    }

    private void handleOrgRequest(ReqEntity reqEntity, Long orgId) throws GlobalException {
        String msg = messageParam(orgId, "error.org.notfound");
        OrgEntity org = orgRepo.findById(orgId)
                .orElseThrow(() -> new GlobalException(msg, HttpStatus.NOT_FOUND));
        reqEntity.setOrganization(org);
        reqEntity.setCreatedUser(org.getOrgName());
        reqEntity.setModifiedUser(org.getOrgName());
    }

    private void handleUserRequest(ReqEntity reqEntity, Long userId) throws GlobalException {
        String msg = messageParam(userId, "error.request.notfound");
        UserEntity user = userRepo.findById(userId)
                .orElseThrow(() -> new GlobalException(msg, HttpStatus.NOT_FOUND));
        reqEntity.setUser(user);
        reqEntity.setCreatedUser(user.getUsername());
        reqEntity.setModifiedUser(user.getUsername());
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
    @Caching(
            evict = {
                    @CacheEvict(value = "requests", allEntries = true),
                    @CacheEvict(value = "users", allEntries = true),
                    @CacheEvict(value = "org", allEntries = true)
            },
            put = {
                    @CachePut(value = "requests", key = "#result.id")
            }
    )
    public ReqEntity update(ReqEntity entity) throws GlobalException {
        if (entity == null || entity.getId() == null) {
            throw new GlobalException("error.request.id.null", HttpStatus.BAD_REQUEST);
        }

        String msg = messageParam(entity.getId(), "error.request.notfound");

        // Fetch the existing request entity
        ReqEntity existingEntity = reqRepo.findById(entity.getId())
                .orElseThrow(() -> new GlobalException(msg, HttpStatus.NOT_FOUND));

        // Update request details (excluding relationships)
        existingEntity.setBloodTypeNeeded(entity.getBloodTypeNeeded());
        existingEntity.setAmount(entity.getAmount());
        existingEntity.setCity(entity.getCity());
        existingEntity.setConservatism(entity.getConservatism());
        existingEntity.setRequestDate(entity.getRequestDate());
        existingEntity.setStatus(entity.getStatus());
        existingEntity.setUrgencyLevel(entity.getUrgencyLevel());
        existingEntity.setPaymentAvailable(entity.getPaymentAvailable());

        existingEntity.setModifiedDate(LocalDate.now());

        // Set modified user based on existing user or organization
        if (existingEntity.getUser() != null) {
            existingEntity.setModifiedUser(existingEntity.getUser().getUsername());
        } else if (existingEntity.getOrganization() != null) {
            existingEntity.setModifiedUser(existingEntity.getOrganization().getOrgName());
        } else {
            existingEntity.setModifiedUser("Unknown"); // Or handle it appropriately
        }


        return reqRepo.save(existingEntity);
    }

    /**
     * Sends notifications to nearby donors about a new request.
     *
     * @param reqEntity The request entity
     * @throws GlobalException if any error occurs while sending notifications
     */
    public void SendNotification(ReqEntity reqEntity) throws GlobalException, FirebaseMessagingException {
        // Ensure reqEntity has a user or organization
        if (reqEntity.getUser() == null && reqEntity.getOrganization() == null) {
            throw new GlobalException("error.request.no_user_or_org", HttpStatus.BAD_REQUEST);
        }

        // Find nearby donors
        List<DonEntity> nearbyDonors = findNearbyDonors(reqEntity.getCity(), reqEntity.getConservatism());

        // Get sender name
        String senderName = (reqEntity.getUser() != null)
                ? reqEntity.getUser().getUsername()
                : reqEntity.getOrganization().getOrgName();

        // Send notifications to nearby donors
        for (DonEntity donor : nearbyDonors) {
            if (donor.getUser().getFcmToken() != null) {
                String[] msParam = {senderName};
                String msg = ms.getMessage("notification.bloodRequest.message", msParam, LocaleContextHolder.getLocale());

                notificationService.sendNotification(new NotificationRequest(
                        donor.getUser().getFcmToken(),
                        "notification.bloodRequest.title", msg
                ));
            }
        }
    }

    /**
     * Finds nearby donors within a given radius.
     *
     * @param city The location of the request
     * @return List of nearby donors
     */
    public List<DonEntity> findNearbyDonors(String city, String conservatism) {
        return donRepo.findTopByCityOrConservatism(city, conservatism);
    }

    public void validId(Long id) {
        if (id == null || id <= 0) {
            String msg = messageParam(id, "error.user.invalid.id");
            throw new GlobalException(msg, HttpStatus.BAD_REQUEST);
        }
    }

    public String messageParam(Long id, String message) {
        String[] msParam = {id != null ? id.toString() : "null"};
        return ms.getMessage(message, msParam, LocaleContextHolder.getLocale());
    }
}
