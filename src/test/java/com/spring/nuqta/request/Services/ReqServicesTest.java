package com.spring.nuqta.request.Services;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.spring.nuqta.donation.Entity.DonEntity;
import com.spring.nuqta.donation.Repo.DonRepo;
import com.spring.nuqta.enums.Level;
import com.spring.nuqta.enums.Status;
import com.spring.nuqta.exception.GlobalException;
import com.spring.nuqta.notifications.Dto.NotificationRequest;
import com.spring.nuqta.notifications.Services.NotificationService;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.organization.Repo.OrgRepo;
import com.spring.nuqta.request.Entity.ReqEntity;
import com.spring.nuqta.request.Repo.ReqRepo;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import com.spring.nuqta.usermanagement.Repo.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReqServicesTest {

    @Mock
    private ReqRepo reqRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private OrgRepo orgRepo;

    @Mock
    private NotificationService notificationService;

    @Mock
    private DonRepo donRepo;

    @Mock
    private CacheManager cacheManager;

    @Mock
    private MessageSource ms;

    @Mock
    private Cache cache;

    @InjectMocks
    private ReqServices reqServices;

    private ReqEntity reqEntity;
    private UserEntity userEntity;
    private OrgEntity orgEntity;
    private DonEntity donEntity;

    @BeforeEach
    void setUp() {
        reqEntity = new ReqEntity();
        reqEntity.setId(1L);
        reqEntity.setBloodTypeNeeded("A+");
        reqEntity.setCity("New York");
        reqEntity.setConservatism("High");
        reqEntity.setRequestDate(LocalDate.now());
        reqEntity.setStatus(Status.FULFILLED);
        reqEntity.setUrgencyLevel(Level.HIGH);
        reqEntity.setPaymentAvailable(true);
        
        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("testUser");
        userEntity.setFcmToken("testFcmToken"); // Ensure FCM token is set

        orgEntity = new OrgEntity();
        orgEntity.setId(1L);
        orgEntity.setOrgName("testOrg");

        donEntity = new DonEntity();
        donEntity.setId(1L);
        donEntity.setUser(userEntity);
    }

    @Test
    void testFindAll() throws GlobalException {
        when(reqRepo.findAll()).thenReturn(Collections.singletonList(reqEntity));

        List<ReqEntity> result = reqServices.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(reqRepo, times(1)).findAll();
    }

    @Test
    void testFindAllThrowsException() {
        when(reqRepo.findAll()).thenReturn(Collections.emptyList());

        GlobalException exception = assertThrows(GlobalException.class, () -> reqServices.findAll());
        assertEquals("error.request.no_requests", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testFindById() throws GlobalException {
        when(reqRepo.findById(1L)).thenReturn(Optional.of(reqEntity));

        ReqEntity result = reqServices.findById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(reqRepo, times(1)).findById(1L);
    }

    @Test
    void testFindByIdThrowsException() {
        when(reqRepo.findById(1L)).thenReturn(Optional.empty());
        when(ms.getMessage(eq("error.request.notfound"), any(), any())).thenReturn("error.request.notfound");

        GlobalException exception = assertThrows(GlobalException.class, () -> reqServices.findById(1L));
        assertEquals("error.request.notfound", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testDeleteById() throws GlobalException {
        when(reqRepo.existsById(1L)).thenReturn(true);
        when(cacheManager.getCache("requests")).thenReturn(cache);

        reqServices.deleteById(1L);

        verify(reqRepo, times(1)).hardDeleteById(1L);
        verify(cache, times(1)).evict(1L);
    }

    @Test
    void testDeleteByIdThrowsException() {
        when(reqRepo.existsById(1L)).thenReturn(false);
        when(ms.getMessage(eq("error.request.notfound"), any(), any())).thenReturn("error.request.notfound");

        GlobalException exception = assertThrows(GlobalException.class, () -> reqServices.deleteById(1L));
        assertEquals("error.request.notfound", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testReCache() throws GlobalException {
        when(reqRepo.findById(1L)).thenReturn(Optional.of(reqEntity));
        when(cacheManager.getCache("requests")).thenReturn(cache);
        when(cacheManager.getCache("users")).thenReturn(cache);
        when(cacheManager.getCache("org")).thenReturn(cache);

        reqServices.ReCache(1L);

        verify(reqRepo, times(1)).save(reqEntity);
        verify(cache, times(1)).evict(1L);
        verify(cache, times(2)).clear(); // Expect 2 clears (users and org caches)
    }

    @Test
    void testAddRequestThrowsException() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());
        when(ms.getMessage(eq("error.request.notfound"), any(), any())).thenReturn("error.request.notfound");

        GlobalException exception = assertThrows(GlobalException.class, () -> reqServices.addRequest(1L, reqEntity));
        assertEquals("error.request.notfound", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }


    @Test
    void testAddRequestForOrgThrowsException() {
        when(orgRepo.findById(1L)).thenReturn(Optional.empty());
        when(ms.getMessage(eq("error.org.notfound"), any(), any())).thenReturn("error.org.notfound");

        GlobalException exception = assertThrows(GlobalException.class, () -> reqServices.addRequestForOrg(1L, reqEntity));
        assertEquals("error.org.notfound", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testUpdate() throws GlobalException {
        when(reqRepo.findById(1L)).thenReturn(Optional.of(reqEntity));
        when(reqRepo.save(reqEntity)).thenReturn(reqEntity);

        ReqEntity result = reqServices.update(reqEntity);

        assertNotNull(result);
        assertEquals(reqEntity.getBloodTypeNeeded(), result.getBloodTypeNeeded());
        verify(reqRepo, times(1)).save(reqEntity);
    }

    @Test
    void testUpdateThrowsException() {
        reqEntity.setId(null);

        GlobalException exception = assertThrows(GlobalException.class, () -> reqServices.update(reqEntity));
        assertEquals("error.request.id.null", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testFindNearbyDonors() {
        when(donRepo.findTopByCityOrConservatism("New York", "High")).thenReturn(Collections.singletonList(donEntity));

        List<DonEntity> result = reqServices.findNearbyDonors("New York", "High");

        assertNotNull(result);
        assertEquals(1, result.size());
        verify(donRepo, times(1)).findTopByCityOrConservatism("New York", "High");
    }

    @Test
    void testValidIdThrowsException() {
        when(ms.getMessage(eq("error.user.invalid.id"), any(), any())).thenReturn("error.user.invalid.id");

        GlobalException exception = assertThrows(GlobalException.class, () -> reqServices.validId(null));
        assertEquals("error.user.invalid.id", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testAddRequest() throws GlobalException, FirebaseMessagingException {
        // Mock userRepo to return a valid user
        when(userRepo.findById(1L)).thenReturn(Optional.of(userEntity));

        // Mock reqRepo to return the saved entity
        when(reqRepo.save(reqEntity)).thenReturn(reqEntity);

        // Mock donRepo to return a non-empty list of nearby donors
        when(donRepo.findTopByCityOrConservatism("New York", "High")).thenReturn(Collections.singletonList(donEntity));

        // Mock messageSource to return a valid message
        when(ms.getMessage(anyString(), any(), any())).thenReturn("Test Message");

        // Call the method under test
        ReqEntity result = reqServices.addRequest(1L, reqEntity);

        // Assertions
        assertNotNull(result);
        assertEquals(userEntity, result.getUser());

        // Verify that notificationService.sendNotification was called
        verify(notificationService, times(1)).sendNotification(any(NotificationRequest.class));
    }

    @Test
    void testAddRequestForOrg() throws GlobalException, FirebaseMessagingException {
        // Mock orgRepo to return a valid organization
        when(orgRepo.findById(1L)).thenReturn(Optional.of(orgEntity));

        // Mock reqRepo to return the saved entity
        when(reqRepo.save(reqEntity)).thenReturn(reqEntity);

        // Mock donRepo to return a non-empty list of nearby donors
        when(donRepo.findTopByCityOrConservatism("New York", "High")).thenReturn(Collections.singletonList(donEntity));

        // Mock messageSource to return a valid message
        when(ms.getMessage(anyString(), any(), any())).thenReturn("Test Message");

        // Call the method under test
        ReqEntity result = reqServices.addRequestForOrg(1L, reqEntity);

        // Assertions
        assertNotNull(result);
        assertEquals(orgEntity, result.getOrganization());

        // Verify that notificationService.sendNotification was called
        verify(notificationService, times(1)).sendNotification(any(NotificationRequest.class));
    }

    @Test
    void testSendNotification() throws GlobalException, FirebaseMessagingException {
        // Set up reqEntity with a valid user
        reqEntity.setUser(userEntity);

        // Mock donRepo to return a non-empty list of nearby donors
        when(donRepo.findTopByCityOrConservatism("New York", "High")).thenReturn(Collections.singletonList(donEntity));

        // Mock messageSource to return a valid message
        when(ms.getMessage(anyString(), any(), any())).thenReturn("Test Message");

        // Call the method under test
        reqServices.SendNotification(reqEntity);

        // Verify that notificationService.sendNotification was called
        verify(notificationService, times(1)).sendNotification(any(NotificationRequest.class));
    }
}