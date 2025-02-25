package com.spring.nuqta.request.Mapper;

import com.spring.nuqta.request.Dto.AddReqDto;
import com.spring.nuqta.request.Entity.ReqEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AddReqMapperTest {

    private AddReqMapper addReqMapper;
    private GeometryFactory geometryFactory;

    @BeforeEach
    void setUp() {
        addReqMapper = Mappers.getMapper(AddReqMapper.class);
        geometryFactory = new GeometryFactory();
    }

    @Test
    void testMap_ShouldMapEntityToDto() {
        // Arrange
        Geometry location = geometryFactory.createPoint(new Coordinate(12.34, 56.78));
        ReqEntity entity = new ReqEntity();
        entity.setLocation(location);

        // Act
        AddReqDto dto = addReqMapper.map(entity);

        // Assert
        assertNotNull(dto);
        assertEquals(12.34, dto.getLongitude());
        assertEquals(56.78, dto.getLatitude());
    }

    @Test
    void testMap_ShouldHandleNullLocation() {
        // Arrange
        ReqEntity entity = new ReqEntity();
        entity.setLocation(null);

        // Act
        AddReqDto dto = addReqMapper.map(entity);

        // Assert
        assertNotNull(dto);
        assertNull(dto.getLongitude());
        assertNull(dto.getLatitude());
    }

    @Test
    void testUnMap_ShouldMapDtoToEntity() {
        // Arrange
        AddReqDto dto = new AddReqDto();
        dto.setLongitude(12.34);
        dto.setLatitude(56.78);

        // Act
        ReqEntity entity = addReqMapper.unMap(dto);

        // Assert
        assertNotNull(entity);
        assertNotNull(entity.getLocation());
        assertEquals(12.34, entity.getLocation().getCoordinate().x);
        assertEquals(56.78, entity.getLocation().getCoordinate().y);
    }

    @Test
    void testUnMap_ShouldHandleNullCoordinates() {
        // Arrange
        AddReqDto dto = new AddReqDto();
        dto.setLongitude(null);
        dto.setLatitude(null);

        // Act
        ReqEntity entity = addReqMapper.unMap(dto);

        // Assert
        assertNotNull(entity);
        assertNull(entity.getLocation());
    }

    @Test
    void testUnMap_UpdateExistingEntity() {
        // Arrange
        Geometry initialLocation = geometryFactory.createPoint(new Coordinate(1.1, 2.2));
        ReqEntity entity = new ReqEntity();
        entity.setLocation(initialLocation);

        AddReqDto dto = new AddReqDto();
        dto.setLongitude(12.34);
        dto.setLatitude(56.78);

        // Act
        ReqEntity updatedEntity = addReqMapper.unMap(entity, dto);

        // Assert
        assertNotNull(updatedEntity);
        assertNotNull(updatedEntity.getLocation());
        assertEquals(12.34, updatedEntity.getLocation().getCoordinate().x);
        assertEquals(56.78, updatedEntity.getLocation().getCoordinate().y);
    }

    @Test
    void testCreateGeometry_ShouldCreateValidPoint() {
        // Act
        Geometry point = addReqMapper.createGeometry(12.34, 56.78);

        // Assert
        assertNotNull(point);
        assertEquals(12.34, point.getCoordinate().x);
        assertEquals(56.78, point.getCoordinate().y);
    }

    @Test
    void testCreateGeometry_ShouldHandleNullValues() {
        // Act
        Geometry point = addReqMapper.createGeometry(null, null);

        // Assert
        assertNull(point);
    }
}
