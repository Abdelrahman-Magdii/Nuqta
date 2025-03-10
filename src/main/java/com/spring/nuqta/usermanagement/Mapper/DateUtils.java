package com.spring.nuqta.usermanagement.Mapper;

import java.time.LocalDate;

public class DateUtils {

    public static int calculateAgeFromBirthDate(LocalDate birthDate) {
        if (birthDate == null) return 0;

        LocalDate today = LocalDate.now();
        int age = today.getYear() - birthDate.getYear();

        // Adjust if the birthdate hasn't occurred yet in the current year
        if (birthDate.getMonthValue() > today.getMonthValue() ||
                (birthDate.getMonthValue() == today.getMonthValue() && birthDate.getDayOfMonth() > today.getDayOfMonth())) {
            age--;
        }

        return age;
    }

    public static LocalDate calculateBirthDateFromAge(int age) {
        if (age < 0) throw new IllegalArgumentException("error.user.age");
        return LocalDate.now().minusYears(age).withDayOfYear(1);
    }
}
