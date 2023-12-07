package com.fittracker.fittracker.exception;

public class ProductNotUpdatedException extends FitTrackerException {

    private static final String MESSAGE_TEMPLATE = "Updated product must differ";

    public ProductNotUpdatedException() {
        super(MESSAGE_TEMPLATE);
    }
}
