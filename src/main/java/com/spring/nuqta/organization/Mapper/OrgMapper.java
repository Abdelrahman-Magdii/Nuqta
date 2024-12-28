package com.spring.nuqta.organization.Mapper;


import com.spring.nuqta.base.Mapper.BaseMapper;
import com.spring.nuqta.organization.Dto.OrgDto;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.request.Dto.ReqDto;
import com.spring.nuqta.request.Entity.ReqEntity;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface OrgMapper extends BaseMapper<OrgEntity, OrgDto> {
    
    @Override
    @Mapping(target = "longitude", expression = "java(entity.getLocation() != null ? entity.getLocation().getCoordinate().x : null)")
    @Mapping(target = "latitude", expression = "java(entity.getLocation() != null ? entity.getLocation().getCoordinate().y : null)")
    @Mapping(target = "requests", source = "requests", qualifiedByName = "mapRequestSetToDtoList")
    OrgDto map(OrgEntity entity);

    @Override
    @Mapping(target = "location", expression = "java(dto.getLongitude() != null && dto.getLatitude() != null ? createGeometry(dto.getLongitude(), dto.getLatitude()) : null)")
    @Mapping(target = "requests", source = "requests", qualifiedByName = "mapRequestDtoListToEntitySet")
    OrgEntity unMap(OrgDto dto);

    @Override
    @Mapping(target = "location", expression = "java(dto.getLongitude() != null && dto.getLatitude() != null ? createGeometry(dto.getLongitude(), dto.getLatitude()) : entity.getLocation())")
    OrgEntity unMap(@MappingTarget OrgEntity entity, OrgDto dto);


    default Geometry createGeometry(Double x, Double y) {
        if (x == null || y == null) {
            return null;
        }
        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPoint(new Coordinate(x, y));
    }

    /// ************ Map Requests ************ //

    // Custom method to map Set<ReqEntity> to List<ReqDto>
    @Named("mapRequestSetToDtoList")
    default List<ReqDto> mapRequestSetToDtoList(Set<ReqEntity> requestSet) {
        if (requestSet == null || requestSet.isEmpty()) {
            return Collections.emptyList();
        }
        return requestSet.stream()
                .map(req -> {
                    ReqDto dto = new ReqDto();
                    dto.setId(req.getId());
                    dto.setAddress(req.getAddress());
                    dto.setStatus(req.getStatus());
                    dto.setRequestDate(req.getRequestDate());
                    dto.setPaymentAvailable(req.getPaymentAvailable());
                    dto.setUrgencyLevel(req.getUrgencyLevel());
                    dto.setBloodTypeNeeded(req.getBloodTypeNeeded());
                    dto.setLongitude(req.getLocation() != null ? req.getLocation().getCoordinate().x : null);
                    dto.setLatitude(req.getLocation() != null ? req.getLocation().getCoordinate().y : null);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Custom method to map List<ReqDto> to Set<ReqEntity>
    @Named("mapRequestDtoListToEntitySet")
    default Set<ReqEntity> mapRequestDtoListToEntitySet(List<ReqDto> requestDtoList) {
        if (requestDtoList == null || requestDtoList.isEmpty()) {
            return Collections.emptySet();
        }
        return requestDtoList.stream()
                .map(dto -> {
                    ReqEntity entity = new ReqEntity();
                    entity.setId(dto.getId());
                    entity.setAddress(dto.getAddress());
                    entity.setStatus(dto.getStatus());
                    entity.setRequestDate(dto.getRequestDate());
                    entity.setPaymentAvailable(dto.getPaymentAvailable());
                    entity.setUrgencyLevel(dto.getUrgencyLevel());
                    entity.setBloodTypeNeeded(dto.getBloodTypeNeeded());

                    entity.setLocation(dto.getLongitude() != null && dto.getLatitude() != null
                            ? new GeometryFactory().createPoint(new Coordinate(dto.getLongitude(), dto.getLatitude()))
                            : null);
                    return entity;
                })
                .collect(Collectors.toSet());
    }


}
