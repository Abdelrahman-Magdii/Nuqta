package com.spring.nuqta.organization.Controller;

import com.spring.nuqta.organization.Dto.AddOrgDto;
import com.spring.nuqta.organization.Dto.OrgDto;
import com.spring.nuqta.organization.Dto.OrgRequestDto;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.organization.Mapper.AddOrgMapper;
import com.spring.nuqta.organization.Mapper.OrgMapper;
import com.spring.nuqta.organization.Mapper.OrgRequestMapper;
import com.spring.nuqta.organization.Services.OrgServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrgControllerTest {

    @Mock
    private OrgServices orgServices;

    @Mock
    private OrgMapper orgMapper;

    @Mock
    private AddOrgMapper addOrgMapper;

    @Mock
    private OrgRequestMapper orgRequestMapper;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private OrgController orgController;

    private OrgEntity orgEntity;
    private OrgDto orgDto;
    private AddOrgDto addOrgDto;
    private OrgRequestDto orgRequestDto;

    @BeforeEach
    void setUp() {
        orgEntity = new OrgEntity();
        orgEntity.setId(1L);
        orgEntity.setOrgName("Test Org");

        orgDto = new OrgDto();
        orgDto.setId(1L);
        orgDto.setOrgName("Test Org");

        addOrgDto = new AddOrgDto();
        addOrgDto.setId(1L);
        addOrgDto.setOrgName("Test Org");

        orgRequestDto = new OrgRequestDto();
        orgRequestDto.setId(1L);
        orgRequestDto.setOrgName("Test Org");
    }

    @Test
    void getAllOrg_ShouldReturnListOfOrganizations() {
        // Arrange
        List<OrgEntity> entities = Collections.singletonList(orgEntity);
        List<OrgDto> dtos = Collections.singletonList(orgDto);

        when(orgServices.findAll()).thenReturn(entities);
        when(orgMapper.map(entities)).thenReturn(dtos);

        // Act
        ResponseEntity<List<OrgDto>> response = orgController.getAllOrg();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtos, response.getBody());
        verify(orgServices).findAll();
        verify(orgMapper).map(entities);
    }

    @Test
    void getOrgById_ShouldReturnOrganization() {
        // Arrange
        when(orgServices.findById(1L)).thenReturn(orgEntity);
        when(orgMapper.map(orgEntity)).thenReturn(orgDto);

        // Act
        ResponseEntity<OrgDto> response = orgController.getOrgById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orgDto, response.getBody());
        verify(orgServices).findById(1L);
        verify(orgMapper).map(orgEntity);
    }

    @Test
    void updateOrg_ShouldUpdateOrganization() {
        // Arrange
        when(addOrgMapper.unMap(addOrgDto)).thenReturn(orgEntity);
        when(orgServices.update(orgEntity)).thenReturn(orgEntity);
        when(orgRequestMapper.map(orgEntity)).thenReturn(orgRequestDto);

        // Act
        ResponseEntity<OrgRequestDto> response = orgController.updateOrg(addOrgDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(orgRequestDto, response.getBody());
        verify(addOrgMapper).unMap(addOrgDto);
        verify(orgServices).update(orgEntity);
        verify(orgRequestMapper).map(orgEntity);
    }

    @Test
    void deleteOrgById_ShouldDeleteOrganization() {
        // Arrange
        Map<String, String> expectedResponse = new HashMap<>();
        expectedResponse.put("message", "Organization deleted successfully");

        when(messageSource.getMessage(eq("org.delete.success"), any(), any()))
                .thenReturn("Organization deleted successfully");

        // Act
        ResponseEntity<?> response = orgController.deleteOrgById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(orgServices).deleteById(1L);
        verify(messageSource).getMessage(eq("org.delete.success"), any(), any());
    }

    @Test
    void changePassword_ShouldChangePassword() {
        // Arrange
        Map<String, String> expectedResponse = new HashMap<>();
        expectedResponse.put("message", "Password changed successfully");

        when(messageSource.getMessage(eq("error.user.password.change"), any(), any()))
                .thenReturn("Password changed successfully");

        // Act
        ResponseEntity<Map<String, String>> response = orgController.changePassword(
                1L, "oldPass", "newPass");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(orgServices).changeOrgPassword(1L, "oldPass", "newPass");
        verify(messageSource).getMessage(eq("error.user.password.change"), any(), any());
    }


    @Test
    void getMS_ShouldReturnLocalizedMessage() {
        // Arrange
        when(messageSource.getMessage("test.key", null, LocaleContextHolder.getLocale()))
                .thenReturn("Test Message");

        // Act
        String result = orgController.getMS("test.key");

        // Assert
        assertEquals("Test Message", result);
        verify(messageSource).getMessage("test.key", null, LocaleContextHolder.getLocale());
    }
    

    @Test
    void updateFcmToken_ShouldHandleServiceErrors() {
        // Arrange
        Long orgId = 1L;
        String fcmToken = "new-fcm-token";
        Map<String, String> request = new HashMap<>();
        request.put("fcmToken", fcmToken);

        // Mock service throwing exception
        when(orgServices.updateFcmToken(orgId, fcmToken))
                .thenThrow(new RuntimeException("Service error"));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            orgController.updateFcmToken(orgId, request);
        });
    }
}