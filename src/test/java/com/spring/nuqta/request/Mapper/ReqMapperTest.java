package com.spring.nuqta.request.Mapper;

import com.spring.nuqta.donation.Dto.DonDto;
import com.spring.nuqta.donation.Entity.DonEntity;
import com.spring.nuqta.enums.DonStatus;
import com.spring.nuqta.enums.Scope;
import com.spring.nuqta.organization.Dto.OrgDto;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.request.Dto.ReqDto;
import com.spring.nuqta.request.Entity.ReqEntity;
import com.spring.nuqta.usermanagement.Dto.UserDto;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Set;

import static com.spring.nuqta.enums.Scope.ORGANIZATION;
import static com.spring.nuqta.enums.Scope.USER;
import static org.junit.jupiter.api.Assertions.*;

class ReqMapperTest {

    private ReqMapper reqMapper;

    @BeforeEach
    void setUp() {
        reqMapper = Mappers.getMapper(ReqMapper.class);
    }

    @Test
    void testMapReqEntityToReqDto() {
        // Arrange
        ReqEntity reqEntity = new ReqEntity();
        reqEntity.setLocation(new GeometryFactory().createPoint(new Coordinate(10.0, 20.0)));

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setUsername("testUser");
        userEntity.setEmail("test@example.com");
        userEntity.setPhoneNumber("1234567890");
        userEntity.setScope(USER);
        userEntity.setBirthDate(LocalDate.of(1990, 1, 1));
        reqEntity.setUser(userEntity);

        OrgEntity orgEntity = new OrgEntity();
        orgEntity.setId(1L);
        orgEntity.setLicenseNumber("LIC123");
        orgEntity.setScope(Scope.ORGANIZATION);
        orgEntity.setOrgName("Test Org");
        orgEntity.setEmail("org@example.com");
        orgEntity.setPhoneNumber("0987654321");
        orgEntity.setLocation(new GeometryFactory().createPoint(new Coordinate(30.0, 40.0)));
        reqEntity.setOrganization(orgEntity);

        DonEntity donEntity = new DonEntity();
        donEntity.setId(1L);
        donEntity.setDonationDate(LocalDate.now());
        donEntity.setLastDonation(LocalDate.now().minusMonths(6));
        donEntity.setWeight(70.0);
        donEntity.setAmount(500.0);
        donEntity.setBloodType("A+");
        donEntity.setStatus(DonStatus.PENDING);
        donEntity.setPaymentOffered(true);
        donEntity.setLocation(new GeometryFactory().createPoint(new Coordinate(50.0, 60.0)));

        Set<DonEntity> donations = new HashSet<>();
        donations.add(donEntity);
        reqEntity.setDonations(donations);

        // Act
        ReqDto reqDto = reqMapper.map(reqEntity);

        // Assert
        assertNotNull(reqDto);
        assertEquals(10.0, reqDto.getLongitude());
        assertEquals(20.0, reqDto.getLatitude());

        assertNotNull(reqDto.getUser());
        assertEquals(1L, reqDto.getUser().getId());
        assertEquals("testUser", reqDto.getUser().getUsername());
        assertEquals("test@example.com", reqDto.getUser().getEmail());
        assertEquals("1234567890", reqDto.getUser().getPhoneNumber());
        assertEquals(USER, reqDto.getUser().getScope());
        assertEquals(Period.between(LocalDate.of(1990, 1, 1), LocalDate.now()).getYears(), reqDto.getUser().getAge());

        assertNotNull(reqDto.getOrganization());
        assertEquals(1L, reqDto.getOrganization().getId());
        assertEquals("LIC123", reqDto.getOrganization().getLicenseNumber());
        assertEquals(Scope.ORGANIZATION, reqDto.getOrganization().getScope());
        assertEquals("Test Org", reqDto.getOrganization().getOrgName());
        assertEquals("org@example.com", reqDto.getOrganization().getEmail());
        assertEquals("0987654321", reqDto.getOrganization().getPhoneNumber());
        assertEquals(30.0, reqDto.getOrganization().getLongitude());
        assertEquals(40.0, reqDto.getOrganization().getLatitude());

        assertNotNull(reqDto.getDonations());
        assertEquals(1, reqDto.getDonations().size()); // Check the size of the Set

        // Access the first element in the Set
        DonDto donDto = reqDto.getDonations().iterator().next();
        assertEquals(1L, donDto.getId());
        assertEquals(LocalDate.now(), donDto.getDonationDate());
        assertEquals(LocalDate.now().minusMonths(6), donDto.getLastDonation());
        assertEquals(70.0, donDto.getWeight());
        assertEquals(500.0, donDto.getAmount());
        assertEquals("A+", donDto.getBloodType());
        assertEquals(DonStatus.PENDING, donDto.getStatus());
        assertTrue(donDto.getPaymentOffered());
        assertEquals(50.0, donDto.getLongitude());
        assertEquals(60.0, donDto.getLatitude());
    }

