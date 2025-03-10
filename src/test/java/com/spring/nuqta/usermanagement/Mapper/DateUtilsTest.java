package com.spring.nuqta.usermanagement.Mapper;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DateUtilsTest {

    @Test
    void testCalculateAgeFromBirthDate() {
        // Test with a past date where birthday has passed this year
        LocalDate birthDate1 = LocalDate.of(2000, 1, 10);
        assertEquals(LocalDate.now().getYear() - 2000, DateUtils.calculateAgeFromBirthDate(birthDate1));

        // Test with a birthdate later in the year (birthday not yet occurred this year)
        LocalDate birthDate2 = LocalDate.of(2000, 12, 31);
        assertEquals(LocalDate.now().getYear() - 2000 - 1, DateUtils.calculateAgeFromBirthDate(birthDate2));

        // Test with today's date
        LocalDate today = LocalDate.now();
        assertEquals(0, DateUtils.calculateAgeFromBirthDate(today));

        // Test with a null birthDate
        assertEquals(0, DateUtils.calculateAgeFromBirthDate(null));
    }

    @Test
    void testCalculateBirthDateFromAge() {
        // Test with valid age
        int age = 25;
        LocalDate expectedBirthDate = LocalDate.now().minusYears(age).withDayOfYear(1);
        assertEquals(expectedBirthDate, DateUtils.calculateBirthDateFromAge(age));

        // Test with age 0 (newborn)
        assertEquals(LocalDate.now().withDayOfYear(1), DateUtils.calculateBirthDateFromAge(0));

        // Test with negative age (should throw exception)
        Exception exception = assertThrows(IllegalArgumentException.class, () -> DateUtils.calculateBirthDateFromAge(-1));
        assertEquals("error.user.age", exception.getMessage());
    }
}
