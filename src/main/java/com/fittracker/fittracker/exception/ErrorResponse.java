package com.fittracker.fittracker.exception;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(NON_NULL)
public record ErrorResponse(String field, String message) {

    public static ErrorResponse withMessage(String message) {
        return new ErrorResponse(null, message);
    }
}
