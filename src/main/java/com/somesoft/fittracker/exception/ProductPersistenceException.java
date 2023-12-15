package com.somesoft.fittracker.exception;

import static java.lang.String.format;

import com.somesoft.fittracker.entity.Product;

public class ProductPersistenceException extends RuntimeException {

    private static final String MESSAGE_TEMPLATE = "Failed to persist product: %s";

    public ProductPersistenceException(Product product) {
        super(format(MESSAGE_TEMPLATE, product.toString()));
    }
}
