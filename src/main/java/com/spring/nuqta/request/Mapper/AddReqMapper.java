package com.spring.nuqta.request.Mapper;

import com.spring.nuqta.base.Mapper.BaseMapper;
import com.spring.nuqta.request.Dto.AddReqDto;
import com.spring.nuqta.request.Entity.ReqEntity;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;


@Mapper(componentModel = "spring")
public interface AddReqMapper extends BaseMapper<ReqEntity, AddReqDto> {


    @Override
    @Mapping(target = "longitude", expression = "java(entity.getLocation() != null ? entity.getLocation().getCoordinate().x : null)")
    @Mapping(target = "latitude", expression = "java(entity.getLocation() != null ? entity.getLocation().getCoordinate().y : null)")
    AddReqDto map(ReqEntity entity);

    @Override
    @Mapping(target = "location", expression = "java(dto.getLongitude() != null && dto.getLatitude() != null ? createGeometry(dto.getLongitude(), dto.getLatitude()) : null)")
    ReqEntity unMap(AddReqDto dto);

    @Override
    @Mapping(target = "location", expression = "java(dto.getLongitude() != null && dto.getLatitude() != null ? createGeometry(dto.getLongitude(), dto.getLatitude()) : entity.getLocation())")
    ReqEntity unMap(@MappingTarget ReqEntity entity, AddReqDto dto);


    default Geometry createGeometry(Double x, Double y) {
        if (x == null || y == null) {
            return null;
        }
        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPoint(new Coordinate(x, y));
    }

}