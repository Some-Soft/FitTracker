package com.fittracker.fittracker.exception;

import static java.lang.String.format;

import java.time.LocalDate;

public class WeightAlreadyExistsException extends FitTrackerException {

    private static final String MESSAGE_TEMPLATE = "Weight already exists for date: %s";

    public WeightAlreadyExistsException(LocalDate date) {
        super(format(MESSAGE_TEMPLATE, date));
    }
}
