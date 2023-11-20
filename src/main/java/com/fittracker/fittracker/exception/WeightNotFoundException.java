package com.fittracker.fittracker.exception;

import java.time.LocalDate;

import static java.lang.String.format;

public class WeightNotFoundException extends FitTrackerException {
    private static final String MESSAGE_TEMPLATE = "Weight not found for date: %s";
    public WeightNotFoundException(LocalDate date) {
        super(format(MESSAGE_TEMPLATE, date));
    }
}
