package com.spring.nuqta.donation.Controller;

import com.spring.nuqta.donation.Dto.AcceptDonationRequestDto;
import com.spring.nuqta.donation.Dto.DonDto;
import com.spring.nuqta.donation.Dto.DonResponseDto;
import com.spring.nuqta.donation.Entity.DonEntity;
import com.spring.nuqta.donation.Mapper.DonMapper;
import com.spring.nuqta.donation.Mapper.DonResponseMapper;
import com.spring.nuqta.donation.Services.DonServices;
import com.spring.nuqta.exception.GlobalException;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DonControllerTest {

    @Mock
    private DonServices donServices;

    @Mock
    private DonMapper donMapper;

    @Mock
    private DonResponseMapper donResponseMapper;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private DonController donController;

    private DonEntity donEntity;
    private DonDto donDto;
    private DonResponseDto donResponseDto;
    private AcceptDonationRequestDto acceptDonationRequestDto;

    @BeforeEach
    void setUp() {
        donEntity = new DonEntity();
        donEntity.setId(1L);
        donEntity.setConservatism("Test Donation");

        donDto = new DonDto();
        donDto.setId(1L);
        donDto.setConservatism("Test Donation");

        donResponseDto = new DonResponseDto();
        donResponseDto.setId(1L);
        donResponseDto.setConservatism("Test Donation");

        acceptDonationRequestDto = new AcceptDonationRequestDto();
        acceptDonationRequestDto.setDonationId(1L);
        acceptDonationRequestDto.setRequestId(1L);
    }

    @Test
    void getDonationById_ShouldReturnDonation() {
        // Arrange
        when(donServices.findById(1L)).thenReturn(donEntity);
        when(donResponseMapper.map(donEntity)).thenReturn(donResponseDto);

        // Act
        ResponseEntity<?> response = donController.getDonationById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(donResponseDto, response.getBody());
        verify(donServices).findById(1L);
        verify(donResponseMapper).map(donEntity);
    }

    @Test
    void getNearestDonationsByConservatism_ShouldReturnDonations() {
        // Arrange
        String conservatism = "high";
        List<DonEntity> entities = Collections.singletonList(donEntity);
        List<DonDto> dtos = Collections.singletonList(donDto);

        when(donServices.findTopConservatism(conservatism)).thenReturn(entities);
        when(donMapper.map(entities)).thenReturn(dtos);

        // Act
        ResponseEntity<?> response = donController.getNearestDonations(conservatism);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtos, response.getBody());
        verify(donServices).findTopConservatism(conservatism);
        verify(donMapper).map(entities);
    }

    @Test
    void getNearestDonationsByCity_ShouldReturnDonations() {
        // Arrange
        String city = "Cairo";
        List<DonEntity> entities = Collections.singletonList(donEntity);
        List<DonDto> dtos = Collections.singletonList(donDto);

        when(donServices.findTopCity(city)).thenReturn(entities);
        when(donMapper.map(entities)).thenReturn(dtos);

        // Act
        ResponseEntity<?> response = donController.getNearestDonationsCity(city);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtos, response.getBody());
        verify(donServices).findTopCity(city);
        verify(donMapper).map(entities);
    }

    @Test
    void acceptDonationRequest_ShouldReturnUpdatedDonation() {
        // Arrange
        when(donServices.acceptDonationRequest(acceptDonationRequestDto)).thenReturn(donEntity);
        when(donResponseMapper.map(donEntity)).thenReturn(donResponseDto);

        // Act
        ResponseEntity<?> response = donController.acceptDonationRequest(acceptDonationRequestDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(donResponseDto, response.getBody());
        verify(donServices).acceptDonationRequest(acceptDonationRequestDto);
        verify(donResponseMapper).map(donEntity);
    }

    @Test
    void deleteAcceptedDonationRequest_ShouldReturnSuccessMessage() {
        // Arrange
        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("message", "Request deleted successfully");

        when(messageSource.getMessage(eq("success.donation.requestDeleted"), any(), any()))
                .thenReturn("Request deleted successfully");

        // Act
        ResponseEntity<Map<String, Object>> response =
                donController.deleteAcceptedDonationRequest(acceptDonationRequestDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(donServices).deleteAcceptedDonationRequest(acceptDonationRequestDto);
    }

    @Test
    void deleteAcceptedDonationRequest_ShouldHandleGlobalException() {
        // Arrange
        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("message", "Global error occurred");

        GlobalException exception = new GlobalException("error.message", HttpStatus.BAD_REQUEST);
        doThrow(exception).when(donServices).deleteAcceptedDonationRequest(acceptDonationRequestDto);

        when(messageSource.getMessage(eq("error.globalException"), any(), any()))
                .thenReturn("Global error occurred");

        // Act
        ResponseEntity<Map<String, Object>> response =
                donController.deleteAcceptedDonationRequest(acceptDonationRequestDto);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(donServices).deleteAcceptedDonationRequest(acceptDonationRequestDto);
    }

    @Test
    void deleteAcceptedDonationRequest_ShouldHandleUnknownException() {
        // Arrange
        Map<String, Object> expectedResponse = new HashMap<>();
        expectedResponse.put("message", "Unknown error occurred");

        doThrow(new RuntimeException()).when(donServices).deleteAcceptedDonationRequest(acceptDonationRequestDto);

        when(messageSource.getMessage(eq("error.unknown"), any(), any()))
                .thenReturn("Unknown error occurred");

        // Act
        ResponseEntity<Map<String, Object>> response =
                donController.deleteAcceptedDonationRequest(acceptDonationRequestDto);

        // Assert
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(donServices).deleteAcceptedDonationRequest(acceptDonationRequestDto);
    }

    @Test
    void getMS_ShouldReturnLocalizedMessage() {
        // Arrange
        when(messageSource.getMessage("test.key", null, LocaleContextHolder.getLocale()))
                .thenReturn("Test Message");

        // Act
        String result = donController.getMS("test.key");

        // Assert
        assertEquals("Test Message", result);
        verify(messageSource).getMessage("test.key", null, LocaleContextHolder.getLocale());
    }
}