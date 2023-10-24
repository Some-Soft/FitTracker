package com.fittracker.fittracker.exception;

import com.fittracker.fittracker.request.RegisterRequest;

import static java.lang.String.format;

public class UserAlreadyExistsException extends RuntimeException {
    private static final String MESSAGE_TEMPLATE = "User already exists for username/email provided: %s/%s";
    public UserAlreadyExistsException(RegisterRequest registerRequest) {
        super(format(MESSAGE_TEMPLATE, registerRequest.username(), registerRequest.password()));
    }
}
