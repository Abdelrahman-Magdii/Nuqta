package com.spring.nuqta.donation.Mapper;

import com.spring.nuqta.donation.Dto.DonDto;
import com.spring.nuqta.donation.Entity.DonEntity;
import com.spring.nuqta.enums.Level;
import com.spring.nuqta.enums.Status;
import com.spring.nuqta.request.Dto.ReqDto;
import com.spring.nuqta.request.Entity.ReqEntity;
import com.spring.nuqta.usermanagement.Mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class DonMapperTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private DonMapper donMapper = Mappers.getMapper(DonMapper.class);

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testMapDonEntityToDonDto() {
        // Arrange
        ReqEntity reqEntity = new ReqEntity();
        reqEntity.setId(1L);
        reqEntity.setStatus(Status.FULFILLED);
        reqEntity.setAmount(100.0);
        reqEntity.setRequestDate(LocalDate.now());
        reqEntity.setBloodTypeNeeded("A+");
        reqEntity.setPaymentAvailable(true);
        reqEntity.setUrgencyLevel(Level.HIGH);
        reqEntity.setConservatism("Low");
        reqEntity.setCity("New York");

        Set<ReqEntity> reqEntities = new HashSet<>();
        reqEntities.add(reqEntity);

        DonEntity donEntity = new DonEntity();
        donEntity.setId(1L);
        donEntity.setAcceptedRequests(reqEntities);

        // Mock UserMapper behavior if needed
        // when(userMapper.map(any())).thenReturn(...);

        // Act
        DonDto donDto = donMapper.map(donEntity);

        // Assert
        assertNotNull(donDto);
        assertEquals(donEntity.getId(), donDto.getId());
        assertNotNull(donDto.getAcceptedRequests());
        assertEquals(1, donDto.getAcceptedRequests().size());

        ReqDto reqDto = donDto.getAcceptedRequests().iterator().next();
        assertEquals(reqEntity.getId(), reqDto.getId());
        assertEquals(reqEntity.getStatus(), reqDto.getStatus());
        assertEquals(reqEntity.getAmount(), reqDto.getAmount());
        assertEquals(reqEntity.getRequestDate(), reqDto.getRequestDate());
        assertEquals(reqEntity.getBloodTypeNeeded(), reqDto.getBloodTypeNeeded());
        assertEquals(reqEntity.getPaymentAvailable(), reqDto.getPaymentAvailable());
        assertEquals(reqEntity.getUrgencyLevel(), reqDto.getUrgencyLevel());
        assertEquals(reqEntity.getConservatism(), reqDto.getConservatism());
        assertEquals(reqEntity.getCity(), reqDto.getCity());
    }

    @Test
    void testMapDonDtoToDonEntity() {
        // Arrange
        ReqDto reqDto = new ReqDto();
        reqDto.setId(1L);
        reqDto.setStatus(Status.FULFILLED);
        reqDto.setAmount(100.0);
        reqDto.setRequestDate(LocalDate.now());
        reqDto.setBloodTypeNeeded("A+");
        reqDto.setPaymentAvailable(true);
        reqDto.setUrgencyLevel(Level.HIGH);
        reqDto.setConservatism("Low");
        reqDto.setCity("New York");

        Set<ReqDto> reqDtos = new HashSet<>();
        reqDtos.add(reqDto);

        DonDto donDto = new DonDto();
        donDto.setId(1L);
        donDto.setAcceptedRequests(reqDtos);

        // Mock UserMapper behavior if needed
        // when(userMapper.unMap(any())).thenReturn(...);

        // Act
        DonEntity donEntity = donMapper.unMap(donDto);

        // Assert
        assertNotNull(donEntity);
        assertEquals(donDto.getId(), donEntity.getId());
        assertNotNull(donEntity.getAcceptedRequests());
        assertEquals(1, donEntity.getAcceptedRequests().size());

        ReqEntity reqEntity = donEntity.getAcceptedRequests().iterator().next();
        assertEquals(reqDto.getId(), reqEntity.getId());
        assertEquals(reqDto.getStatus(), reqEntity.getStatus());
        assertEquals(reqDto.getAmount(), reqEntity.getAmount());
        assertEquals(reqDto.getRequestDate(), reqEntity.getRequestDate());
        assertEquals(reqDto.getBloodTypeNeeded(), reqEntity.getBloodTypeNeeded());
        assertEquals(reqDto.getPaymentAvailable(), reqEntity.getPaymentAvailable());
        assertEquals(reqDto.getUrgencyLevel(), reqEntity.getUrgencyLevel());
        assertEquals(reqDto.getConservatism(), reqEntity.getConservatism());
        assertEquals(reqDto.getCity(), reqEntity.getCity());
    }

    @Test
    void testMapReqEntityToDto_NullInput() {
        // Act
        ReqDto reqDto = donMapper.mapReqEntityToDto(null);

        // Assert
        assertNull(reqDto);
    }

    @Test
    void testMapReqDtoToEntity_NullInput() {
        // Act
        ReqEntity reqEntity = donMapper.mapReqDtoToEntity(null);

        // Assert
        assertNull(reqEntity);
    }
}