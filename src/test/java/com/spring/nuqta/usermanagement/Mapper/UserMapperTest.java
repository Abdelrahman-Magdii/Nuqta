package com.spring.nuqta.usermanagement.Mapper;

import com.spring.nuqta.donation.Dto.DonDto;
import com.spring.nuqta.donation.Entity.DonEntity;
import com.spring.nuqta.request.Dto.ReqDto;
import com.spring.nuqta.request.Entity.ReqEntity;
import com.spring.nuqta.usermanagement.Dto.UserDto;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class UserMapperTest {

    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        userMapper = Mappers.getMapper(UserMapper.class);
    }

    @Test
    void testMapUserEntityToUserDto() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setBirthDate(LocalDate.of(2003, 1, 1));
        userEntity.setDonation(createDonEntity());
        userEntity.setRequests(Set.of(createReqEntity()));

        // Act
        UserDto userDto = userMapper.map(userEntity);

        // Assert
        assertNotNull(userDto);
        assertEquals(22, userDto.getAge());
        assertNotNull(userDto.getDonation());
        assertEquals(1, userDto.getRequests().size());
    }

    @Test
    void testUnMapUserDtoToUserEntity() {
        // Arrange
        UserDto userDto = new UserDto();
        userDto.setAge(30);
        userDto.setDonation(createDonDto());
        userDto.setRequests(List.of(createReqDto()));

        // Act
        UserEntity userEntity = userMapper.unMap(userDto);

        // Assert
        assertNotNull(userEntity);
        assertEquals(LocalDate.now().minusYears(30).withDayOfYear(1), userEntity.getBirthDate());
        assertNotNull(userEntity.getDonation());
        assertEquals(1, userEntity.getRequests().size());
    }

    @Test
    void testMapDonationToDto() {
        // Arrange
        DonEntity donEntity = createDonEntity();

        // Act
        DonDto donDto = userMapper.mapDonationToDto(donEntity);

        // Assert
        assertNotNull(donDto);
        assertEquals(donEntity.getId(), donDto.getId());
        assertEquals(donEntity.getAmount(), donDto.getAmount());
        assertEquals(donEntity.getLocation().getCoordinate().getX(), donDto.getLatitude());
        assertEquals(donEntity.getLocation().getCoordinate().getY(), donDto.getLongitude());
    }

    @Test
    void testMapDonationToEntity() {
        // Arrange
        DonDto donDto = createDonDto();

        // Act
        DonEntity donEntity = userMapper.mapDonationToEntity(donDto);

        // Assert
        assertNotNull(donEntity);
        assertEquals(donDto.getId(), donEntity.getId());
        assertEquals(donDto.getAmount(), donEntity.getAmount());
        assertEquals(donDto.getLatitude(), donEntity.getLocation().getCoordinate().getX());
        assertEquals(donDto.getLongitude(), donEntity.getLocation().getCoordinate().getY());
    }

    @Test
    void testMapRequestSetToDtoList() {
        // Arrange
        Set<ReqEntity> reqEntities = Set.of(createReqEntity());

        // Act
        List<ReqDto> reqDtos = userMapper.mapRequestSetToDtoList(reqEntities);

        // Assert
        assertNotNull(reqDtos);
        assertEquals(1, reqDtos.size());
        assertEquals(reqEntities.iterator().next().getId(), reqDtos.get(0).getId());
    }

    @Test
    void testMapRequestDtoListToEntitySet() {
        // Arrange
        List<ReqDto> reqDtos = List.of(createReqDto());

        // Act
        Set<ReqEntity> reqEntities = userMapper.mapRequestDtoListToEntitySet(reqDtos);

        // Assert
        assertNotNull(reqEntities);
        assertEquals(1, reqEntities.size());
        assertEquals(reqDtos.get(0).getId(), reqEntities.iterator().next().getId());
    }

    @Test
    void testCalculateAgeFromBirthDate() {
        // Arrange
        LocalDate birthDate = LocalDate.of(2003, 3, 10);

        // Act
        int age = userMapper.calculateAgeFromBirthDate(birthDate);

        // Assert
        assertEquals(21, age); // Assuming the current year is 2023
    }

    @Test
    void testCalculateBirthDateFromAge() {
        // Arrange
        int age = 22;

        // Act
        LocalDate birthDate = userMapper.calculateBirthDateFromAge(age);

        System.out.println(birthDate);
        // Assert
        assertEquals(LocalDate.now().minusYears(age).withDayOfYear(1), birthDate);
    }

    private DonEntity createDonEntity() {
        DonEntity donEntity = new DonEntity();
        donEntity.setId(1L);
        donEntity.setAmount(100.0);
        donEntity.setLocation(new GeometryFactory().createPoint(new Coordinate(10.0, 20.0)));
        return donEntity;
    }

    private DonDto createDonDto() {
        DonDto donDto = new DonDto();
        donDto.setId(1L);
        donDto.setAmount(100.0);
        donDto.setLatitude(10.0);
        donDto.setLongitude(20.0);
        return donDto;
    }

    private ReqEntity createReqEntity() {
        ReqEntity reqEntity = new ReqEntity();
        reqEntity.setId(1L);
        reqEntity.setAmount(50.0);
        reqEntity.setLocation(new GeometryFactory().createPoint(new Coordinate(15.0, 25.0)));
        return reqEntity;
    }

    private ReqDto createReqDto() {
        ReqDto reqDto = new ReqDto();
        reqDto.setId(1L);
        reqDto.setAmount(50.0);
        reqDto.setLatitude(15.0);
        reqDto.setLongitude(25.0);
        return reqDto;
    }
}