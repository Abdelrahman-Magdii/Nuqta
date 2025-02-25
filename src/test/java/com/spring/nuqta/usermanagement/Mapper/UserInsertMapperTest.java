package com.spring.nuqta.usermanagement.Mapper;

import com.spring.nuqta.donation.Dto.DonDto;
import com.spring.nuqta.donation.Entity.DonEntity;
import com.spring.nuqta.usermanagement.Dto.UserInsertDto;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static com.spring.nuqta.enums.DonStatus.COMPLETED;
import static com.spring.nuqta.enums.DonStatus.PENDING;
import static org.assertj.core.api.Assertions.assertThat;

class UserInsertMapperTest {

    private UserInsertMapper userInsertMapper;

    @BeforeEach
    void setUp() {
        userInsertMapper = Mappers.getMapper(UserInsertMapper.class); // Assuming MapStruct generates an implementation
    }

    @Test
    void mapDonationToDto_AllAttributes() {
        // Arrange
        DonEntity donation = new DonEntity();
        donation.setId(1L);
        donation.setAmount(100.0);
        donation.setDonationDate(LocalDate.of(2024, 1, 1));
        donation.setLastDonation(LocalDate.of(2023, 12, 1));
        donation.setStatus(COMPLETED);
        donation.setPaymentOffered(true);
        donation.setBloodType("O+");
        donation.setWeight(75.0);
        donation.setLocation(new GeometryFactory().createPoint(new Coordinate(40.0, -75.0)));

        // Act
        DonDto result = userInsertMapper.mapDonationToDto(donation);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getAmount()).isEqualTo(100.0);
        assertThat(result.getDonationDate()).isEqualTo(LocalDate.of(2024, 1, 1));
        assertThat(result.getLastDonation()).isEqualTo(LocalDate.of(2023, 12, 1));
        assertThat(result.getStatus()).isEqualTo(COMPLETED);
        assertThat(result.getPaymentOffered()).isTrue();
        assertThat(result.getBloodType()).isEqualTo("O+");
        assertThat(result.getWeight()).isEqualTo(75.0);
        assertThat(result.getLatitude()).isEqualTo(40.0);
        assertThat(result.getLongitude()).isEqualTo(-75.0);
    }

    @Test
    void mapDonationToEntity_AllAttributes() {
        // Arrange
        DonDto donationDto = new DonDto();
        donationDto.setId(2L);
        donationDto.setAmount(200.0);
        donationDto.setDonationDate(LocalDate.of(2024, 2, 1));
        donationDto.setLastDonation(LocalDate.of(2023, 11, 1));
        donationDto.setStatus(PENDING);
        donationDto.setPaymentOffered(false);
        donationDto.setBloodType("A-");
        donationDto.setWeight(80.0);
        donationDto.setLatitude(30.0);
        donationDto.setLongitude(-85.0);

        // Act
        DonEntity result = userInsertMapper.mapDonationToEntity(donationDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getAmount()).isEqualTo(200.0);
        assertThat(result.getDonationDate()).isEqualTo(LocalDate.of(2024, 2, 1));
        assertThat(result.getLastDonation()).isEqualTo(LocalDate.of(2023, 11, 1));
        assertThat(result.getStatus()).isEqualTo(PENDING);
        assertThat(result.getPaymentOffered()).isFalse();
        assertThat(result.getBloodType()).isEqualTo("A-");
        assertThat(result.getWeight()).isEqualTo(80.0);
        assertThat(result.getLocation().getCoordinate().getX()).isEqualTo(30.0);
        assertThat(result.getLocation().getCoordinate().getY()).isEqualTo(-85.0);
        assertThat(result.getCreatedDate()).isEqualTo(LocalDate.now()); // Ensuring createdDate is set
        assertThat(result.getModifiedDate()).isEqualTo(LocalDate.now()); // Ensuring modifiedDate is set
    }

    @Test
    void mapUserEntityToDto_AllAttributes() {
        // Arrange
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("John Doe");
        userEntity.setEmail("john.doe@example.com");
        userEntity.setPhoneNumber("1234567890");
        userEntity.setBirthDate(LocalDate.of(1990, 5, 20));

        // Act
        UserInsertDto result = userInsertMapper.map(userEntity);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getUsername()).isEqualTo("John Doe");
        assertThat(result.getEmail()).isEqualTo("john.doe@example.com");
        assertThat(result.getPhoneNumber()).isEqualTo("1234567890");
        assertThat(result.getBirthDate()).isEqualTo(LocalDate.of(1990, 5, 20));
    }

    @Test
    void unMapUserInsertDtoToEntity_AllAttributes() {
        // Arrange
        UserInsertDto userDto = new UserInsertDto();
        userDto.setId(2L);
        userDto.setUsername("Jane Doe");
        userDto.setEmail("jane.doe@example.com");
        userDto.setPhoneNumber("0987654321");
        userDto.setBirthDate(LocalDate.of(1995, 7, 15));

        // Act
        UserEntity result = userInsertMapper.unMap(userDto);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getUsername()).isEqualTo("Jane Doe");
        assertThat(result.getEmail()).isEqualTo("jane.doe@example.com");
        assertThat(result.getPhoneNumber()).isEqualTo("0987654321");
        assertThat(result.getBirthDate()).isEqualTo(LocalDate.of(1995, 7, 15));
    }
}
