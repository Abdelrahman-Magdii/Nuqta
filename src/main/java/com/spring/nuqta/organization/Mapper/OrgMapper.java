package com.spring.nuqta.organization.Mapper;


import com.spring.nuqta.base.Mapper.BaseMapper;
import com.spring.nuqta.organization.Dto.OrgDto;
import com.spring.nuqta.organization.Entity.OrgEntity;
import com.spring.nuqta.request.Mapper.AddReqMapper;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {AddReqMapper.class})
public interface OrgMapper extends BaseMapper<OrgEntity, OrgDto> {
    
}