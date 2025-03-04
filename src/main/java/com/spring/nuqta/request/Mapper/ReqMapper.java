package com.spring.nuqta.request.Mapper;

import com.spring.nuqta.base.Mapper.BaseMapper;
import com.spring.nuqta.donation.Mapper.DonResponseUserUpdateMapper;
import com.spring.nuqta.organization.Mapper.OrgRequestReqMapper;
import com.spring.nuqta.request.Dto.ReqDto;
import com.spring.nuqta.request.Entity.ReqEntity;
import com.spring.nuqta.usermanagement.Mapper.UserResponseToReqMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {UserResponseToReqMapper.class, OrgRequestReqMapper.class, DonResponseUserUpdateMapper.class})
public interface ReqMapper extends BaseMapper<ReqEntity, ReqDto> {

}