    @Test
    void testUnMapReqDtoToReqEntity() {
        // Arrange
        ReqDto reqDto = new ReqDto();
        reqDto.setLongitude(10.0);
        reqDto.setLatitude(20.0);

        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setUsername("testUser");
        userDto.setEmail("test@example.com");
        userDto.setPhoneNumber("1234567890");
        userDto.setScope(USER);
        userDto.setAge(30);
        reqDto.setUser(userDto);

        OrgDto orgDto = new OrgDto();
        orgDto.setId(1L);
        orgDto.setLicenseNumber("LIC123");
        orgDto.setScope(ORGANIZATION);
        orgDto.setOrgName("Test Org");
        orgDto.setEmail("org@example.com");
        orgDto.setPhoneNumber("0987654321");
        orgDto.setLongitude(30.0);
        orgDto.setLatitude(40.0);
        reqDto.setOrganization(orgDto);

        DonDto donDto = new DonDto();
        donDto.setId(1L);
        donDto.setDonationDate(LocalDate.now());
        donDto.setLastDonation(LocalDate.now().minusMonths(6));
        donDto.setWeight(70.0);
        donDto.setAmount(500.0);
        donDto.setBloodType("A+");
        donDto.setStatus(DonStatus.COMPLETED);
        donDto.setPaymentOffered(true);
        donDto.setLongitude(50.0);
        donDto.setLatitude(60.0);

        Set<DonDto> donations = new HashSet<>();
        donations.add(donDto);
        reqDto.setDonations(donations);

        // Act
        ReqEntity reqEntity = reqMapper.unMap(reqDto);

        // Assert
        assertNotNull(reqEntity);
        assertNotNull(reqEntity.getLocation());
        assertEquals(10.0, reqEntity.getLocation().getCoordinate().x);
        assertEquals(20.0, reqEntity.getLocation().getCoordinate().y);

        assertNotNull(reqEntity.getUser());
        assertEquals(1L, reqEntity.getUser().getId());
        assertEquals("testUser", reqEntity.getUser().getUsername());
        assertEquals("test@example.com", reqEntity.getUser().getEmail());
        assertEquals("1234567890", reqEntity.getUser().getPhoneNumber());
        assertEquals(USER, reqEntity.getUser().getScope());
        assertEquals(LocalDate.now().minusYears(30).withDayOfYear(1), reqEntity.getUser().getBirthDate());

        assertNotNull(reqEntity.getOrganization());
        assertEquals(1L, reqEntity.getOrganization().getId());
        assertEquals("LIC123", reqEntity.getOrganization().getLicenseNumber());
        assertEquals(ORGANIZATION, reqEntity.getOrganization().getScope());
        assertEquals("Test Org", reqEntity.getOrganization().getOrgName());
        assertEquals("org@example.com", reqEntity.getOrganization().getEmail());
        assertEquals("0987654321", reqEntity.getOrganization().getPhoneNumber());
        assertEquals(30.0, reqEntity.getOrganization().getLocation().getCoordinate().x);
        assertEquals(40.0, reqEntity.getOrganization().getLocation().getCoordinate().y);

        assertNotNull(reqEntity.getDonations());
        assertEquals(1, reqEntity.getDonations().size()); // Check the size of the Set

        // Access the first element in the Set
        DonEntity donEntity = reqEntity.getDonations().iterator().next();
        assertEquals(1L, donEntity.getId());
        assertEquals(LocalDate.now(), donEntity.getDonationDate());
        assertEquals(LocalDate.now().minusMonths(6), donEntity.getLastDonation());
        assertEquals(70.0, donEntity.getWeight());
        assertEquals(500.0, donEntity.getAmount());
        assertEquals("A+", donEntity.getBloodType());
        assertEquals(DonStatus.COMPLETED, donEntity.getStatus());
        assertTrue(donEntity.getPaymentOffered());
        assertEquals(50.0, donEntity.getLocation().getCoordinate().x);
        assertEquals(60.0, donEntity.getLocation().getCoordinate().y);
    }

    @Test
    void testUnMapReqEntityWithReqDto() {
        // Arrange
        ReqEntity reqEntity = new ReqEntity();
        reqEntity.setLocation(new GeometryFactory().createPoint(new Coordinate(10.0, 20.0)));

        ReqDto reqDto = new ReqDto();
        reqDto.setLongitude(30.0);
        reqDto.setLatitude(40.0);

        // Act
        reqMapper.unMap(reqEntity, reqDto);

        // Assert
        assertNotNull(reqEntity.getLocation());
        assertEquals(30.0, reqEntity.getLocation().getCoordinate().x);
        assertEquals(40.0, reqEntity.getLocation().getCoordinate().y);
    }

    @Test
    void testCreateGeometry() {
        // Arrange
        Double x = 10.0;
        Double y = 20.0;

        // Act
        Geometry geometry = reqMapper.createGeometry(x, y);

        // Assert
        assertNotNull(geometry);
        assertEquals(10.0, geometry.getCoordinate().x);
        assertEquals(20.0, geometry.getCoordinate().y);
    }

    @Test
    void testCreateGeometryWithNullValues() {
        // Arrange
        Double x = null;
        Double y = null;

        // Act
        Geometry geometry = reqMapper.createGeometry(x, y);

        // Assert
        assertNull(geometry);
    }
}