package com.fittracker.fittracker.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.format.DateTimeParseException;

import static java.util.Optional.ofNullable;

@Component
class ErrorResponseMapper {

    private static final String DATE_FIELD_NAME = "date";
    private static final String INVALID_DATA_TYPE_MESSAGE = "Invalid data type";
    private static final String INVALID_DATE_FORMAT_MESSAGE = "Date must be in format: YYYY-MM-DD";
    private static final String INVALID_REQUEST_MESSAGE = "Invalid request";
    private static final String NULL_PARAMETER_MESSAGE = "Parameter must not be null";
    private static final String UNKNOWN_ERROR_MESSAGE = "Unknown error";

    ErrorResponse map(WeightNotFoundException e) {
        return ErrorResponse.withMessage(e.getMessage());
    }

    ErrorResponse map(WeightAlreadyExistsException e) {
        return ErrorResponse.withMessage(e.getMessage());
    }

    ErrorResponse map(MethodArgumentNotValidException e) {
        return ofNullable(e.getBindingResult().getFieldError())
                .map(fieldError -> new ErrorResponse(fieldError.getField(), fieldError.getDefaultMessage()))
                .orElse(ErrorResponse.withMessage(INVALID_REQUEST_MESSAGE));
    }

    ErrorResponse map(MethodArgumentTypeMismatchException e) {
        return new ErrorResponse(e.getName(), INVALID_DATA_TYPE_MESSAGE);
    }

    ErrorResponse map(HttpMessageNotReadableException e) {
        return ofNullable(e.getRootCause())
                .map(this::mapThrowableToErrorResponse)
                .orElse(ErrorResponse.withMessage(INVALID_REQUEST_MESSAGE));
    }

    ErrorResponse map(MissingServletRequestParameterException e) {
        return new ErrorResponse(e.getParameterName(), NULL_PARAMETER_MESSAGE);
    }

    ErrorResponse map(Exception e) {
        return ErrorResponse.withMessage(UNKNOWN_ERROR_MESSAGE);
    }

    private ErrorResponse mapThrowableToErrorResponse(Throwable throwable) {
        return switch (throwable) {
            case DateTimeParseException ignored -> new ErrorResponse(DATE_FIELD_NAME, INVALID_DATE_FORMAT_MESSAGE);
            case InvalidFormatException invalidFormatException ->
                    new ErrorResponse(invalidFormatException.getPath().getFirst().getFieldName(), INVALID_DATA_TYPE_MESSAGE);
            default -> ErrorResponse.withMessage(INVALID_REQUEST_MESSAGE);
        };
    }
}
