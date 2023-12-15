package com.somesoft.fittracker.exception;

public class InvalidDateRangeException extends FitTrackerException {

    private static final String INVALID_DATE_RANGE_MESSAGE = "Start date cannot be after end date";

    public InvalidDateRangeException() {
        super(INVALID_DATE_RANGE_MESSAGE);
    }
}
