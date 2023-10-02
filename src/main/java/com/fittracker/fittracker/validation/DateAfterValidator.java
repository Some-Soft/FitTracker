package com.fittracker.fittracker.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

public class DateAfterValidator implements ConstraintValidator<DateAfter, LocalDate> {

    private LocalDate startingDate;
    @Override
    public void initialize(DateAfter dateAfter) {
        startingDate = LocalDate.parse(dateAfter.value());
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext context) {
        return localDate == null || localDate.isAfter(startingDate);
    }
}
