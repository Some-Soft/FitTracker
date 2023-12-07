package com.fittracker.fittracker.exception;

import static java.lang.String.format;

import com.fittracker.fittracker.entity.Product;

public class ProductPersistenceException extends RuntimeException {

    private static final String MESSAGE_TEMPLATE = "Failed to persist product: %s";

    public ProductPersistenceException(Product product) {
        super(format(MESSAGE_TEMPLATE, product.toString()));
    }
}
