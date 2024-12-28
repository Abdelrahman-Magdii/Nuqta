package com.spring.nuqta.organization.Mapper;


import com.spring.nuqta.base.Mapper.BaseMapper;
import com.spring.nuqta.organization.Dto.AddOrgDto;
import com.spring.nuqta.organization.Entity.OrgEntity;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AddOrgMapper extends BaseMapper<OrgEntity, AddOrgDto> {


    @Override
    @Mapping(target = "longitude", expression = "java(entity.getLocation() != null ? entity.getLocation().getCoordinate().x : null)")
    @Mapping(target = "latitude", expression = "java(entity.getLocation() != null ? entity.getLocation().getCoordinate().y : null)")
    AddOrgDto map(OrgEntity entity);

    @Override
    @Mapping(target = "location", expression = "java(dto.getLongitude() != null && dto.getLatitude() != null ? createGeometry(dto.getLongitude(), dto.getLatitude()) : null)")
    OrgEntity unMap(AddOrgDto dto);

    @Override
    @Mapping(target = "location", expression = "java(dto.getLongitude() != null && dto.getLatitude() != null ? createGeometry(dto.getLongitude(), dto.getLatitude()) : entity.getLocation())")
    OrgEntity unMap(@MappingTarget OrgEntity entity, AddOrgDto dto);


    default Geometry createGeometry(Double x, Double y) {
        if (x == null || y == null) {
            return null;
        }
        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPoint(new Coordinate(x, y));
    }

}
