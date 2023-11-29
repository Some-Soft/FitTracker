package com.fittracker.fittracker.exception;

public class ProductAlreadyExistsException extends FitTrackerException {

    private static final String MESSAGE_TEMPLATE = "Updated product must differ";

    public ProductAlreadyExistsException() {
        super(MESSAGE_TEMPLATE);
    }
}
