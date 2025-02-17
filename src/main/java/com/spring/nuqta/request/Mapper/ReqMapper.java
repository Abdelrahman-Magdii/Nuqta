package com.spring.nuqta.request.Mapper;

import com.spring.nuqta.base.Mapper.BaseMapper;
import com.spring.nuqta.donation.Dto.DonDto;
import com.spring.nuqta.donation.Entity.DonEntity;
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

import java.time.LocalDate;
import java.time.Period;


@Mapper(componentModel = "spring")
public interface ReqMapper extends BaseMapper<ReqEntity, ReqDto> {


    @Override
    @Mapping(target = "longitude", expression = "java(entity.getLocation() != null ? entity.getLocation().getCoordinate().x : null)")
    @Mapping(target = "latitude", expression = "java(entity.getLocation() != null ? entity.getLocation().getCoordinate().y : null)")
    @Mapping(target = "user", source = "user", qualifiedByName = "mapUserEntityToDto")
    @Mapping(target = "organization", source = "organization", qualifiedByName = "mapOrganizationEntityToDto")
    @Mapping(target = "donation", source = "donation", qualifiedByName = "mapDonationEntityToDto")
    ReqDto map(ReqEntity entity);

    @Override
    @Mapping(target = "location", expression = "java(dto.getLongitude() != null && dto.getLatitude() != null ? createGeometry(dto.getLongitude(), dto.getLatitude()) : null)")
    @Mapping(target = "user", source = "user", qualifiedByName = "mapUserDtoToEntity")
    @Mapping(target = "organization", source = "organization", qualifiedByName = "mapOrgDtoToEntity")
    @Mapping(target = "donation", source = "donation", qualifiedByName = "mapDonationDtoToEntity")
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
        dto.setPhoneNumber(userEntity.getPhoneNumber());
        dto.setScope(userEntity.getScope());
        dto.setAge(Period.between(userEntity.getBirthDate(), LocalDate.now()).getYears());
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
        entity.setPhoneNumber(userDto.getPhoneNumber());
        entity.setScope(userDto.getScope());

        // Calculate birthDate from age
        if (userDto.getAge() != null) {
            LocalDate birthDate = LocalDate.now().minusYears(userDto.getAge()).withDayOfYear(1); // Set to January 1st
            entity.setBirthDate(birthDate);
        }

//        // Map nested objects (if needed)
//        if (userDto.getDonation() != null) {
//            entity.setDonation(mapDonationDtoToEntity(userDto.getDonation()));
//        }

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
        dto.setLicenseNumber(orgEntity.getLicenseNumber());
        dto.setScope(orgEntity.getScope());
        dto.setOrgName(orgEntity.getOrgName());
        dto.setEmail(orgEntity.getEmail());
        dto.setPhoneNumber(orgEntity.getPhoneNumber());
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
        entity.setLicenseNumber(orgDto.getLicenseNumber());
        entity.setScope(orgDto.getScope());
        entity.setOrgName(orgDto.getOrgName());
        entity.setEmail(orgDto.getEmail());
        entity.setPhoneNumber(orgDto.getPhoneNumber());
        entity.setLocation(new GeometryFactory().createPoint(new Coordinate(orgDto.getLongitude(), orgDto.getLatitude())));

        return entity;
    }

    /// *****************************************//
    @Named("mapDonationEntityToDto")
    default DonDto mapDonationEntityToDto(DonEntity donEntity) {
        if (donEntity == null) {
            return null;
        }

        DonDto dto = new DonDto();
        dto.setId(donEntity.getId());
        dto.setDonationDate(donEntity.getDonationDate());
        dto.setLastDonation(donEntity.getLastDonation());
        dto.setWeight(donEntity.getWeight());
        dto.setAmount(donEntity.getAmount());
        dto.setBloodType(donEntity.getBloodType());
        dto.setStatus(donEntity.getStatus());
        dto.setPaymentOffered(donEntity.getPaymentOffered());
        dto.setLongitude(donEntity.getLocation().getCoordinate().x);
        dto.setLatitude(donEntity.getLocation().getCoordinate().y);
        return dto;
    }

    @Named("mapDonationDtoToEntity")
    default DonEntity mapDonationDtoToEntity(DonDto donDto) {
        if (donDto == null) {
            return null;
        }
        DonEntity entity = new DonEntity();
        entity.setId(donDto.getId());
        entity.setDonationDate(donDto.getDonationDate());
        entity.setLastDonation(donDto.getLastDonation());
        entity.setWeight(donDto.getWeight());
        entity.setAmount(donDto.getAmount());
        entity.setBloodType(donDto.getBloodType());
        entity.setStatus(donDto.getStatus());
        entity.setPaymentOffered(donDto.getPaymentOffered());
        entity.setLocation(new GeometryFactory().createPoint(new Coordinate(donDto.getLongitude(), donDto.getLatitude())));
        return entity;
    }

}