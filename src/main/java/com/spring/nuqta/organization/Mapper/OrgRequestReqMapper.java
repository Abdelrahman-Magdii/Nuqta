package com.spring.nuqta.organization.Mapper;


import com.spring.nuqta.base.Mapper.BaseMapper;
import com.spring.nuqta.organization.Dto.OrgRequestReqDto;
import com.spring.nuqta.organization.Entity.OrgEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface OrgRequestReqMapper extends BaseMapper<OrgEntity, OrgRequestReqDto> {

}
