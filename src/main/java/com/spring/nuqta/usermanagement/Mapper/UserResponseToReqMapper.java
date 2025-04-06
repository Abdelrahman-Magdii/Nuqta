package com.spring.nuqta.usermanagement.Mapper;

import com.spring.nuqta.base.Mapper.BaseMapper;
import com.spring.nuqta.usermanagement.Dto.UserResponseToReqDto;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserResponseToReqMapper extends BaseMapper<UserEntity, UserResponseToReqDto> {

    @Override
    @Mapping(target = "age", expression = "java(com.spring.nuqta.usermanagement.Mapper.DateUtils.calculateAgeFromBirthDate(entity.getBirthDate()))")
    UserResponseToReqDto map(UserEntity entity);

    @Override
    @Mapping(target = "birthDate", expression = "java(com.spring.nuqta.usermanagement.Mapper.DateUtils.calculateBirthDateFromAge(dto.getAge()))")
    UserEntity unMap(UserResponseToReqDto dto);


}
