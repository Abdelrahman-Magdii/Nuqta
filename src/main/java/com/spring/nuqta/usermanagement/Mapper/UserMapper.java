package com.spring.nuqta.usermanagement.Mapper;

import com.spring.nuqta.base.Mapper.BaseMapper;
import com.spring.nuqta.donation.Mapper.DonResponseUserMapper;
import com.spring.nuqta.request.Mapper.AddReqMapper;
import com.spring.nuqta.usermanagement.Dto.UserDto;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {DonResponseUserMapper.class, AddReqMapper.class})
public interface UserMapper extends BaseMapper<UserEntity, UserDto> {

    @Override
    @Mapping(target = "age", expression = "java(com.spring.nuqta.usermanagement.Mapper.DateUtils.calculateAgeFromBirthDate(entity.getBirthDate()))")
    UserDto map(UserEntity entity);

    @Override
    @Mapping(target = "birthDate", expression = "java(com.spring.nuqta.usermanagement.Mapper.DateUtils.calculateBirthDateFromAge(dto.getAge()))")
    UserEntity unMap(UserDto dto);

}
