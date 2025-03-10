package com.spring.nuqta.usermanagement.Mapper;

import com.spring.nuqta.base.Mapper.BaseMapper;
import com.spring.nuqta.usermanagement.Dto.UserResponseToReqDto;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;

@Mapper(componentModel = "spring")
public interface UserResponseToReqMapper extends BaseMapper<UserEntity, UserResponseToReqDto> {

    @Override
    @Mapping(target = "age", source = "birthDate", qualifiedByName = "calculateAge")
    UserResponseToReqDto map(UserEntity entity);

    @Override
    @Mapping(target = "birthDate", source = "age", qualifiedByName = "calculateBirthDate")
    UserEntity unMap(UserResponseToReqDto dto);
    
    @Named("calculateAge")
    default int calculateAge(LocalDate birthDate) {
        return DateUtils.calculateAgeFromBirthDate(birthDate);
    }

    @Named("calculateBirthDate")
    default LocalDate calculateBirthDate(int age) {
        return DateUtils.calculateBirthDateFromAge(age);
    }


}
