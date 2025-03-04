package com.spring.nuqta.donation.Services;

import com.google.firebase.messaging.FirebaseMessagingException;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DonServicesTest {

    @Mock
    private DonRepo donRepository;

    @Mock
    private ReqRepo reqRepository;

    @Mock
    private NotificationService notificationService;

    @Mock
    private Logger mockLogger;

    @InjectMocks
    private DonServices donServices;

    private DonEntity donation;
    private ReqEntity request;
    private AcceptDonationRequestDto dto;

    @BeforeEach
    void setUp() {
        donation = new DonEntity();
        donation.setId(1L);

        UserEntity donor = new UserEntity();
        donor.setId(1L);
        donor.setFcmToken("donor-fcm-token"); // Set a valid FCM token
        donation.setUser(donor); // Set the user for the donation

        request = new ReqEntity();
        request.setId(1L);

        UserEntity requestUser = new UserEntity();
        requestUser.setId(2L);
        requestUser.setFcmToken("request-user-fcm-token"); // Set a valid FCM token
        request.setUser(requestUser); // Set the user for the request

        dto = new AcceptDonationRequestDto();
        dto.setDonationId(1L);
        dto.setRequestId(1L);
    }

    @Test
    void testFindAll() {
        // Arrange
        when(donRepository.findAll()).thenReturn(Collections.singletonList(donation));

        // Act
        List<DonEntity> result = donServices.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(donRepository, times(1)).findAll();
    }

    @Test
    void testFindById_Success() {
        // Arrange
        when(donRepository.findById(1L)).thenReturn(Optional.of(donation));

        // Act
        DonEntity result = donServices.findById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(donRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_NotFound() {
        // Arrange
        when(donRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        GlobalException exception = assertThrows(GlobalException.class, () -> donServices.findById(1L));
        assertEquals("Donation not found for ID: 1", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(donRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_InvalidId() {
        // Act & Assert
        GlobalException exception = assertThrows(GlobalException.class, () -> donServices.findById(0L));
        assertEquals("Invalid ID: 0", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
        verify(donRepository, never()).findById(anyLong());
    }

    @Test
    void testFindNearestLocations() {
        // Arrange
        when(donRepository.findNearestLocationWithin100km(10.0, 20.0)).thenReturn(Collections.singletonList(donation));

        // Act
        List<DonEntity> result = donServices.findNearestLocations(10.0, 20.0);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(donRepository, times(1)).findNearestLocationWithin100km(10.0, 20.0);
    }


    @Test
    void testAcceptDonationRequest_Success() throws FirebaseMessagingException {
        // Arrange
        when(donRepository.findById(1L)).thenReturn(Optional.of(donation));
        when(reqRepository.findById(1L)).thenReturn(Optional.of(request));
        when(donRepository.save(donation)).thenReturn(donation); // Mock save to return the donation
        when(reqRepository.save(request)).thenReturn(request);   // Mock save to return the request

        // Mock the notification service to return a dummy message ID
        when(notificationService.sendNotification(any(NotificationRequest.class)))
                .thenReturn("dummy-message-id");

        // Act
        DonEntity result = donServices.acceptDonationRequest(dto);

        // Assert
        assertNotNull(result, "The result should not be null");
        assertEquals(donation, result, "The returned donation should match the expected donation");
        assertTrue(donation.getAcceptedRequests().contains(request), "Request should be added to accepted requests");
        assertTrue(request.getDonations().contains(donation), "Donation should be added to request's donations");
        verify(donRepository, times(1)).save(donation);
        verify(reqRepository, times(1)).save(request);
        verify(notificationService, times(1)).sendNotification(any(NotificationRequest.class));
    }

    @Test
    void testAcceptDonationRequest_DonationNotFound() {
        // Arrange
        when(donRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        GlobalException exception = assertThrows(GlobalException.class, () -> donServices.acceptDonationRequest(dto));
        assertEquals("Donation not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(donRepository, never()).save(any());
    }

    @Test
    void testAcceptDonationRequest_RequestNotFound() {
        // Arrange
        when(donRepository.findById(1L)).thenReturn(Optional.of(donation));
        when(reqRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        GlobalException exception = assertThrows(GlobalException.class, () -> donServices.acceptDonationRequest(dto));
        assertEquals("Request not found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        verify(donRepository, never()).save(any());
    }

    @Test
    void testAcceptDonationRequest_AlreadyAccepted() {
        // Arrange
        donation.getAcceptedRequests().add(request);
        request.getDonations().add(donation);
        when(donRepository.findById(1L)).thenReturn(Optional.of(donation));
        when(reqRepository.findById(1L)).thenReturn(Optional.of(request));

        // Act & Assert
        GlobalException exception = assertThrows(GlobalException.class, () -> donServices.acceptDonationRequest(dto));
        assertEquals("Request already accept", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        verify(donRepository, never()).save(any());
    }

    @Test
    void testDeleteAcceptedDonationRequest_Success() {
        // Arrange
        donation.getAcceptedRequests().add(request);
        request.getDonations().add(donation);
        when(donRepository.findById(1L)).thenReturn(Optional.of(donation));
        when(reqRepository.findById(1L)).thenReturn(Optional.of(request));

        // Act
        donServices.deleteAcceptedDonationRequest(dto);

        // Assert
        assertFalse(donation.getAcceptedRequests().contains(request));
        assertFalse(request.getDonations().contains(donation));
        verify(donRepository, times(1)).save(donation);
        verify(reqRepository, times(1)).save(request);
    }

    @Test
    void testDeleteAcceptedDonationRequest_AlreadyDeleted() {
        // Arrange
        when(donRepository.findById(1L)).thenReturn(Optional.of(donation));
        when(reqRepository.findById(1L)).thenReturn(Optional.of(request));

        // Act & Assert
        GlobalException exception = assertThrows(GlobalException.class, () -> donServices.deleteAcceptedDonationRequest(dto));
        assertEquals("Request already deleted", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());
        verify(donRepository, never()).save(any());
    }


    @Test
    void testSendNotificationIfApplicable_NoToken() {
        // Arrange
        request.setUser(new UserEntity());
        donation.setUser(new UserEntity());

        // Act
        donServices.sendNotificationIfApplicable(donation, request);

        // Assert
        verify(notificationService, never()).sendNotification(any());
    }

    @Test
    void testSendNotificationIfApplicable_WithUser() throws FirebaseMessagingException {
        // Arrange
        UserEntity donor = new UserEntity();
        donor.setId(1L);
        donor.setFcmToken("donor-fcm-token");
        donation.setUser(donor);

        UserEntity requestUser = new UserEntity();
        requestUser.setId(2L);
        requestUser.setFcmToken("request-user-fcm-token");
        request.setUser(requestUser);

        // Mock the notification service to return a dummy message ID
        when(notificationService.sendNotification(any(NotificationRequest.class)))
                .thenReturn("dummy-message-id");

        // Act
        donServices.sendNotificationIfApplicable(donation, request);

        // Assert
        verify(notificationService, times(1)).sendNotification(any(NotificationRequest.class));
    }

    @Test
    void testSendNotificationIfApplicable_WithUserAndOrganization() throws FirebaseMessagingException {
        // Arrange
        UserEntity donor = new UserEntity();
        donor.setId(1L);
        donor.setFcmToken("donor-fcm-token");
        donation.setUser(donor);

        UserEntity requestUser = new UserEntity();
        requestUser.setId(2L);
        requestUser.setFcmToken("request-user-fcm-token");
        request.setUser(requestUser);

        OrgEntity org = new OrgEntity();
        org.setId(3L);
        org.setFcmToken("org-fcm-token");
        request.setOrganization(org);

        when(notificationService.sendNotification(any(NotificationRequest.class)))
                .thenReturn("dummy-message-id");

        // Act
        donServices.sendNotificationIfApplicable(donation, request);

        // Assert
        verify(notificationService, times(2)).sendNotification(any(NotificationRequest.class));
    }

    @Test
    void testInvalidIdThrowsException() {
        Exception exception = assertThrows(GlobalException.class, () -> {
            donServices.findById(null);
        });
        assertEquals("Invalid ID: null", exception.getMessage());

        exception = assertThrows(GlobalException.class, () -> {
            donServices.findById(0L);
        });
        assertEquals("Invalid ID: 0", exception.getMessage());

        exception = assertThrows(GlobalException.class, () -> {
            donServices.findById(-1L);
        });
        assertEquals("Invalid ID: -1", exception.getMessage());
    }


    @Test
    void testValidDonationAndRequestDoesNotLogWarning() {

        donServices.sendNotificationIfApplicable(donation, request);

        // Ensure that warning log was never called
        verify(mockLogger, never()).warn("Donation or request is null. Notification not sent.");
    }

}