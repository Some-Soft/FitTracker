package com.somesoft.fittracker.exception;

import static java.lang.String.format;

public class ProductAlreadyExistsException extends FitTrackerException {

    private static final String MESSAGE_TEMPLATE = "Product with name: %s, already exists";

    public ProductAlreadyExistsException(String productName) {
        super(format(MESSAGE_TEMPLATE, productName));
    }
}
