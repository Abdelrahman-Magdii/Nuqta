package com.spring.nuqta.organization.Services;

import com.spring.nuqta.enums.Scope;
import com.spring.nuqta.exception.GlobalException;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.organization.Repo.OrgRepo;
import com.spring.nuqta.usermanagement.Repo.UserRepo;
import com.spring.nuqta.verificationToken.General.GeneralVerification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class OrgServicesTest {

    @Mock
    private OrgRepo organizationRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private GeneralVerification generalVerification;

    @Mock
    private UserRepo userRepo;

    @InjectMocks
    private OrgServices orgServices;

    private OrgEntity orgEntity;

    @BeforeEach
    void setUp() {
        orgEntity = new OrgEntity();
        orgEntity.setId(1L);
        orgEntity.setOrgName("Test Org");
        orgEntity.setEmail("test@org.com");
        orgEntity.setLocation(new GeometryFactory().createPoint(new Coordinate(12.34, 56.78)));
        orgEntity.setPhoneNumber("1234567890");
        orgEntity.setScope(Scope.ORGANIZATION);
        orgEntity.setPassword("password");
        orgEntity.setLicenseNumber("LIC123");
        orgEntity.setCreatedDate(LocalDate.now());
        orgEntity.setCreatedUser("Test User");
        orgEntity.setModifiedDate(LocalDate.now());
        orgEntity.setModifiedUser("Test User");
    }

    @Test
    void testFindAll() {
        when(organizationRepository.findAll()).thenReturn(Collections.singletonList(orgEntity));

        List<OrgEntity> result = orgServices.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(orgEntity, result.get(0));

        verify(organizationRepository, times(1)).findAll();
    }

    @Test
    void testFindAllThrowsException() {
        when(organizationRepository.findAll()).thenReturn(Collections.emptyList());

        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.findAll());

        assertEquals("No organizations found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verify(organizationRepository, times(1)).findAll();
    }

    @Test
    void testFindById() {
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(orgEntity));

        OrgEntity result = orgServices.findById(1L);

        assertNotNull(result);
        assertEquals(orgEntity, result);

        verify(organizationRepository, times(1)).findById(1L);
    }

    @Test
    void testFindByIdThrowsException() {
        when(organizationRepository.findById(1L)).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.findById(1L));

        assertEquals("Organization not found with ID: 1", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verify(organizationRepository, times(1)).findById(1L);
    }

    @Test
    void testUpdate() {
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(orgEntity));
        when(organizationRepository.save(any(OrgEntity.class))).thenReturn(orgEntity);

        OrgEntity updatedEntity = new OrgEntity();
        updatedEntity.setId(1L);
        updatedEntity.setOrgName("Updated Org");
        updatedEntity.setLocation(new GeometryFactory().createPoint(new Coordinate(12.44, 56.88)));

        updatedEntity.setPhoneNumber("0987654321");
        updatedEntity.setScope(Scope.ORGANIZATION);

        OrgEntity result = orgServices.update(updatedEntity);

        assertNotNull(result);
        assertEquals("Updated Org", result.getOrgName());
        assertEquals(12.44, result.getLocation().getCoordinate().getX());
        assertEquals(56.88, result.getLocation().getCoordinate().getY());
        assertEquals("0987654321", result.getPhoneNumber());

        verify(organizationRepository, times(1)).findById(1L);
        verify(organizationRepository, times(1)).save(any(OrgEntity.class));
    }

    @Test
    void testUpdateThrowsExceptionWhenEntityIsNull() {
        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.update(null));

        assertEquals("Organization ID cannot be null", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        verify(organizationRepository, never()).save(any(OrgEntity.class));
    }

    @Test
    void testUpdateThrowsExceptionWhenIdIsNull() {
        OrgEntity entity = new OrgEntity();
        entity.setId(null);

        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.update(entity));

        assertEquals("Organization ID cannot be null", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        verify(organizationRepository, never()).save(any(OrgEntity.class));
    }

    @Test
    void testUpdateThrowsExceptionWhenOrganizationNotFound() {
        when(organizationRepository.findById(1L)).thenReturn(Optional.empty());

        OrgEntity entity = new OrgEntity();
        entity.setId(1L);

        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.update(entity));

        assertEquals("Organization not found with ID: 1", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verify(organizationRepository, times(1)).findById(1L);
        verify(organizationRepository, never()).save(any(OrgEntity.class));
    }

    @Test
    void testDeleteById() {
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(orgEntity));

        orgServices.deleteById(1L);

        verify(organizationRepository, times(1)).findById(1L);
        verify(organizationRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteByIdThrowsException() {
        when(organizationRepository.findById(1L)).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.deleteById(1L));

        assertEquals("Organization not found with ID: 1", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verify(organizationRepository, times(1)).findById(1L);
        verify(organizationRepository, never()).deleteById(1L);
    }

    @Test
    void testSaveOrg() {
        when(organizationRepository.findByLicenseNumberOrEmail(anyString(), anyString())).thenReturn(Optional.empty());
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(organizationRepository.save(any(OrgEntity.class))).thenReturn(orgEntity);

        orgServices.saveOrg(orgEntity);

        verify(organizationRepository, times(1)).findByLicenseNumberOrEmail(anyString(), anyString());
        verify(userRepo, times(1)).findByEmail(anyString());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(organizationRepository, times(1)).save(any(OrgEntity.class));
        verify(generalVerification, times(1)).sendOtpEmail(any(OrgEntity.class));
    }

    @Test
    void testSaveOrgThrowsExceptionWhenLicenseNumberOrEmailExists() {
        when(organizationRepository.findByLicenseNumberOrEmail(anyString(), anyString())).thenReturn(Optional.of(orgEntity));

        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.saveOrg(orgEntity));

        assertEquals("Organization Email or License Number already exists", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        verify(organizationRepository, times(1)).findByLicenseNumberOrEmail(anyString(), anyString());
        verify(organizationRepository, never()).save(any(OrgEntity.class));
    }

    @Test
    void testChangeOrgPassword() {
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(orgEntity));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(passwordEncoder.encode(anyString())).thenReturn("newEncodedPassword");
        when(organizationRepository.save(any(OrgEntity.class))).thenReturn(orgEntity);

        orgServices.changeOrgPassword(1L, "oldPassword", "newPassword");

        verify(organizationRepository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(passwordEncoder, times(1)).encode(anyString());
        verify(organizationRepository, times(1)).save(any(OrgEntity.class));
    }

    @Test
    void testChangeOrgPasswordThrowsExceptionWhenOrganizationNotFound() {
        when(organizationRepository.findById(1L)).thenReturn(Optional.empty());

        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.changeOrgPassword(1L, "oldPassword", "newPassword"));

        assertEquals("Organization not found with ID: 1", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verify(organizationRepository, times(1)).findById(1L);
        verify(passwordEncoder, never()).matches(anyString(), anyString());
        verify(organizationRepository, never()).save(any(OrgEntity.class));
    }

    @Test
    void testChangeOrgPasswordThrowsExceptionWhenOldPasswordIsIncorrect() {
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(orgEntity));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.changeOrgPassword(1L, "oldPassword", "newPassword"));

        assertEquals("Old password is incorrect.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        verify(organizationRepository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).matches(anyString(), anyString());
        verify(organizationRepository, never()).save(any(OrgEntity.class));
    }

    @Test
    void testUpdateFcmToken() {
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(orgEntity));
        when(organizationRepository.save(any(OrgEntity.class))).thenReturn(orgEntity);

        String result = orgServices.updateFcmToken(1L, "newFcmToken");

        assertEquals("FCM token updated successfully", result);

        verify(organizationRepository, times(1)).findById(1L);
        verify(organizationRepository, times(1)).save(any(OrgEntity.class));
    }

    @Test
    void testUpdateFcmTokenThrowsExceptionWhenOrganizationNotFound() {
        when(organizationRepository.findById(1L)).thenReturn(Optional.empty());

        String result = orgServices.updateFcmToken(1L, "newFcmToken");

        assertEquals("Organization not found", result);

        verify(organizationRepository, times(1)).findById(1L);
        verify(organizationRepository, never()).save(any(OrgEntity.class));
    }

    @Test
    void testValidateOrganizationFields_AllFieldsValid() {
        OrgEntity orgEntity = new OrgEntity();
        orgEntity.setLicenseNumber("LIC123");
        orgEntity.setOrgName("Test Org");
        orgEntity.setPassword("password");
        orgEntity.setPhoneNumber("1234567890");
        orgEntity.setScope(Scope.ORGANIZATION);
        orgEntity.setLocation(new GeometryFactory().createPoint(new Coordinate(12.34, 56.78)));

        // No exception should be thrown
        assertDoesNotThrow(() -> orgServices.validateOrganizationFields(orgEntity));
    }

    @Test
    void testValidateOrganizationFields_LicenseNumberMissing() {
        OrgEntity orgEntity = new OrgEntity();
        orgEntity.setOrgName("Test Org");
        orgEntity.setPassword("password");
        orgEntity.setPhoneNumber("1234567890");
        orgEntity.setScope(Scope.ORGANIZATION);
        orgEntity.setLocation(new GeometryFactory().createPoint(new Coordinate(12.34, 56.78)));

        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.validateOrganizationFields(orgEntity));

        assertEquals("License Number is required.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testValidateOrganizationFields_OrgNameMissing() {
        OrgEntity orgEntity = new OrgEntity();
        orgEntity.setLicenseNumber("LIC123");
        orgEntity.setPassword("password");
        orgEntity.setPhoneNumber("1234567890");
        orgEntity.setScope(Scope.ORGANIZATION);
        orgEntity.setLocation(new GeometryFactory().createPoint(new Coordinate(12.34, 56.78)));

        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.validateOrganizationFields(orgEntity));

        assertEquals("Organization name is required.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testValidateOrganizationFields_PasswordMissing() {
        OrgEntity orgEntity = new OrgEntity();
        orgEntity.setLicenseNumber("LIC123");
        orgEntity.setOrgName("Test Org");
        orgEntity.setPhoneNumber("1234567890");
        orgEntity.setScope(Scope.ORGANIZATION);
        orgEntity.setLocation(new GeometryFactory().createPoint(new Coordinate(12.34, 56.78)));

        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.validateOrganizationFields(orgEntity));

        assertEquals("Password is required.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testValidateOrganizationFields_PhoneNumberMissing() {
        OrgEntity orgEntity = new OrgEntity();
        orgEntity.setLicenseNumber("LIC123");
        orgEntity.setOrgName("Test Org");
        orgEntity.setPassword("password");
        orgEntity.setScope(Scope.ORGANIZATION);
        orgEntity.setLocation(new GeometryFactory().createPoint(new Coordinate(12.34, 56.78)));

        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.validateOrganizationFields(orgEntity));

        assertEquals("Mobile phone number is required.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testValidateOrganizationFields_ScopeMissing() {
        OrgEntity orgEntity = new OrgEntity();
        orgEntity.setLicenseNumber("LIC123");
        orgEntity.setOrgName("Test Org");
        orgEntity.setPassword("password");
        orgEntity.setPhoneNumber("1234567890");
        orgEntity.setLocation(new GeometryFactory().createPoint(new Coordinate(12.34, 56.78)));

        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.validateOrganizationFields(orgEntity));

        assertEquals("Scope must be set to 'organization'.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testValidateOrganizationFields_ScopeInvalid() {
        OrgEntity orgEntity = new OrgEntity();
        orgEntity.setLicenseNumber("LIC123");
        orgEntity.setOrgName("Test Org");
        orgEntity.setPassword("password");
        orgEntity.setPhoneNumber("1234567890");
        orgEntity.setScope(Scope.USER);
        orgEntity.setLocation(new GeometryFactory().createPoint(new Coordinate(12.34, 56.78)));

        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.validateOrganizationFields(orgEntity));

        assertEquals("Scope must be set to 'organization'.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testValidateOrganizationFields_LocationMissing() {
        OrgEntity orgEntity = new OrgEntity();
        orgEntity.setLicenseNumber("LIC123");
        orgEntity.setOrgName("Test Org");
        orgEntity.setPassword("password");
        orgEntity.setPhoneNumber("1234567890");
        orgEntity.setScope(Scope.ORGANIZATION);

        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.validateOrganizationFields(orgEntity));

        assertEquals("Location is required.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }
}