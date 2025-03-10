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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.CacheManager;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Arrays;
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

    @Mock
    private MessageSource ms;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private OrgServices orgServices;

    private OrgEntity orgEntity;

    @BeforeEach
    void setUp() {
        orgEntity = new OrgEntity();
        orgEntity.setId(1L);
        orgEntity.setOrgName("Test Org");
        orgEntity.setEmail("test@org.com");
        orgEntity.setCity("Test City");
        orgEntity.setConservatism("Test Conservatism");
        orgEntity.setPhoneNumber("1234567890");
        orgEntity.setScope(Scope.ORGANIZATION);
        orgEntity.setPassword("password");
        orgEntity.setLicenseNumber("LIC123");
        orgEntity.setFcmToken("fcmToken");
        orgEntity.setCreatedDate(LocalDate.now());
        orgEntity.setCreatedUser("Test User");
        orgEntity.setModifiedDate(LocalDate.now());
        orgEntity.setModifiedUser("Test User");
    }

    @Test
    void testFindAll() {
        when(organizationRepository.findAllByEnabledTrue()).thenReturn(Arrays.asList(orgEntity));
        List<OrgEntity> result = orgServices.findAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(organizationRepository, times(1)).findAllByEnabledTrue();
    }

    @Test
    void testFindAllThrowsException() {
        when(organizationRepository.findAllByEnabledTrue()).thenReturn(Collections.emptyList());
        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.findAll());
        assertEquals("org.no.organizations", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testFindById() {
        when(organizationRepository.findByIdAndEnabledTrue(1L)).thenReturn(Optional.of(orgEntity));
        OrgEntity result = orgServices.findById(1L);
        assertNotNull(result);
        assertEquals("Test Org", result.getOrgName());
        verify(organizationRepository, times(1)).findByIdAndEnabledTrue(1L);
    }

    @Test
    void testFindByIdThrowsException() {
        when(organizationRepository.findByIdAndEnabledTrue(1L)).thenReturn(Optional.empty());
        when(ms.getMessage(eq("org.not.found"), any(), eq(LocaleContextHolder.getLocale()))).thenReturn("org.not.found");

        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.findById(1L));
        assertEquals("org.not.found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testUpdate() {
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(orgEntity));
        when(organizationRepository.save(any(OrgEntity.class))).thenReturn(orgEntity);

        OrgEntity updatedEntity = new OrgEntity();
        updatedEntity.setId(1L);
        updatedEntity.setOrgName("Updated Org");
        updatedEntity.setCity("Updated City");
        updatedEntity.setConservatism("Updated Conservatism");
        updatedEntity.setPhoneNumber("0987654321");
        updatedEntity.setScope(Scope.ORGANIZATION);
        updatedEntity.setFcmToken("updatedFcmToken");

        OrgEntity result = orgServices.update(updatedEntity);
        assertNotNull(result);
        assertEquals("Updated Org", result.getOrgName());
        verify(organizationRepository, times(1)).findById(1L);
        verify(organizationRepository, times(1)).save(any(OrgEntity.class));
    }

    @Test
    void testUpdateThrowsExceptionWhenEntityIsNull() {
        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.update(null));
        assertEquals("org.id.null", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testUpdateThrowsExceptionWhenIdIsNull() {
        OrgEntity entity = new OrgEntity();
        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.update(entity));
        assertEquals("org.id.null", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testUpdateThrowsExceptionWhenOrganizationNotFound() {
        when(organizationRepository.findById(1L)).thenReturn(Optional.empty());
        when(ms.getMessage(eq("org.not.found"), any(), eq(LocaleContextHolder.getLocale()))).thenReturn("org.not.found");

        OrgEntity entity = new OrgEntity();
        entity.setId(1L);
        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.update(entity));
        assertEquals("org.not.found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testDeleteById() {
        when(organizationRepository.existsById(1L)).thenReturn(true);
        orgServices.deleteById(1L);
        verify(organizationRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteByIdThrowsException() {
        when(organizationRepository.existsById(1L)).thenReturn(false);
        when(ms.getMessage(eq("org.not.found"), any(), eq(LocaleContextHolder.getLocale()))).thenReturn("org.not.found");

        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.deleteById(1L));
        assertEquals("org.not.found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testSaveOrg() {
        when(organizationRepository.findByLicenseNumberOrEmail("LIC123", "test@org.com")).thenReturn(Optional.empty());
        when(userRepo.findByEmail("test@org.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword");
        when(organizationRepository.save(any(OrgEntity.class))).thenReturn(orgEntity);

        orgServices.saveOrg(orgEntity);

        verify(organizationRepository, times(1)).findByLicenseNumberOrEmail("LIC123", "test@org.com");
        verify(userRepo, times(1)).findByEmail("test@org.com");
        verify(passwordEncoder, times(1)).encode("password");
        verify(organizationRepository, times(1)).save(any(OrgEntity.class));
        verify(generalVerification, times(1)).sendOtpEmail(orgEntity);
    }

    @Test
    void testSaveOrgThrowsExceptionWhenEmailOrLicenseExists() {
        when(organizationRepository.findByLicenseNumberOrEmail("LIC123", "test@org.com")).thenReturn(Optional.of(orgEntity));
        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.saveOrg(orgEntity));
        assertEquals("org.email.license.exists", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testChangeOrgPassword() {
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(orgEntity));
        when(passwordEncoder.matches("oldPassword", "password")).thenReturn(true);
        when(passwordEncoder.encode("newPassword")).thenReturn("encodedNewPassword");

        orgServices.changeOrgPassword(1L, "oldPassword", "newPassword");

        verify(organizationRepository, times(1)).findById(1L);
        verify(passwordEncoder, times(1)).matches("oldPassword", "password");
        verify(passwordEncoder, times(1)).encode("newPassword");
        verify(organizationRepository, times(1)).save(orgEntity);
    }

    @Test
    void testChangeOrgPasswordThrowsExceptionWhenOrganizationNotFound() {
        when(organizationRepository.findById(1L)).thenReturn(Optional.empty());
        when(ms.getMessage(eq("org.not.found"), any(), eq(LocaleContextHolder.getLocale()))).thenReturn("org.not.found");

        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.changeOrgPassword(1L, "oldPassword", "newPassword"));
        assertEquals("org.not.found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
    }

    @Test
    void testChangeOrgPasswordThrowsExceptionWhenOldPasswordIncorrect() {
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(orgEntity));
        when(passwordEncoder.matches("oldPassword", "password")).thenReturn(false);
        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.changeOrgPassword(1L, "oldPassword", "newPassword"));
        assertEquals("org.old.password.incorrect", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testUpdateFcmToken() {
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(orgEntity));
        ResponseEntity<?> response = orgServices.updateFcmToken(1L, "newFcmToken");
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(organizationRepository, times(1)).findById(1L);
        verify(organizationRepository, times(1)).save(orgEntity);
    }

    @Test
    void testUpdateFcmTokenThrowsExceptionWhenOrganizationNotFound() {
        when(organizationRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<?> response = orgServices.updateFcmToken(1L, "newFcmToken");
        assertNotNull(response);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        verify(organizationRepository, times(1)).findById(1L);
    }

    @Test
    void testValidateOrganizationFields() {
        OrgEntity entity = new OrgEntity();
        entity.setLicenseNumber("LIC123");
        entity.setOrgName("Test Org");
        entity.setPassword("password");
        entity.setPhoneNumber("1234567890");
        entity.setScope(Scope.ORGANIZATION);
        entity.setCity("Test City");
        entity.setConservatism("Test Conservatism");
        entity.setFcmToken("fcmToken");

        assertDoesNotThrow(() -> orgServices.validateOrganizationFields(entity));
    }

    @Test
    void testValidateOrganizationFieldsThrowsExceptionWhenLicenseNumberIsNull() {
        OrgEntity entity = new OrgEntity();
        entity.setOrgName("Test Org");
        entity.setPassword("password");
        entity.setPhoneNumber("1234567890");
        entity.setScope(Scope.ORGANIZATION);
        entity.setCity("Test City");
        entity.setConservatism("Test Conservatism");
        entity.setFcmToken("fcmToken");

        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.validateOrganizationFields(entity));
        assertEquals("org.license.required", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testValidateOrganizationFieldsThrowsExceptionWhenOrgNameIsNull() {
        OrgEntity entity = new OrgEntity();
        entity.setLicenseNumber("LIC123");
        entity.setPassword("password");
        entity.setPhoneNumber("1234567890");
        entity.setScope(Scope.ORGANIZATION);
        entity.setCity("Test City");
        entity.setConservatism("Test Conservatism");
        entity.setFcmToken("fcmToken");

        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.validateOrganizationFields(entity));
        assertEquals("org.name.required", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testValidateOrganizationFieldsThrowsExceptionWhenPasswordIsNull() {
        OrgEntity entity = new OrgEntity();
        entity.setLicenseNumber("LIC123");
        entity.setOrgName("Test Org");
        entity.setPhoneNumber("1234567890");
        entity.setScope(Scope.ORGANIZATION);
        entity.setCity("Test City");
        entity.setConservatism("Test Conservatism");
        entity.setFcmToken("fcmToken");

        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.validateOrganizationFields(entity));
        assertEquals("org.password.required", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testValidateOrganizationFieldsThrowsExceptionWhenPhoneNumberIsNull() {
        OrgEntity entity = new OrgEntity();
        entity.setLicenseNumber("LIC123");
        entity.setOrgName("Test Org");
        entity.setPassword("password");
        entity.setScope(Scope.ORGANIZATION);
        entity.setCity("Test City");
        entity.setConservatism("Test Conservatism");
        entity.setFcmToken("fcmToken");

        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.validateOrganizationFields(entity));
        assertEquals("org.phone.required", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testValidateOrganizationFieldsThrowsExceptionWhenScopeIsNull() {
        OrgEntity entity = new OrgEntity();
        entity.setLicenseNumber("LIC123");
        entity.setOrgName("Test Org");
        entity.setPassword("password");
        entity.setPhoneNumber("1234567890");
        entity.setCity("Test City");
        entity.setConservatism("Test Conservatism");
        entity.setFcmToken("fcmToken");

        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.validateOrganizationFields(entity));
        assertEquals("org.scope.required", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testValidateOrganizationFieldsThrowsExceptionWhenCityIsNull() {
        OrgEntity entity = new OrgEntity();
        entity.setLicenseNumber("LIC123");
        entity.setOrgName("Test Org");
        entity.setPassword("password");
        entity.setPhoneNumber("1234567890");
        entity.setScope(Scope.ORGANIZATION);
        entity.setConservatism("Test Conservatism");
        entity.setFcmToken("fcmToken");

        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.validateOrganizationFields(entity));
        assertEquals("org.city.required", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testValidateOrganizationFieldsThrowsExceptionWhenConservatismIsNull() {
        OrgEntity entity = new OrgEntity();
        entity.setLicenseNumber("LIC123");
        entity.setOrgName("Test Org");
        entity.setPassword("password");
        entity.setPhoneNumber("1234567890");
        entity.setScope(Scope.ORGANIZATION);
        entity.setCity("Test City");
        entity.setFcmToken("fcmToken");

        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.validateOrganizationFields(entity));
        assertEquals("org.conservatism.required", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testValidateOrganizationFieldsThrowsExceptionWhenFcmTokenIsNull() {
        OrgEntity entity = new OrgEntity();
        entity.setLicenseNumber("LIC123");
        entity.setOrgName("Test Org");
        entity.setPassword("password");
        entity.setPhoneNumber("1234567890");
        entity.setScope(Scope.ORGANIZATION);
        entity.setCity("Test City");
        entity.setConservatism("Test Conservatism");

        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.validateOrganizationFields(entity));
        assertEquals("org.fcm.token.required", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testMessageParam() {
        when(ms.getMessage(eq("org.not.found"), any(), eq(LocaleContextHolder.getLocale()))).thenReturn("org.not.found");
        String result = orgServices.messageParam(1L, "org.not.found");
        assertEquals("org.not.found", result);
    }
}