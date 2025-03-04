package com.spring.nuqta.request.Mapper;

import com.spring.nuqta.base.Mapper.BaseMapper;
import com.spring.nuqta.request.Dto.AddReqDto;
import com.spring.nuqta.request.Entity.ReqEntity;
import org.mapstruct.Mapper;


@Mapper(componentModel = "spring")
public interface AddReqMapper extends BaseMapper<ReqEntity, AddReqDto> {

}