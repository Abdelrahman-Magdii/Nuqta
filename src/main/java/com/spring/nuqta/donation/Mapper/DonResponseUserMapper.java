package com.spring.nuqta.donation.Mapper;

import com.spring.nuqta.base.Mapper.BaseMapper;
import com.spring.nuqta.donation.Dto.DonResponseUserDto;
import com.spring.nuqta.donation.Entity.DonEntity;
import com.spring.nuqta.request.Mapper.AddReqMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {AddReqMapper.class})
public interface DonResponseUserMapper extends BaseMapper<DonEntity, DonResponseUserDto> {

}