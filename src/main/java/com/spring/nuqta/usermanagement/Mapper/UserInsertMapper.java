package com.spring.nuqta.usermanagement.Mapper;

import com.spring.nuqta.base.Mapper.BaseMapper;
import com.spring.nuqta.donation.Dto.DonDto;
import com.spring.nuqta.donation.Entity.DonEntity;
import com.spring.nuqta.usermanagement.Dto.UserInsertDto;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface UserInsertMapper extends BaseMapper<UserEntity, UserInsertDto> {

    @Override
    @Mapping(target = "donation", source = "donation", qualifiedByName = "mapDonationToDto")
    UserInsertDto map(UserEntity entity);

    @Override
    @Mapping(target = "donation", source = "donation", qualifiedByName = "mapDonationToEntity")
    UserEntity unMap(UserInsertDto dto);


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
        donationDto.setPaymentOffered(donation.getPaymentOffered());
        donationDto.setBloodType(donation.getBloodType());
        donationDto.setWeight(donation.getWeight());
        donationDto.setLatitude(donation.getLocation().getCoordinate().getX());
        donationDto.setLongitude(donation.getLocation().getCoordinate().getY());
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
        donation.setPaymentOffered(donationDto.getPaymentOffered());
        donation.setBloodType(donationDto.getBloodType());
        donation.setWeight(donationDto.getWeight());
        donation.setLocation(new GeometryFactory().createPoint(new Coordinate(donationDto.getLatitude(), donationDto.getLongitude())));
        donation.setCreatedDate(LocalDate.now());
        donation.setModifiedDate(LocalDate.now());
        return donation;
    }

}
