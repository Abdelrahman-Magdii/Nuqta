package com.spring.nuqta.request.Controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.spring.nuqta.exception.GlobalException;
import com.spring.nuqta.request.Dto.AddReqDto;
import com.spring.nuqta.request.Dto.ReqDto;
import com.spring.nuqta.request.Entity.ReqEntity;
import com.spring.nuqta.request.Mapper.AddReqMapper;
import com.spring.nuqta.request.Mapper.ReqMapper;
import com.spring.nuqta.request.Services.ReqServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReqControllerTest {

    @Mock
    private ReqServices reqServices;

    @Mock
    private ReqMapper reqMapper;

    @Mock
    private AddReqMapper addReqMapper;

    @Mock
    private MessageSource messageSource;

    @InjectMocks
    private ReqController reqController;

    private ReqEntity reqEntity;
    private ReqDto reqDto;
    private AddReqDto addReqDto;

    @BeforeEach
    void setUp() {
        reqEntity = new ReqEntity();
        reqEntity.setId(1L);

        reqDto = new ReqDto();
        reqDto.setId(1L);

        addReqDto = new AddReqDto();
        addReqDto.setId(1L);
        addReqDto.setUserId(1L);
    }

    @Test
    void getAllReq_ShouldReturnListOfRequests() {
        // Arrange
        List<ReqEntity> entities = Arrays.asList(reqEntity);
        List<ReqDto> dtos = Arrays.asList(reqDto);

        when(reqServices.findAll()).thenReturn(entities);
        when(reqMapper.map(entities)).thenReturn(dtos);

        // Act
        ResponseEntity<?> response = reqController.getAllReq();

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(dtos, response.getBody());
        verify(reqServices).findAll();
        verify(reqMapper).map(entities);
    }

    @Test
    void getReqById_ShouldReturnRequest() {
        // Arrange
        when(reqServices.findById(1L)).thenReturn(reqEntity);
        when(reqMapper.map(reqEntity)).thenReturn(reqDto);

        // Act
        ResponseEntity<?> response = reqController.getReqById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(reqDto, response.getBody());
        verify(reqServices).findById(1L);
        verify(reqMapper).map(reqEntity);
    }

    @Test
    void addRequest_WithUserId_ShouldCreateRequest() throws FirebaseMessagingException {
        // Arrange
        when(addReqMapper.unMap(addReqDto)).thenReturn(reqEntity);
        when(reqServices.addRequest(reqEntity, 1L, false)).thenReturn(reqEntity);
        when(addReqMapper.map(reqEntity)).thenReturn(addReqDto);

        // Act
        ResponseEntity<?> response = reqController.addRequest(addReqDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(addReqDto, response.getBody());
        verify(addReqMapper).unMap(addReqDto);
        verify(reqServices).addRequest(reqEntity, 1L, false);
        verify(addReqMapper).map(reqEntity);
    }

    @Test
    void addRequest_WithOrgId_ShouldCreateRequest() throws FirebaseMessagingException {
        // Arrange
        addReqDto.setUserId(null);
        addReqDto.setOrgId(1L);

        when(addReqMapper.unMap(addReqDto)).thenReturn(reqEntity);
        when(reqServices.addRequest(reqEntity, 1L, true)).thenReturn(reqEntity);
        when(addReqMapper.map(reqEntity)).thenReturn(addReqDto);

        // Act
        ResponseEntity<?> response = reqController.addRequest(addReqDto);

        // Assert
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(addReqDto, response.getBody());
        verify(addReqMapper).unMap(addReqDto);
        verify(reqServices).addRequest(reqEntity, 1L, true);
        verify(addReqMapper).map(reqEntity);
    }

    @Test
    void addRequest_WithoutIds_ShouldThrowException() {
        // Arrange
        addReqDto.setUserId(null);
        addReqDto.setOrgId(null);

        // Act & Assert
        GlobalException exception = assertThrows(GlobalException.class, () -> {
            reqController.addRequest(addReqDto);
        });

        assertEquals("error.request.notfoundID", exception.getMessage());
        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatus());
    }

    @Test
    void updateReq_ShouldUpdateRequest() {
        // Arrange
        when(addReqMapper.unMap(addReqDto)).thenReturn(reqEntity);
        when(reqServices.update(reqEntity)).thenReturn(reqEntity);
        when(addReqMapper.map(reqEntity)).thenReturn(addReqDto);

        // Act
        ResponseEntity<?> response = reqController.updateReq(addReqDto);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(addReqDto, response.getBody());
        verify(addReqMapper).unMap(addReqDto);
        verify(reqServices).update(reqEntity);
        verify(addReqMapper).map(reqEntity);
    }

    @Test
    void deleteReqById_ShouldDeleteRequest() {
        // Arrange
        Map<String, String> expectedResponse = new HashMap<>();
        expectedResponse.put("message", "Request deleted successfully");

        when(messageSource.getMessage(eq("error.request.delete"), any(), any()))
                .thenReturn("Request deleted successfully");

        // Act
        ResponseEntity<?> response = reqController.deleteReqById(1L);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse, response.getBody());
        verify(reqServices).ReCache(1L);
        verify(reqServices).deleteById(1L);
        verify(messageSource).getMessage(eq("error.request.delete"), any(), any());
    }

    @Test
    void addRequest_ShouldHandleFirebaseException() throws FirebaseMessagingException {
        // Arrange
        when(addReqMapper.unMap(addReqDto)).thenReturn(reqEntity);
        FirebaseMessagingException exception = mock(FirebaseMessagingException.class);
        when(reqServices.addRequest(reqEntity, 1L, false))
                .thenThrow(exception);

        // Act & Assert
        assertThrows(FirebaseMessagingException.class, () -> {
            reqController.addRequest(addReqDto);
        });
    }
}