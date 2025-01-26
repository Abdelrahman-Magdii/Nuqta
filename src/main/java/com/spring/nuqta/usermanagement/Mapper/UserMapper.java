package com.spring.nuqta.usermanagement.Mapper;

import com.spring.nuqta.base.Mapper.BaseMapper;
import com.spring.nuqta.donation.Dto.DonDto;
import com.spring.nuqta.donation.Entity.DonEntity;
import com.spring.nuqta.request.Dto.ReqDto;
import com.spring.nuqta.request.Entity.ReqEntity;
import com.spring.nuqta.usermanagement.Dto.UserDto;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.time.Period;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface UserMapper extends BaseMapper<UserEntity, UserDto> {

    @Override
    @Mapping(target = "age", source = "birthDate", qualifiedByName = "calculateAgeFromBirthDate")
    @Mapping(target = "donation", source = "donation", qualifiedByName = "mapDonationToDto")
    @Mapping(target = "requests", source = "requests", qualifiedByName = "mapRequestSetToDtoList")
    UserDto map(UserEntity userEntity);

    @Override
    @Mapping(target = "birthDate", source = "age", qualifiedByName = "calculateBirthDateFromAge")
    @Mapping(target = "donation", source = "donation", qualifiedByName = "mapDonationToEntity")
    @Mapping(target = "requests", source = "requests", qualifiedByName = "mapRequestDtoListToEntitySet")
    UserEntity unMap(UserDto userDto);

    // Custom method to map DonEntity to DonDto
    @Named("mapDonationToDto")
    default DonDto mapDonationToDto(DonEntity donation) {
        if (donation == null) {
            return null;
        }
        DonDto donationDto = new DonDto();
        donationDto.setId(donation.getId());
        donationDto.setAmount(donation.getAmount());
        donationDto.setDonationDate(donation.getDonationDate());
        donationDto.setLastDonation(donation.getLastDonation());
        donationDto.setStatus(donation.getStatus());
        donationDto.setLatitude(donation.getLocation().getCoordinate().x);
        donationDto.setLongitude(donation.getLocation().getCoordinate().y);
        donationDto.setPaymentOffered(donation.getPaymentOffered());
        donationDto.setBloodType(donation.getBloodType());
        return donationDto;
    }

    // Custom method to map DonDto to DonEntity
    @Named("mapDonationToEntity")
    default DonEntity mapDonationToEntity(DonDto donationDto) {
        if (donationDto == null) {
            return null;
        }
        DonEntity donation = new DonEntity();
        donation.setId(donationDto.getId());
        donation.setAmount(donationDto.getAmount());
        donation.setDonationDate(donationDto.getDonationDate());
        donation.setLastDonation(donationDto.getLastDonation());
        donation.setStatus(donationDto.getStatus());
        donation.setLocation(new GeometryFactory().createPoint(new Coordinate(donationDto.getLongitude(), donationDto.getLatitude())));
        donation.setPaymentOffered(donationDto.getPaymentOffered());
        donation.setBloodType(donationDto.getBloodType());

        return donation;
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


    // Custom method to calculate age from birthDate
    @Named("calculateAgeFromBirthDate")
    default int calculateAgeFromBirthDate(LocalDate birthDate) {
        if (birthDate == null) {
            return 0;
        }
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    // Custom method to calculate birthDate from age
    @Named("calculateBirthDateFromAge")
    default LocalDate calculateBirthDateFromAge(int age) {
        if (age < 0) {
            throw new IllegalArgumentException("Age cannot be negative");
        }
        return LocalDate.now().minusYears(age).withDayOfYear(1); // Set to January 1st of the calculated year
    }

}
