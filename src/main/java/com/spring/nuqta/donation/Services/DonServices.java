package com.spring.nuqta.donation.Services;

import com.spring.nuqta.base.Services.BaseServices;
import com.spring.nuqta.donation.Dto.AcceptDonationRequestDto;
import com.spring.nuqta.donation.Entity.DonEntity;
import com.spring.nuqta.donation.Repo.DonRepo;
import com.spring.nuqta.enums.DonStatus;
import com.spring.nuqta.exception.GlobalException;
import com.spring.nuqta.mail.Services.EmailService;
import com.spring.nuqta.mail.template.SendMailToDoner;
import com.spring.nuqta.mail.template.SendMailToDonerRejected;
import com.spring.nuqta.notifications.Dto.NotificationRequest;
import com.spring.nuqta.notifications.Services.NotificationService;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.request.Entity.ReqEntity;
import com.spring.nuqta.request.Repo.ReqRepo;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
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
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final MessageSource ms;

    /**
     * Retrieves all donations with caching.
     */
    @Cacheable(value = "donation")
    public List<DonEntity> findTopConservatism(String conservatism) {
        List<DonEntity> entities = donRepository.findFirstByConservatismContainingIgnoreCase(conservatism);
        if (entities.isEmpty()) {
            throw new GlobalException("error.donation.notFound", HttpStatus.NOT_FOUND);
        }
        return entities;
    }

    @Cacheable(value = "donation")
    public List<DonEntity> findTopCity(String city) {
        List<DonEntity> entities = donRepository.findFirstByCityContainingIgnoreCase(city);
        if (entities.isEmpty()) {
            throw new GlobalException("error.donation.notFound", HttpStatus.NOT_FOUND);
        }
        return entities;
    }

    /**
     * Retrieves a donation by ID with caching.
     */
    @Override
    @Cacheable(value = "donation", key = "#id")
    public DonEntity findById(Long id) {
        if (id == null || id <= 0) {
            String msg = messageParam(id, "error.invalid.id");
            throw new GlobalException(msg, HttpStatus.BAD_REQUEST);
        }

        Optional<DonEntity> entity = donRepository.findById(id);
        if (entity.isEmpty()) {
            String msg = messageParam(id, "error.donation.notFoundById");
            throw new GlobalException(msg, HttpStatus.NOT_FOUND);
        }

        return entity.get();
    }

    /**
     * Accepts a donation request and updates the cache.
     */
    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "donation", key = "#dto.donationId", allEntries = true),
                    @CacheEvict(value = "users", allEntries = true),
                    @CacheEvict(value = "requests", allEntries = true)
            })
    public DonEntity acceptDonationRequest(AcceptDonationRequestDto dto) throws MessagingException {
        DonEntity donation = donRepository.findById(dto.getDonationId())
                .orElseThrow(() -> new GlobalException("error.don.notFound", HttpStatus.NOT_FOUND));

        ReqEntity request = reqRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new GlobalException("error.request.notFound", HttpStatus.NOT_FOUND));

        if (donation.getAcceptedRequests().contains(request) || request.getDonations().contains(donation)) {
            throw new GlobalException("error.request.alreadyAccepted", HttpStatus.CONFLICT);
        }

        donation.getAcceptedRequests().add(request);
        request.getDonations().add(donation);
        donation.setStatus(DonStatus.INVALID);

        // Save both entities to persist the relationship
        reqRepository.save(request);
        DonEntity entity = donRepository.save(donation);

//        sendNotificationIfApplicable(donation, request);

        if (request.getUser() != null)
            this.sendMail(donation, request);
        return entity;
    }

    @Transactional
    @Caching(
            evict = {
                    @CacheEvict(value = "donation", key = "#dto.donationId", allEntries = true),
                    @CacheEvict(value = "users", allEntries = true),
                    @CacheEvict(value = "requests", allEntries = true)
            })
    public void deleteAcceptedDonationRequest(AcceptDonationRequestDto dto) throws MessagingException {
        DonEntity donation = donRepository.findById(dto.getDonationId())
                .orElseThrow(() -> new GlobalException("error.don.notFound", HttpStatus.NOT_FOUND));

        ReqEntity request = reqRepository.findById(dto.getRequestId())
                .orElseThrow(() -> new GlobalException("error.request.notFound", HttpStatus.NOT_FOUND));

        if (!donation.getAcceptedRequests().contains(request)) {
            throw new GlobalException("error.request.alreadyDeleted", HttpStatus.CONFLICT);
        }

        // Remove the relationship
        donation.getAcceptedRequests().remove(request);
        request.getDonations().remove(donation);
        donation.setStatus(DonStatus.VALID);

        // Save both entities to persist the relationship removal
        reqRepository.save(request);
        donRepository.save(donation);

        if (request.getUser() != null)
            this.sendMailRejected(donation, request);
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
        String[] msParam = {donorName};
        String message = ms.getMessage("notification.requestAcceptedMessage", msParam, LocaleContextHolder.getLocale());

        // Send notification to the request user (if user exists and has an FCM token)
        Optional.ofNullable(request.getUser())
                .map(UserEntity::getFcmToken)
                .ifPresent(token -> {
                    notificationService.sendNotification(new NotificationRequest(token, "notification.requestAcceptedTitle", message));
                });

        // Send notification to the request organization (if organization exists and has an FCM token)
        Optional.ofNullable(request.getOrganization())
                .map(OrgEntity::getFcmToken)
                .ifPresent(token -> {
                    notificationService.sendNotification(new NotificationRequest(token, "notification.requestAcceptedTitle", message));
                });
    }

    void sendMail(DonEntity donation, ReqEntity req) throws MessagingException {

        UserEntity donor = donation.getUser();
        if (donor == null || donor.getFcmToken() == null) {
            return;
        }

        SendMailToDoner context = new SendMailToDoner();
        context.init(req.getUser());
        context.buildVerificationUrl(donor);

        emailService.sendMail(context);

    }

    void sendMailRejected(DonEntity donation, ReqEntity req) throws MessagingException {

        UserEntity donor = donation.getUser();
        if (donor == null || donor.getFcmToken() == null) {
            return;
        }

        SendMailToDonerRejected context = new SendMailToDonerRejected();
        context.init(req.getUser());
        context.buildVerificationUrl(donor);

        emailService.sendMail(context);

    }

    public String messageParam(Long id, String message) {
        String[] msParam = {id != null ? id.toString() : "null"};
        return ms.getMessage(message, msParam, LocaleContextHolder.getLocale());
    }
}
