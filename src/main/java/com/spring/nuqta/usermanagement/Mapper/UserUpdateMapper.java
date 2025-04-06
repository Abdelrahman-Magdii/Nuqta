package com.spring.nuqta.usermanagement.Mapper;

import com.spring.nuqta.base.Mapper.BaseMapper;
import com.spring.nuqta.donation.Mapper.DonResponseUserUpdateMapper;
import com.spring.nuqta.usermanagement.Dto.UserUpdateDto;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {DonResponseUserUpdateMapper.class})
public interface UserUpdateMapper extends BaseMapper<UserEntity, UserUpdateDto> {

    @Override
    @Mapping(target = "age", expression = "java(com.spring.nuqta.usermanagement.Mapper.DateUtils.calculateAgeFromBirthDate(entity.getBirthDate()))")
    UserUpdateDto map(UserEntity entity);

    @Override
    @Mapping(target = "birthDate", expression = "java(com.spring.nuqta.usermanagement.Mapper.DateUtils.calculateBirthDateFromAge(dto.getAge()))")
    UserEntity unMap(UserUpdateDto dto);
}
