package com.somesoft.fittracker.exception;

import static java.lang.String.format;

import java.time.LocalDate;

public class WeightNotFoundException extends FitTrackerException {

    private static final String MESSAGE_TEMPLATE = "Weight not found for date: %s";

    public WeightNotFoundException(LocalDate date) {
        super(format(MESSAGE_TEMPLATE, date));
    }
}
