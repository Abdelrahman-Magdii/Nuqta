package com.spring.nuqta.request.Mapper;

import com.spring.nuqta.base.Mapper.BaseMapper;
import com.spring.nuqta.organization.Dto.OrgDto;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.request.Dto.ReqDto;
import com.spring.nuqta.request.Entity.ReqEntity;
import com.spring.nuqta.usermanagement.Dto.UserDto;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;


@Mapper(componentModel = "spring")
public interface ReqMapper extends BaseMapper<ReqEntity, ReqDto> {


    @Override
    @Mapping(target = "longitude", expression = "java(entity.getLocation() != null ? entity.getLocation().getCoordinate().x : null)")
    @Mapping(target = "latitude", expression = "java(entity.getLocation() != null ? entity.getLocation().getCoordinate().y : null)")
    @Mapping(target = "user", source = "user", qualifiedByName = "mapUserEntityToDto")
    @Mapping(target = "organization", source = "organization", qualifiedByName = "mapOrganizationEntityToDto")
    ReqDto map(ReqEntity entity);

    @Override
    @Mapping(target = "location", expression = "java(dto.getLongitude() != null && dto.getLatitude() != null ? createGeometry(dto.getLongitude(), dto.getLatitude()) : null)")
    @Mapping(target = "user", source = "user", qualifiedByName = "mapUserDtoToEntity")
    @Mapping(target = "organization", source = "organization", qualifiedByName = "mapOrgDtoToEntity")
    ReqEntity unMap(ReqDto dto);

    @Override
    @Mapping(target = "location", expression = "java(dto.getLongitude() != null && dto.getLatitude() != null ? createGeometry(dto.getLongitude(), dto.getLatitude()) : entity.getLocation())")
    ReqEntity unMap(@MappingTarget ReqEntity entity, ReqDto dto);


    default Geometry createGeometry(Double x, Double y) {
        if (x == null || y == null) {
            return null;
        }
        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPoint(new Coordinate(x, y));
    }


    /// ******************************************//
    // Custom method to map UserEntity to UserDto
    @Named("mapUserEntityToDto")
    default UserDto mapUserEntityToDto(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        UserDto dto = new UserDto();
        dto.setId(userEntity.getId());
        dto.setUsername(userEntity.getUsername());
        dto.setEmail(userEntity.getEmail());
        dto.setPhone_number(userEntity.getPhone_number());
        dto.setScope(userEntity.getScope());
        dto.setAge(userEntity.getAge());


        return dto;
    }

    // Custom method to map UserDto to UserEntity
    @Named("mapUserDtoToEntity")
    default UserEntity mapUserDtoToEntity(UserDto userDto) {
        if (userDto == null) {
            return null;
        }
        UserEntity entity = new UserEntity();
        entity.setId(userDto.getId());
        entity.setUsername(userDto.getUsername());
        entity.setEmail(userDto.getEmail());
        entity.setPhone_number(userDto.getPhone_number());
        entity.setScope(userDto.getScope());
        entity.setAge(userDto.getAge());

        return entity;
    }

    /// *****************************************//
    @Named("mapOrganizationEntityToDto")
    default OrgDto mapOrganizationEntityToDto(OrgEntity orgEntity) {
        if (orgEntity == null) {
            return null;
        }
        OrgDto dto = new OrgDto();
        dto.setId(orgEntity.getId());
        dto.setLicense_number(orgEntity.getLicense_number());
        dto.setScope(orgEntity.getScope());
        dto.setOrg_name(orgEntity.getOrg_name());
        dto.setEmail(orgEntity.getEmail());
        dto.setPhone_number(orgEntity.getPhone_number());
        dto.setLongitude(orgEntity.getLocation().getCoordinate().x);
        dto.setLatitude(orgEntity.getLocation().getCoordinate().y);
        return dto;
    }

    @Named("mapOrgDtoToEntity")
    default OrgEntity mapOrgDtoToEntity(OrgDto orgDto) {
        if (orgDto == null) {
            return null;
        }
        OrgEntity entity = new OrgEntity();
        entity.setId(orgDto.getId());
        entity.setLicense_number(orgDto.getLicense_number());
        entity.setScope(orgDto.getScope());
        entity.setOrg_name(orgDto.getOrg_name());
        entity.setEmail(orgDto.getEmail());
        entity.setPhone_number(orgDto.getPhone_number());
        entity.setLocation(new GeometryFactory().createPoint(new Coordinate(orgDto.getLongitude(), orgDto.getLatitude())));

        return entity;
    }

}