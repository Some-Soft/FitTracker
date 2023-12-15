package com.somesoft.fittracker.exception;

import static java.lang.String.format;

import java.util.UUID;

public class ProductNotFoundException extends FitTrackerException {

    private static final String MESSAGE_TEMPLATE = "Product not found for id: %s";

    public ProductNotFoundException(UUID id) {
        super(format(MESSAGE_TEMPLATE, id));
    }
}
