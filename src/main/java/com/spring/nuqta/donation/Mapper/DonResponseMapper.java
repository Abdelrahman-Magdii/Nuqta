package com.spring.nuqta.donation.Mapper;

import com.spring.nuqta.base.Mapper.BaseMapper;
import com.spring.nuqta.donation.Dto.DonResponseDto;
import com.spring.nuqta.donation.Entity.DonEntity;
import com.spring.nuqta.request.Mapper.AddReqMapper;
import com.spring.nuqta.usermanagement.Mapper.UserResponseMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserResponseMapper.class, AddReqMapper.class})
public interface DonResponseMapper extends BaseMapper<DonEntity, DonResponseDto> {

}