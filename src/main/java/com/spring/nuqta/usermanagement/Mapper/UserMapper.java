package com.spring.nuqta.usermanagement.Mapper;

import com.spring.nuqta.base.Mapper.BaseMapper;
import com.spring.nuqta.donation.Mapper.DonResponseUserMapper;
import com.spring.nuqta.request.Mapper.AddReqMapper;
import com.spring.nuqta.usermanagement.Dto.UserDto;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;

@Mapper(componentModel = "spring", uses = {DonResponseUserMapper.class, AddReqMapper.class})
public interface UserMapper extends BaseMapper<UserEntity, UserDto> {

    @Override
    @Mapping(target = "age", source = "birthDate", qualifiedByName = "calculateAge")
    UserDto map(UserEntity entity);

    @Override
    @Mapping(target = "birthDate", source = "age", qualifiedByName = "calculateBirthDate")
    UserEntity unMap(UserDto dto);

    @Named("calculateAge")
    default int calculateAge(LocalDate birthDate) {
        return DateUtils.calculateAgeFromBirthDate(birthDate);
    }

    @Named("calculateBirthDate")
    default LocalDate calculateBirthDate(int age) {
        return DateUtils.calculateBirthDateFromAge(age);
    }
}
