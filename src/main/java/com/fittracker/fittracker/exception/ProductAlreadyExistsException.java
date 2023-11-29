package com.fittracker.fittracker.exception;

import static java.lang.String.format;

public class ProductAlreadyExistsException extends FitTrackerException {

    private static final String MESSAGE_TEMPLATE = "Product: %s already exists";

    public ProductAlreadyExistsException(String productName) {
        super(format(MESSAGE_TEMPLATE, productName));
    }
}
