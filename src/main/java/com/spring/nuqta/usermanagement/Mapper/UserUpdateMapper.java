package com.spring.nuqta.usermanagement.Mapper;

import com.spring.nuqta.base.Mapper.BaseMapper;
import com.spring.nuqta.donation.Mapper.DonResponseUserUpdateMapper;
import com.spring.nuqta.usermanagement.Dto.UserUpdateDto;
import com.spring.nuqta.usermanagement.Entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDate;

@Mapper(componentModel = "spring", uses = {DonResponseUserUpdateMapper.class})
public interface UserUpdateMapper extends BaseMapper<UserEntity, UserUpdateDto> {

    @Override
    @Mapping(target = "age", source = "birthDate")
    UserUpdateDto map(UserEntity entity);

    @Override
    @Mapping(target = "birthDate", source = "age")
    UserEntity unMap(UserUpdateDto dto);

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
