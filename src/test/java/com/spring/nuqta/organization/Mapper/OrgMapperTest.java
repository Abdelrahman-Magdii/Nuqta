package com.spring.nuqta.organization.Mapper;

import com.spring.nuqta.organization.Dto.OrgDto;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.request.Dto.ReqDto;
import com.spring.nuqta.request.Entity.ReqEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import static com.spring.nuqta.enums.Status.FULFILLED;
import static com.spring.nuqta.enums.Status.OPEN;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class OrgMapperTest {

    private OrgMapper orgMapper; // MapStruct generates this implementation

    @BeforeEach
    void setUp() {
        orgMapper = Mappers.getMapper(OrgMapper.class);
    }

    @Test
    void testMap_OrgEntityToOrgDto() {
        // Arrange
        OrgEntity orgEntity = new OrgEntity();
        orgEntity.setId(1L);
        orgEntity.setOrgName("Test Org");
        orgEntity.setEmail("test@org.com");

        // Set location using GeometryFactory
        GeometryFactory geometryFactory = new GeometryFactory();
        Point location = geometryFactory.createPoint(new Coordinate(10.0, 20.0));
        orgEntity.setLocation(location);

        // Act
        OrgDto orgDto = orgMapper.map(orgEntity);

        // Assert
        assertThat(orgDto).isNotNull();
        assertThat(orgDto.getId()).isEqualTo(1L);
        assertThat(orgDto.getOrgName()).isEqualTo("Test Org");
        assertThat(orgDto.getEmail()).isEqualTo("test@org.com");
        assertThat(orgDto.getLongitude()).isEqualTo(10.0);
        assertThat(orgDto.getLatitude()).isEqualTo(20.0);
    }

    @Test
    void testMap_OrgEntityToOrgDto_LocationIsNull() {
        // Arrange
        OrgEntity orgEntity = new OrgEntity();
        orgEntity.setId(1L);
        orgEntity.setOrgName("Test Org");
        orgEntity.setLocation(null); // Location is null

        // Act
        OrgDto orgDto = orgMapper.map(orgEntity);

        // Assert
        assertThat(orgDto).isNotNull();
        assertThat(orgDto.getLongitude()).isNull();
        assertThat(orgDto.getLatitude()).isNull();
    }

    @Test
    void testUnMap_OrgDtoToOrgEntity() {
        // Arrange
        OrgDto orgDto = new OrgDto();
        orgDto.setId(1L);
        orgDto.setOrgName("Test Org");
        orgDto.setEmail("test@org.com");
        orgDto.setLongitude(10.0);
        orgDto.setLatitude(20.0);

        // Act
        OrgEntity orgEntity = orgMapper.unMap(orgDto);

        // Assert
        assertThat(orgEntity).isNotNull();
        assertThat(orgEntity.getId()).isEqualTo(1L);
        assertThat(orgEntity.getOrgName()).isEqualTo("Test Org");
        assertThat(orgEntity.getEmail()).isEqualTo("test@org.com");
        assertThat(orgEntity.getLocation()).isNotNull();
        assertThat(orgEntity.getLocation().getCoordinate().x).isEqualTo(10.0);
        assertThat(orgEntity.getLocation().getCoordinate().y).isEqualTo(20.0);
    }

    @Test
    void testUnMap_OrgDtoToOrgEntity_LocationIsNull() {
        // Arrange
        OrgDto orgDto = new OrgDto();
        orgDto.setId(1L);
        orgDto.setOrgName("Test Org");
        orgDto.setLongitude(null); // Location is null
        orgDto.setLatitude(null);

        // Act
        OrgEntity orgEntity = orgMapper.unMap(orgDto);

        // Assert
        assertThat(orgEntity).isNotNull();
        assertThat(orgEntity.getLocation()).isNull();
    }

    @Test
    void testUnMap_UpdateOrgEntityWithOrgDto() {
        // Arrange
        OrgEntity orgEntity = new OrgEntity();
        orgEntity.setId(1L);
        orgEntity.setOrgName("Old Org Name");
        orgEntity.setEmail("old@org.com");

        OrgDto orgDto = new OrgDto();
        orgDto.setId(1L);
        orgDto.setOrgName("New Org Name");
        orgDto.setEmail("new@org.com");
        orgDto.setLongitude(30.0);
        orgDto.setLatitude(40.0);

        // Act
        OrgEntity updatedEntity = orgMapper.unMap(orgEntity, orgDto);

        // Assert
        assertThat(updatedEntity).isNotNull();
        assertThat(updatedEntity.getId()).isEqualTo(1L); // ID should not change
        assertThat(updatedEntity.getOrgName()).isEqualTo("New Org Name");
        assertThat(updatedEntity.getEmail()).isEqualTo("new@org.com");
        assertThat(updatedEntity.getLocation()).isNotNull();
        assertThat(updatedEntity.getLocation().getCoordinate().x).isEqualTo(30.0);
        assertThat(updatedEntity.getLocation().getCoordinate().y).isEqualTo(40.0);
    }

    @Test
    void testMapRequestSetToDtoList() {
        // Arrange
        ReqEntity reqEntity = new ReqEntity();
        reqEntity.setId(1L);
        reqEntity.setAddress("Test Address");
        reqEntity.setStatus(FULFILLED);

        Set<ReqEntity> requestSet = Set.of(reqEntity);

        // Act
        List<ReqDto> reqDtoList = orgMapper.mapRequestSetToDtoList(requestSet);

        // Assert
        assertThat(reqDtoList).isNotNull().hasSize(1);
        assertThat(reqDtoList.get(0).getId()).isEqualTo(1L);
        assertThat(reqDtoList.get(0).getAddress()).isEqualTo("Test Address");
        assertThat(reqDtoList.get(0).getStatus()).isEqualTo(FULFILLED);
    }

    @Test
    void testMapRequestSetToDtoList_EmptySet() {
        // Arrange
        Set<ReqEntity> requestSet = Collections.emptySet();

        // Act
        List<ReqDto> reqDtoList = orgMapper.mapRequestSetToDtoList(requestSet);

        // Assert
        assertThat(reqDtoList).isNotNull().isEmpty();
    }

    @Test
    void testMapRequestDtoListToEntitySet() {
        // Arrange
        ReqDto reqDto = new ReqDto();
        reqDto.setId(1L);
        reqDto.setAddress("Test Address");
        reqDto.setStatus(OPEN);

        List<ReqDto> requestDtoList = List.of(reqDto);

        // Act
        Set<ReqEntity> reqEntitySet = orgMapper.mapRequestDtoListToEntitySet(requestDtoList);

        // Assert
        assertThat(reqEntitySet).isNotNull().hasSize(1);
        assertThat(reqEntitySet.iterator().next().getId()).isEqualTo(1L);
        assertThat(reqEntitySet.iterator().next().getAddress()).isEqualTo("Test Address");
        assertThat(reqEntitySet.iterator().next().getStatus()).isEqualTo(OPEN);
    }

    @Test
    void testMapRequestDtoListToEntitySet_EmptyList() {
        // Arrange
        List<ReqDto> requestDtoList = Collections.emptyList();

        // Act
        Set<ReqEntity> reqEntitySet = orgMapper.mapRequestDtoListToEntitySet(requestDtoList);

        // Assert
        assertThat(reqEntitySet).isNotNull().isEmpty();
    }
}