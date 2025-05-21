package com.spring.nuqta.donation.Services;

import com.spring.nuqta.donation.Dto.AcceptDonationRequestDto;
import com.spring.nuqta.donation.Entity.DonEntity;
import com.spring.nuqta.donation.Repo.DonRepo;
import com.spring.nuqta.exception.GlobalException;
import com.spring.nuqta.mail.Services.EmailService;
import com.spring.nuqta.notifications.Dto.NotificationRequest;
import com.spring.nuqta.notifications.Services.NotificationService;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.request.Entity.ReqEntity;
import com.spring.nuqta.request.Repo.ReqRepo;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
    private MessageSource messageSource;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private DonServices donServices;

    @Mock
    private EmailService emailService;
    
    private DonEntity donation;
    private ReqEntity request;
    private AcceptDonationRequestDto dto;

    @BeforeEach
    void setUp() {
        donation = new DonEntity();
        donation.setId(1L);

        request = new ReqEntity();
        request.setId(1L);

        dto = new AcceptDonationRequestDto();
        dto.setDonationId(1L);
        dto.setRequestId(1L);
    }

    @Test
    void testFindTopConservatism() {
        when(donRepository.findFirstByConservatismContainingIgnoreCase(anyString())).thenReturn(Collections.singletonList(donation));

        List<DonEntity> result = donServices.findTopConservatism("conservatism");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(donation, result.get(0));

        verify(donRepository, times(1)).findFirstByConservatismContainingIgnoreCase(anyString());
    }

    @Test
    void testFindTopConservatism_NotFound() {
        when(donRepository.findFirstByConservatismContainingIgnoreCase(anyString())).thenReturn(Collections.emptyList());

        GlobalException exception = assertThrows(GlobalException.class, () -> donServices.findTopConservatism("conservatism"));

        assertEquals("error.donation.notFound", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verify(donRepository, times(1)).findFirstByConservatismContainingIgnoreCase(anyString());
    }

    @Test
    void testFindTopCity() {
        when(donRepository.findFirstByCityContainingIgnoreCase(anyString())).thenReturn(Collections.singletonList(donation));

        List<DonEntity> result = donServices.findTopCity("city");

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(donation, result.get(0));

        verify(donRepository, times(1)).findFirstByCityContainingIgnoreCase(anyString());
    }

    @Test
    void testFindTopCity_NotFound() {
        when(donRepository.findFirstByCityContainingIgnoreCase(anyString())).thenReturn(Collections.emptyList());

        GlobalException exception = assertThrows(GlobalException.class, () -> donServices.findTopCity("city"));

        assertEquals("error.donation.notFound", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verify(donRepository, times(1)).findFirstByCityContainingIgnoreCase(anyString());
    }

    @Test
    void testFindById() {
        when(donRepository.findById(anyLong())).thenReturn(Optional.of(donation));

        DonEntity result = donServices.findById(1L);

        assertNotNull(result);
        assertEquals(donation, result);

        verify(donRepository, times(1)).findById(anyLong());
    }

    @Test
    @Transactional
    void testAcceptDonationRequest_AlreadyAccepted() {
        donation.getAcceptedRequests().add(request);
        request.getDonations().add(donation);

        when(donRepository.findById(anyLong())).thenReturn(Optional.of(donation));
        when(reqRepository.findById(anyLong())).thenReturn(Optional.of(request));

        GlobalException exception = assertThrows(GlobalException.class, () -> donServices.acceptDonationRequest(dto));

        assertEquals("error.request.alreadyAccepted", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());

        verify(donRepository, times(1)).findById(anyLong());
        verify(reqRepository, times(1)).findById(anyLong());
        verify(reqRepository, never()).save(any(ReqEntity.class));
        verify(donRepository, never()).save(any(DonEntity.class));
    }

    @Test
    @Transactional
    void testDeleteAcceptedDonationRequest() {
        donation.getAcceptedRequests().add(request);
        request.getDonations().add(donation);

        when(donRepository.findById(anyLong())).thenReturn(Optional.of(donation));
        when(reqRepository.findById(anyLong())).thenReturn(Optional.of(request));

        donServices.deleteAcceptedDonationRequest(dto);

        assertFalse(donation.getAcceptedRequests().contains(request));
        assertFalse(request.getDonations().contains(donation));

        verify(donRepository, times(1)).findById(anyLong());
        verify(reqRepository, times(1)).findById(anyLong());
        verify(reqRepository, times(1)).save(any(ReqEntity.class));
        verify(donRepository, times(1)).save(any(DonEntity.class));
    }

    @Test
    @Transactional
    void testDeleteAcceptedDonationRequest_AlreadyDeleted() {
        when(donRepository.findById(anyLong())).thenReturn(Optional.of(donation));
        when(reqRepository.findById(anyLong())).thenReturn(Optional.of(request));

        GlobalException exception = assertThrows(GlobalException.class, () -> donServices.deleteAcceptedDonationRequest(dto));

        assertEquals("error.request.alreadyDeleted", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());

        verify(donRepository, times(1)).findById(anyLong());
        verify(reqRepository, times(1)).findById(anyLong());
        verify(reqRepository, never()).save(any(ReqEntity.class));
        verify(donRepository, never()).save(any(DonEntity.class));
    }

    @Test
    void testSendNotificationIfApplicable() {
        // Set up donation with a valid ID
        donation.setId(1L);

        // Set up donor with FCM token
        UserEntity donor = new UserEntity();
        donor.setFcmToken("donor-token");
        donation.setUser(donor);

        // Set up request user with FCM token
        UserEntity requestUser = new UserEntity();
        requestUser.setFcmToken("request-user-token");
        request.setUser(requestUser);

        // Set up organization with FCM token
        OrgEntity org = new OrgEntity();
        org.setFcmToken("org-token");
        request.setOrganization(org);

        // Mock the messageSource to return a valid message
        when(messageSource.getMessage(eq("notification.requestAcceptedMessage"), any(), any()))
                .thenReturn("Your donation request has been accepted.");

        // Call the method under test
        donServices.sendNotificationIfApplicable(donation, request);

        // Verify that notifications were sent
        verify(notificationService, times(2)).sendNotification(any(NotificationRequest.class));
    }

    @Test
    void testSendNotificationIfApplicable_NullDonationOrRequest() {
        donServices.sendNotificationIfApplicable(null, request);
        donServices.sendNotificationIfApplicable(donation, null);

        verify(notificationService, never()).sendNotification(any(NotificationRequest.class));
    }

    @Test
    void testSendNotificationIfApplicable_NullDonorOrToken() {
        donation.setUser(null);
        donServices.sendNotificationIfApplicable(donation, request);

        request.setUser(null);
        donServices.sendNotificationIfApplicable(donation, request);

        verify(notificationService, never()).sendNotification(any(NotificationRequest.class));
    }

    @Test
    void testFindById_InvalidId() {
        when(messageSource.getMessage(eq("error.invalid.id"), any(), any())).thenReturn("error.invalid.id");

        GlobalException exception = assertThrows(GlobalException.class, () -> donServices.findById(0L));

        assertEquals("error.invalid.id", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        verify(donRepository, never()).findById(anyLong());
    }

    @Test
    void testFindById_NotFound() {
        when(donRepository.findById(anyLong())).thenReturn(Optional.empty());
        when(messageSource.getMessage(eq("error.donation.notFoundById"), any(), any())).thenReturn("error.donation.notFoundById");

        GlobalException exception = assertThrows(GlobalException.class, () -> donServices.findById(1L));

        assertEquals("error.donation.notFoundById", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verify(donRepository, times(1)).findById(anyLong());
    }

    @Test
    @Transactional
    void testAcceptDonationRequest() throws MessagingException {
        when(donRepository.findById(anyLong())).thenReturn(Optional.of(donation));
        when(reqRepository.findById(anyLong())).thenReturn(Optional.of(request));
        when(donRepository.save(any(DonEntity.class))).thenReturn(donation);

        DonEntity result = donServices.acceptDonationRequest(dto);

        assertNotNull(result);
        assertTrue(donation.getAcceptedRequests().contains(request));
        assertTrue(request.getDonations().contains(donation));

        verify(donRepository, times(1)).findById(anyLong());
        verify(reqRepository, times(1)).findById(anyLong());
        verify(reqRepository, times(1)).save(any(ReqEntity.class));
        verify(donRepository, times(1)).save(any(DonEntity.class));
    }
}