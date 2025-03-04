package com.spring.nuqta.usermanagement.Mapper;

import com.spring.nuqta.base.Mapper.BaseMapper;
import com.spring.nuqta.donation.Mapper.DonResponseUserMapper;
import com.spring.nuqta.request.Mapper.AddReqMapper;
import com.spring.nuqta.usermanagement.Dto.UserDto;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;

@Mapper(componentModel = "spring", uses = {DonResponseUserMapper.class, AddReqMapper.class})
public interface UserMapper extends BaseMapper<UserEntity, UserDto> {

    @Override
    @Mapping(target = "age", source = "birthDate")
    UserDto map(UserEntity entity);

    @Override
    @Mapping(target = "birthDate", source = "age")
    UserEntity unMap(UserDto dto);

    default int calculateAgeFromBirthDate(LocalDate birthDate) {
        if (birthDate == null) return 0;

        LocalDate today = LocalDate.now();
        int age = today.getYear() - birthDate.getYear();

        // Adjust if the birth date hasn't occurred yet in the current year
        if (birthDate.getMonthValue() > today.getMonthValue() ||
                (birthDate.getMonthValue() == today.getMonthValue() && birthDate.getDayOfMonth() > today.getDayOfMonth())) {
            age--;
        }

        return age;
    }


    default LocalDate calculateBirthDateFromAge(int age) {
        if (age < 0) throw new IllegalArgumentException("Age cannot be negative");
        return LocalDate.now().minusYears(age).withDayOfYear(1); // Approximate to the first day of the year
    }
}
