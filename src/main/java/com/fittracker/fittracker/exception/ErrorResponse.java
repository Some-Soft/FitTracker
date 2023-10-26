package com.fittracker.fittracker.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@JsonInclude(NON_NULL)
public record ErrorResponse(String field, String message) {
    static ErrorResponse withMessage(String message) {
        return new ErrorResponse(null, message);
    }
}
