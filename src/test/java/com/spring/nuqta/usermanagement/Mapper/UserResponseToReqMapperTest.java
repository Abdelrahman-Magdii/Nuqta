package com.spring.nuqta.usermanagement.Mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserResponseToReqMapperTest {

    private UserResponseToReqMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = Mappers.getMapper(UserResponseToReqMapper.class);
    }

    @Test
    void calculateAgeFromBirthDate_ShouldReturnCorrectAge() {
        LocalDate today = LocalDate.now();

        // Case 1: Birthday already passed this year
        LocalDate birthDate1 = today.minusYears(22).minusDays(5);
        assertEquals(22, mapper.calculateAgeFromBirthDate(birthDate1));

        // Case 2: Birthday is today
        LocalDate birthDate2 = today.minusYears(30);
        assertEquals(30, mapper.calculateAgeFromBirthDate(birthDate2));

        // Case 3: Birthday has not occurred yet this year
        LocalDate birthDate3 = today.minusYears(25).plusDays(5);
        assertEquals(24, mapper.calculateAgeFromBirthDate(birthDate3));

        // Case 4: Leap year check (born on Feb 29)
        LocalDate leapYearBirthDate = LocalDate.of(2000, 2, 29); // 2000 was a leap year
        LocalDate adjustedBirthDate = leapYearBirthDate.withYear(today.getYear());

        // If the adjusted year is NOT a leap year, use Feb 28 instead
        if (!adjustedBirthDate.isLeapYear()) {
            adjustedBirthDate = LocalDate.of(today.getYear(), 2, 28);
        }

        int expectedLeapAge = today.getYear() - 2000;
        if (adjustedBirthDate.isAfter(today)) {
            expectedLeapAge--;
        }

        assertEquals(expectedLeapAge, mapper.calculateAgeFromBirthDate(leapYearBirthDate));
    }

    @Test
    void calculateAgeFromBirthDate_ShouldReturnZeroIfNull() {
        assertEquals(0, mapper.calculateAgeFromBirthDate(null));
    }

    @Test
    void calculateBirthDateFromAge_ShouldReturnCorrectBirthDate() {
        int age = 25;
        LocalDate expectedBirthDate = LocalDate.now().minusYears(age).withDayOfYear(1);

        assertEquals(expectedBirthDate, mapper.calculateBirthDateFromAge(age));
    }

    @Test
    void calculateBirthDateFromAge_ShouldThrowExceptionIfNegative() {
        assertThrows(IllegalArgumentException.class, () -> mapper.calculateBirthDateFromAge(-5));
    }

}