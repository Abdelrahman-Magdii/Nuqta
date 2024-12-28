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

@Mapper(componentModel = "spring")
public interface UserInsertMapper extends BaseMapper<UserEntity, UserInsertDto> {

    @Override
    @Mapping(target = "donation", source = "donation", qualifiedByName = "mapDonationToDto")
    UserInsertDto map(UserEntity userEntity);

    @Override
    @Mapping(target = "donation", source = "donation", qualifiedByName = "mapDonationToEntity")
    UserEntity unMap(UserInsertDto userDto);

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
        donationDto.setWeight(donation.getWeight());
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
        donation.setWeight(donationDto.getWeight());
        return donation;
    }

}
