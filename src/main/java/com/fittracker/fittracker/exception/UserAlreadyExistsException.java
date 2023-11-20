package com.fittracker.fittracker.exception;

import static java.lang.String.format;

import com.fittracker.fittracker.request.RegisterRequest;

public class UserAlreadyExistsException extends FitTrackerException {

    private static final String MESSAGE_TEMPLATE = "User already exists for username/email provided: %s/%s";

    public UserAlreadyExistsException(RegisterRequest registerRequest) {
        super(format(MESSAGE_TEMPLATE, registerRequest.username(), registerRequest.email()));
    }
}
