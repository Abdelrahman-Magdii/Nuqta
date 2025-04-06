package com.spring.nuqta.usermanagement.Mapper;

import com.spring.nuqta.base.Mapper.BaseMapper;
import com.spring.nuqta.usermanagement.Dto.UserResponseToDonDto;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserResponseMapper extends BaseMapper<UserEntity, UserResponseToDonDto> {

    @Override
    @Mapping(target = "age", expression = "java(com.spring.nuqta.usermanagement.Mapper.DateUtils.calculateAgeFromBirthDate(entity.getBirthDate()))")
    UserResponseToDonDto map(UserEntity entity);

    @Override
    @Mapping(target = "birthDate", expression = "java(com.spring.nuqta.usermanagement.Mapper.DateUtils.calculateBirthDateFromAge(dto.getAge()))")
    UserEntity unMap(UserResponseToDonDto dto);

}
