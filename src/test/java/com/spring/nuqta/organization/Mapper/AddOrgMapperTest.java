package com.spring.nuqta.organization.Mapper;

import com.spring.nuqta.organization.Dto.AddOrgDto;
import com.spring.nuqta.organization.Entity.OrgEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AddOrgMapperTest {

    private AddOrgMapper addOrgMapper; // MapStruct generates this implementation

    @BeforeEach
    void setUp() {
        addOrgMapper = Mappers.getMapper(AddOrgMapper.class);
    }

    @Test
    void testMap_OrgEntityToAddOrgDto() {
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
        AddOrgDto addOrgDto = addOrgMapper.map(orgEntity);

        // Assert
        assertThat(addOrgDto).isNotNull();
        assertThat(addOrgDto.getId()).isEqualTo(1L);
        assertThat(addOrgDto.getOrgName()).isEqualTo("Test Org");
        assertThat(addOrgDto.getEmail()).isEqualTo("test@org.com");
        assertThat(addOrgDto.getLongitude()).isEqualTo(10.0);
        assertThat(addOrgDto.getLatitude()).isEqualTo(20.0);
    }

    @Test
    void testMap_OrgEntityToAddOrgDto_LocationIsNull() {
        // Arrange
        OrgEntity orgEntity = new OrgEntity();
        orgEntity.setId(1L);
        orgEntity.setOrgName("Test Org");
        orgEntity.setLocation(null); // Location is null

        // Act
        AddOrgDto addOrgDto = addOrgMapper.map(orgEntity);

        // Assert
        assertThat(addOrgDto).isNotNull();
        assertThat(addOrgDto.getLongitude()).isNull();
        assertThat(addOrgDto.getLatitude()).isNull();
    }

    @Test
    void testUnMap_AddOrgDtoToOrgEntity() {
        // Arrange
        AddOrgDto addOrgDto = new AddOrgDto();
        addOrgDto.setId(1L);
        addOrgDto.setOrgName("Test Org");
        addOrgDto.setEmail("test@org.com");
        addOrgDto.setLongitude(10.0);
        addOrgDto.setLatitude(20.0);

        // Act
        OrgEntity orgEntity = addOrgMapper.unMap(addOrgDto);

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
    void testUnMap_AddOrgDtoToOrgEntity_LocationIsNull() {
        // Arrange
        AddOrgDto addOrgDto = new AddOrgDto();
        addOrgDto.setId(1L);
        addOrgDto.setOrgName("Test Org");
        addOrgDto.setLongitude(null); // Location is null
        addOrgDto.setLatitude(null);

        // Act
        OrgEntity orgEntity = addOrgMapper.unMap(addOrgDto);

        // Assert
        assertThat(orgEntity).isNotNull();
        assertThat(orgEntity.getLocation()).isNull();
    }

    @Test
    void testUnMap_UpdateOrgEntityWithAddOrgDto() {
        // Arrange
        OrgEntity orgEntity = new OrgEntity();
        orgEntity.setId(1L);
        orgEntity.setOrgName("Old Org Name");
        orgEntity.setEmail("old@org.com");

        AddOrgDto addOrgDto = new AddOrgDto();
        addOrgDto.setId(1L);
        addOrgDto.setOrgName("New Org Name");
        addOrgDto.setEmail("new@org.com");
        addOrgDto.setLongitude(30.0);
        addOrgDto.setLatitude(40.0);

        // Act
        OrgEntity updatedEntity = addOrgMapper.unMap(orgEntity, addOrgDto);

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
    void testCreateGeometry() {
        // Arrange
        Double longitude = 10.0;
        Double latitude = 20.0;

        // Act
        Geometry geometry = addOrgMapper.createGeometry(longitude, latitude);

        // Assert
        assertThat(geometry).isNotNull();
        assertThat(geometry.getCoordinate().x).isEqualTo(10.0);
        assertThat(geometry.getCoordinate().y).isEqualTo(20.0);
    }

    @Test
    void testCreateGeometry_WithNullValues() {
        // Arrange
        Double longitude = null;
        Double latitude = null;

        // Act
        Geometry geometry = addOrgMapper.createGeometry(longitude, latitude);

        // Assert
        assertThat(geometry).isNull();
    }
}