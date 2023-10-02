package com.fittracker.fittracker.exception;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(String field, String message) {
    public static ErrorResponse withMessage(String message) {
        return new ErrorResponse(null, message);
    }
}
