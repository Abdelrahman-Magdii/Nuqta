package com.spring.nuqta.donation.Mapper;

import com.spring.nuqta.base.Mapper.BaseMapper;
import com.spring.nuqta.donation.Dto.DonResponseUserDto;
import com.spring.nuqta.donation.Entity.DonEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DonResponseUserUpdateMapper extends BaseMapper<DonEntity, DonResponseUserDto> {

}