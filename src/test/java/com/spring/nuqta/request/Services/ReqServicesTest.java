package com.spring.nuqta.request.Services;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.spring.nuqta.donation.Entity.DonEntity;
import com.spring.nuqta.donation.Repo.DonRepo;
import com.spring.nuqta.enums.Level;
import com.spring.nuqta.enums.Status;
import com.spring.nuqta.exception.GlobalException;
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
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.spring.nuqta.enums.Level.LOW;
import static com.spring.nuqta.enums.Status.FULFILLED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReqServicesTest {

    @Mock
    private ReqRepo reqRepo;

    @Mock
    private UserRepo userRepo;

    @Mock
    private OrgRepo orgRepo;

    @Mock
    private DonRepo donRepo;

    @Mock
    private NotificationService notificationService;

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
        reqEntity.setAmount(2.0);
        reqEntity.setAddress("Test Address");
        reqEntity.setStatus(Status.OPEN);
        reqEntity.setUrgencyLevel(Level.HIGH);
        reqEntity.setPaymentAvailable(true);
        reqEntity.setCreatedDate(LocalDate.now());
        reqEntity.setModifiedDate(LocalDate.now());

        userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("testUser");

        orgEntity = new OrgEntity();
        orgEntity.setId(1L);
        orgEntity.setOrgName("Test Org");

        donEntity = new DonEntity();
        donEntity.setId(1L);
        donEntity.setUser(userEntity);
    }

    @Test
    void testFindAll() {
        when(reqRepo.findAll()).thenReturn(Collections.singletonList(reqEntity));

        List<ReqEntity> result = reqServices.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(reqEntity, result.get(0));

        verify(reqRepo, times(1)).findAll();
    }

    @Test
    void testFindAllThrowsException() {
        when(reqRepo.findAll()).thenReturn(Collections.emptyList());

        GlobalException exception = assertThrows(GlobalException.class, () -> reqServices.findAll());

        assertEquals("No requests found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verify(reqRepo, times(1)).findAll();
    }

    @Test
    void testFindById() {
        when(reqRepo.findById(1L)).thenReturn(Optional.of(reqEntity));

        ReqEntity result = reqServices.findById(1L);

        assertNotNull(result);
        assertEquals(reqEntity, result);

        verify(reqRepo, times(1)).findById(1L);
    }

    @Test
    void testFindByIdThrowsException() {
        when(reqRepo.findById(1L)).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> reqServices.findById(1L));

        assertEquals("Request not found with ID: 1", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verify(reqRepo, times(1)).findById(1L);
    }

    @Test
    void testDeleteById() {
        when(reqRepo.existsById(1L)).thenReturn(true);

        reqServices.deleteById(1L);

        verify(reqRepo, times(1)).existsById(1L);
        verify(reqRepo, times(1)).hardDeleteById(1L);
    }

    @Test
    void testDeleteByIdThrowsException() {
        when(reqRepo.existsById(1L)).thenReturn(false);

        GlobalException exception = assertThrows(GlobalException.class, () -> reqServices.deleteById(1L));

        assertEquals("Request not found with ID: 1", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verify(reqRepo, times(1)).existsById(1L);
        verify(reqRepo, never()).hardDeleteById(1L);
    }

    @Test
    void testAddRequest() throws FirebaseMessagingException {
        when(userRepo.findById(1L)).thenReturn(Optional.of(userEntity));
        when(reqRepo.save(any(ReqEntity.class))).thenReturn(reqEntity);

        ReqEntity result = reqServices.addRequest(1L, reqEntity);

        assertNotNull(result);
        assertEquals(userEntity, result.getUser());
        assertEquals(LocalDate.now(), result.getCreatedDate());
        assertEquals(LocalDate.now(), result.getModifiedDate());
        assertEquals(userEntity.getUsername(), result.getCreatedUser());
        assertEquals(userEntity.getUsername(), result.getModifiedUser());

        verify(userRepo, times(1)).findById(1L);
        verify(reqRepo, times(1)).save(any(ReqEntity.class));
    }

    @Test
    void testAddRequestThrowsException() {
        when(userRepo.findById(1L)).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> reqServices.addRequest(1L, reqEntity));

        assertEquals("User not found with ID: 1", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verify(userRepo, times(1)).findById(1L);
        verify(reqRepo, never()).save(any(ReqEntity.class));
    }

    @Test
    void testAddRequestForOrg() throws FirebaseMessagingException {
        when(orgRepo.findById(1L)).thenReturn(Optional.of(orgEntity));
        when(reqRepo.save(any(ReqEntity.class))).thenReturn(reqEntity);

        ReqEntity result = reqServices.addRequestForOrg(1L, reqEntity);

        assertNotNull(result);
        assertEquals(orgEntity, result.getOrganization());
        assertEquals(LocalDate.now(), result.getCreatedDate());
        assertEquals(LocalDate.now(), result.getModifiedDate());
        assertEquals(orgEntity.getOrgName(), result.getCreatedUser());
        assertEquals(orgEntity.getOrgName(), result.getModifiedUser());

        verify(orgRepo, times(1)).findById(1L);
        verify(reqRepo, times(1)).save(any(ReqEntity.class));
    }

    @Test
    void testAddRequestForOrgThrowsException() {
        when(orgRepo.findById(1L)).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> reqServices.addRequestForOrg(1L, reqEntity));

        assertEquals("Organization not found with ID: 1", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verify(orgRepo, times(1)).findById(1L);
        verify(reqRepo, never()).save(any(ReqEntity.class));
    }

    @Test
    void testUpdate() {
        when(reqRepo.findById(1L)).thenReturn(Optional.of(reqEntity));
        when(reqRepo.save(any(ReqEntity.class))).thenReturn(reqEntity);

        ReqEntity updatedEntity = new ReqEntity();
        updatedEntity.setId(1L);
        updatedEntity.setBloodTypeNeeded("B+");
        updatedEntity.setAmount(3.0);
        updatedEntity.setAddress("Updated Address");
        updatedEntity.setStatus(FULFILLED);
        updatedEntity.setUrgencyLevel(LOW);
        updatedEntity.setPaymentAvailable(false);

        ReqEntity result = reqServices.update(updatedEntity);

        assertNotNull(result);
        assertEquals("B+", result.getBloodTypeNeeded());
        assertEquals(3, result.getAmount());
        assertEquals("Updated Address", result.getAddress());
        assertEquals(FULFILLED, result.getStatus());
        assertEquals(LOW, result.getUrgencyLevel());
        assertFalse(result.getPaymentAvailable());
        assertEquals(LocalDate.now(), result.getModifiedDate());

        verify(reqRepo, times(1)).findById(1L);
        verify(reqRepo, times(1)).save(any(ReqEntity.class));
    }

    @Test
    void testUpdateThrowsExceptionWhenIdIsNull() {
        ReqEntity entity = new ReqEntity();
        entity.setId(null);

        GlobalException exception = assertThrows(GlobalException.class, () -> reqServices.update(entity));

        assertEquals("Request ID cannot be null", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        verify(reqRepo, never()).save(any(ReqEntity.class));
    }

    @Test
    void testUpdateThrowsExceptionWhenRequestNotFound() {
        when(reqRepo.findById(1L)).thenReturn(Optional.empty());

        ReqEntity entity = new ReqEntity();
        entity.setId(1L);

        GlobalException exception = assertThrows(GlobalException.class, () -> reqServices.update(entity));

        assertEquals("Request not found with ID: 1", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verify(reqRepo, times(1)).findById(1L);
        verify(reqRepo, never()).save(any(ReqEntity.class));
    }

    @Test
    public void testSetModifiedUser_WhenUserExists() {
        ReqEntity existingEntity = new ReqEntity();
        UserEntity user = new UserEntity();
        user.setUsername("JohnDoe");

        existingEntity.setUser(user);

        // Execute logic
        if (existingEntity.getUser() != null) {
            existingEntity.setModifiedUser(existingEntity.getUser().getUsername());
        } else if (existingEntity.getOrganization() != null) {
            existingEntity.setModifiedUser(existingEntity.getOrganization().getOrgName());
        } else {
            existingEntity.setModifiedUser("Unknown");
        }

        assertEquals("JohnDoe", existingEntity.getModifiedUser());
    }

    @Test
    public void testSetModifiedUser_WhenOrganizationExists() {
        ReqEntity existingEntity = new ReqEntity();
        OrgEntity org = new OrgEntity();
        org.setOrgName("RedCross");

        existingEntity.setOrganization(org);

        // Execute logic
        if (existingEntity.getUser() != null) {
            existingEntity.setModifiedUser(existingEntity.getUser().getUsername());
        } else if (existingEntity.getOrganization() != null) {
            existingEntity.setModifiedUser(existingEntity.getOrganization().getOrgName());
        } else {
            existingEntity.setModifiedUser("Unknown");
        }

        assertEquals("RedCross", existingEntity.getModifiedUser());
    }

    @Test
    public void testSetModifiedUser_WhenNeitherExists() {
        ReqEntity existingEntity = new ReqEntity();

        // Execute logic
        if (existingEntity.getUser() != null) {
            existingEntity.setModifiedUser(existingEntity.getUser().getUsername());
        } else if (existingEntity.getOrganization() != null) {
            existingEntity.setModifiedUser(existingEntity.getOrganization().getOrgName());
        } else {
            existingEntity.setModifiedUser("Unknown");
        }

        assertEquals("Unknown", existingEntity.getModifiedUser());
    }


    @Test
    public void testFindNearbyDonors() {
        when(donRepo.findNearbyDonors(any(), anyDouble())).thenReturn(new ArrayList<>());
        List<DonEntity> donors = reqServices.findNearbyDonors(new GeometryFactory().createPoint(new Coordinate(0, 0)));

        assertNotNull(donors);
    }


}