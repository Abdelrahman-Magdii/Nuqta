package com.spring.nuqta.donation.Mapper;

import com.spring.nuqta.base.Mapper.BaseMapper;
import com.spring.nuqta.donation.Dto.DonDto;
import com.spring.nuqta.donation.Entity.DonEntity;
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

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface DonMapper extends BaseMapper<DonEntity, DonDto> {

    @Override
    @Mapping(target = "longitude", expression = "java(entity.getLocation() != null ? entity.getLocation().getCoordinate().x : null)")
    @Mapping(target = "latitude", expression = "java(entity.getLocation() != null ? entity.getLocation().getCoordinate().y : null)")
    @Mapping(target = "user", expression = "java(mapUserEntityToDto(entity.getUser()))")
    @Mapping(target = "request", source = "request", qualifiedByName = "mapReqEntityToDto")
    DonDto map(DonEntity entity);

    @Override
    @Mapping(target = "location", expression = "java(dto.getLongitude() != null && dto.getLatitude() != null ? createGeometry(dto.getLongitude(), dto.getLatitude()) : null)")
    @Mapping(target = "request", source = "request", qualifiedByName = "mapReqDtoToEntity")
    DonEntity unMap(DonDto dto);

    @Override
    @Mapping(target = "location", expression = "java(dto.getLongitude() != null && dto.getLatitude() != null ? createGeometry(dto.getLongitude(), dto.getLatitude()) : entity.getLocation())")
    DonEntity unMap(@MappingTarget DonEntity entity, DonDto dto);


    default Geometry createGeometry(Double x, Double y) {
        if (x == null || y == null) {
            return null;
        }
        GeometryFactory geometryFactory = new GeometryFactory();
        return geometryFactory.createPoint(new Coordinate(x, y));
    }

    /// **************** Map Users *************************//
    default UserDto mapUserEntityToDto(UserEntity userEntity) {
        if (userEntity == null) {
            return null;
        }
        UserDto userDto = new UserDto();
        userDto.setId(userEntity.getId()); // Map only essential fields to avoid recursion
        userDto.setUsername(userEntity.getUsername());
        userDto.setEmail(userEntity.getEmail());
        userDto.setAge(LocalDate.now().getYear() - userEntity.getBirthDate().getYear());
        userDto.setPhoneNumber(userEntity.getPhoneNumber());
        userDto.setScope(userEntity.getScope());
        return userDto;
    }

    /// **************** Map Req *************************//
    @Named("mapReqEntityToDto")
    default ReqDto mapReqEntityToDto(ReqEntity reqEntity) {
        if (reqEntity == null) {
            return null;
        }
        ReqDto reqDto = new ReqDto();
        reqDto.setId(reqEntity.getId());
        reqDto.setAddress(reqEntity.getAddress());
        reqDto.setLatitude(reqEntity.getLocation().getCoordinate().x);
        reqDto.setLongitude(reqEntity.getLocation().getCoordinate().y);
        reqDto.setStatus(reqEntity.getStatus());
        reqDto.setAmount(reqEntity.getAmount());
        reqDto.setRequestDate(reqEntity.getRequestDate());
        reqDto.setBloodTypeNeeded(reqEntity.getBloodTypeNeeded());
        reqDto.setPaymentAvailable(reqEntity.getPaymentAvailable());
        reqDto.setUrgencyLevel(reqEntity.getUrgencyLevel());
//        reqDto.setDonation(reqEntity.getDonation());
        return reqDto;
    }

    @Named("mapReqDtoToEntity")
    default ReqEntity mapReqDtoToEntity(ReqDto reqDto) {
        if (reqDto == null) {
            return null;
        }
        ReqEntity req = new ReqEntity();
        req.setId(reqDto.getId());
        req.setAddress(reqDto.getAddress());
        req.setLocation(new GeometryFactory().createPoint(new Coordinate(reqDto.getLongitude(), reqDto.getLatitude())));
        req.setStatus(reqDto.getStatus());
        req.setAmount(reqDto.getAmount());
        req.setRequestDate(reqDto.getRequestDate());
        req.setBloodTypeNeeded(reqDto.getBloodTypeNeeded());
        req.setPaymentAvailable(reqDto.getPaymentAvailable());
        req.setUrgencyLevel(reqDto.getUrgencyLevel());
        return req;
    }


}
