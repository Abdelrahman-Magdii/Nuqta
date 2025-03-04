package com.spring.nuqta.organization.Services;

import com.spring.nuqta.enums.Scope;
import com.spring.nuqta.exception.GlobalException;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.organization.Repo.OrgRepo;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import com.spring.nuqta.usermanagement.Repo.UserRepo;
import com.spring.nuqta.verificationToken.General.GeneralVerification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
        orgEntity.setPhoneNumber("1234567890");
        orgEntity.setScope(Scope.ORGANIZATION);
        orgEntity.setPassword("password");
        orgEntity.setLicenseNumber("LIC123");
        orgEntity.setCreatedDate(LocalDate.now());
        orgEntity.setCreatedUser("Test User");
        orgEntity.setModifiedDate(LocalDate.now());
        orgEntity.setModifiedUser("Test User");
        orgEntity.setConservatism("conservatism");
        orgEntity.setCity("city");
    }

    @Test
    void testFindAll() {
        when(organizationRepository.findAllByEnabledTrue()).thenReturn(Collections.singletonList(orgEntity));

        List<OrgEntity> result = orgServices.findAll();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(orgEntity, result.get(0));

        verify(organizationRepository, times(1)).findAllByEnabledTrue();
    }

    @Test
    void testFindAllThrowsException() {
        when(organizationRepository.findAllByEnabledTrue()).thenReturn(Collections.emptyList()); // ✅ Mock the correct method

        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.findAll());

        assertEquals("No organizations found", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verify(organizationRepository, times(1)).findAllByEnabledTrue();  // ✅ Verify the correct method
    }

    @Test
    void testFindById() {
        when(organizationRepository.findByIdAndEnabledTrue(1L)).thenReturn(Optional.of(orgEntity));

        OrgEntity result = orgServices.findById(1L);

        assertNotNull(result);
        assertEquals(orgEntity, result);

        verify(organizationRepository, times(1)).findByIdAndEnabledTrue(1L);
    }

    @Test
    void testFindByIdThrowsException() {
        when(organizationRepository.findByIdAndEnabledTrue(1L)).thenReturn(Optional.empty());  // ✅ Mock the correct method

        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.findById(1L));

        assertEquals("Organization not found with ID: 1", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verify(organizationRepository, times(1)).findByIdAndEnabledTrue(1L);  // ✅ Verify the correct method
    }


    @Test
    void testUpdate() {
        when(organizationRepository.findById(1L)).thenReturn(Optional.of(orgEntity));
        when(organizationRepository.save(any(OrgEntity.class))).thenReturn(orgEntity);

        OrgEntity updatedEntity = new OrgEntity();
        updatedEntity.setId(1L);
        updatedEntity.setOrgName("Updated Org");
        updatedEntity.setCity("New City");
        updatedEntity.setConservatism("New Conservatism");
        updatedEntity.setPhoneNumber("0987654321");
        updatedEntity.setScope(Scope.ORGANIZATION);

        OrgEntity result = orgServices.update(updatedEntity);

        assertNotNull(result);
        assertEquals("Updated Org", result.getOrgName());
        assertEquals("New City", result.getCity());
        assertEquals("New Conservatism", result.getConservatism());
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
        when(organizationRepository.existsById(1L)).thenReturn(true);

        orgServices.deleteById(1L);

        verify(organizationRepository, times(1)).existsById(1L);
        verify(organizationRepository, times(1)).deleteById(1L);
    }

    @Test
    void testDeleteByIdThrowsException() {
        when(organizationRepository.existsById(1L)).thenReturn(false);  // Mock existsById instead of findById

        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.deleteById(1L));

        assertEquals("Organization not found with ID: 1", exception.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());

        verify(organizationRepository, times(1)).existsById(1L);  // Verify correct method call
        verify(organizationRepository, never()).deleteById(1L);  // Ensure deleteById is never called
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
        orgEntity.setConservatism("conservatism");
        orgEntity.setCity("city");

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
        orgEntity.setConservatism("conservatism");
        orgEntity.setCity("city");

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
        orgEntity.setConservatism("conservatism");
        orgEntity.setCity("city");

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
        orgEntity.setConservatism("conservatism");
        orgEntity.setCity("city");

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
        orgEntity.setConservatism("conservatism");
        orgEntity.setCity("city");

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
        orgEntity.setConservatism("conservatism");
        orgEntity.setCity("city");

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
        orgEntity.setConservatism("conservatism");
        orgEntity.setCity("city");

        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.validateOrganizationFields(orgEntity));

        assertEquals("Scope must be set to 'organization'.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testValidateOrganizationFields_ConservatismMissing() {
        OrgEntity orgEntity = new OrgEntity();
        orgEntity.setLicenseNumber("LIC123");
        orgEntity.setOrgName("Test Org");
        orgEntity.setPassword("password");
        orgEntity.setPhoneNumber("1234567890");
        orgEntity.setScope(Scope.ORGANIZATION);
        orgEntity.setCity("city");

        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.validateOrganizationFields(orgEntity));

        assertEquals("Conservatism is required.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testValidateOrganizationFields_CityMissing() {
        OrgEntity orgEntity = new OrgEntity();
        orgEntity.setLicenseNumber("LIC123");
        orgEntity.setOrgName("Test Org");
        orgEntity.setPassword("password");
        orgEntity.setPhoneNumber("1234567890");
        orgEntity.setScope(Scope.ORGANIZATION);
        orgEntity.setConservatism("city");

        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.validateOrganizationFields(orgEntity));

        assertEquals("City is required.", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void testUpdateOrgThrowsExceptionWhenNameExists() {
        // Given: An organization with the same name but a different ID already exists
        OrgEntity entity = new OrgEntity();
        entity.setId(2L);
        entity.setOrgName("ExistingOrg");

        // Ensure organization exists before checking for duplicate name
        when(organizationRepository.findById(2L)).thenReturn(Optional.of(entity));
        when(organizationRepository.existsByOrgNameAndIdNot("ExistingOrg", 2L)).thenReturn(true);

        // When & Then: Expect an exception when calling the update method
        GlobalException exception = assertThrows(GlobalException.class, () -> orgServices.update(entity));

        assertEquals("Organization name already exists", exception.getMessage());
        assertEquals(HttpStatus.CONFLICT, exception.getStatus());

        verify(organizationRepository, times(1)).findById(2L);
        verify(organizationRepository, times(1)).existsByOrgNameAndIdNot("ExistingOrg", 2L);
    }

    @Test
    void testUpdateOrgSuccessWhenNameDoesNotExist() {
        OrgEntity entity = new OrgEntity();
        entity.setId(2L);
        entity.setOrgName("UniqueOrg");

        // Ensure the organization exists in the database
        when(organizationRepository.findById(2L)).thenReturn(Optional.of(entity));
        when(organizationRepository.existsByOrgNameAndIdNot("UniqueOrg", 2L)).thenReturn(false);
        when(organizationRepository.save(any(OrgEntity.class))).thenReturn(entity);

        OrgEntity updatedEntity = orgServices.update(entity);

        assertNotNull(updatedEntity);
        assertEquals("UniqueOrg", updatedEntity.getOrgName());

        verify(organizationRepository, times(1)).findById(2L);
        verify(organizationRepository, times(1)).existsByOrgNameAndIdNot("UniqueOrg", 2L);
        verify(organizationRepository, times(1)).save(entity);
    }


    @Test
    void shouldThrowExceptionWhenOrganizationAlreadyExists() {
        // Mock: Organization already exists
        when(organizationRepository.findByLicenseNumberOrEmail(orgEntity.getLicenseNumber(), orgEntity.getEmail()))
                .thenReturn(Optional.of(orgEntity));

        when(userRepo.findByEmail(orgEntity.getEmail()))
                .thenReturn(Optional.empty());

        // Test and assert exception
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            orgServices.saveOrg(orgEntity);
        });

        assertEquals("Organization Email or License Number already exists", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        // Verify repository interactions
        verify(organizationRepository, times(1)).findByLicenseNumberOrEmail(orgEntity.getLicenseNumber(), orgEntity.getEmail());
        verify(userRepo, times(1)).findByEmail(orgEntity.getEmail());
        verifyNoMoreInteractions(organizationRepository, userRepo);
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyExists() {
        // Mock: User already exists
        when(organizationRepository.findByLicenseNumberOrEmail(orgEntity.getLicenseNumber(), orgEntity.getEmail()))
                .thenReturn(Optional.empty());

        when(userRepo.findByEmail(orgEntity.getEmail()))
                .thenReturn(Optional.of(new UserEntity()));

        // Test and assert exception
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            orgServices.saveOrg(orgEntity);
        });

        assertEquals("Organization Email or License Number already exists", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());

        // Verify repository interactions
        verify(organizationRepository, times(1)).findByLicenseNumberOrEmail(orgEntity.getLicenseNumber(), orgEntity.getEmail());
        verify(userRepo, times(1)).findByEmail(orgEntity.getEmail());
        verifyNoMoreInteractions(organizationRepository, userRepo);
    }

    @Test
    void shouldSaveOrganizationWhenNoDuplicateExists() {
        // Mock: No existing organization or user
        when(organizationRepository.findByLicenseNumberOrEmail(orgEntity.getLicenseNumber(), orgEntity.getEmail()))
                .thenReturn(Optional.empty());
        when(userRepo.findByEmail(orgEntity.getEmail()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.encode(orgEntity.getPassword())).thenReturn("encodedPassword");
        when(organizationRepository.save(any(OrgEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Test
        assertDoesNotThrow(() -> orgServices.saveOrg(orgEntity));

        // Verify repository interactions
        verify(organizationRepository, times(1)).findByLicenseNumberOrEmail(orgEntity.getLicenseNumber(), orgEntity.getEmail());
        verify(userRepo, times(1)).findByEmail(orgEntity.getEmail());
        verify(organizationRepository, times(1)).save(any(OrgEntity.class));
        verify(generalVerification, times(1)).sendOtpEmail(any(OrgEntity.class));
    }

}