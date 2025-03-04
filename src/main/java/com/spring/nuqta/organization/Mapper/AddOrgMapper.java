package com.spring.nuqta.organization.Mapper;


import com.spring.nuqta.base.Mapper.BaseMapper;
import com.spring.nuqta.organization.Dto.AddOrgDto;
import com.spring.nuqta.organization.Entity.OrgEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AddOrgMapper extends BaseMapper<OrgEntity, AddOrgDto> {

    @Override
    @Mapping(target = "password", ignore = true)
    AddOrgDto map(OrgEntity entity);

    @Override
    OrgEntity unMap(AddOrgDto dto);
}
