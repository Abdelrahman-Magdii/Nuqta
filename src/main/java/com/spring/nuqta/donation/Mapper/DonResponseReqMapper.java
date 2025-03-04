package com.spring.nuqta.donation.Mapper;

import com.spring.nuqta.base.Mapper.BaseMapper;
import com.spring.nuqta.donation.Dto.DonResponseReqDto;
import com.spring.nuqta.donation.Entity.DonEntity;
import com.spring.nuqta.usermanagement.Mapper.UserResponseToReqMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserResponseToReqMapper.class})
public interface DonResponseReqMapper extends BaseMapper<DonEntity, DonResponseReqDto> {

}