package com.fittracker.fittracker.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestControllerAdvice
public class RestExceptionHandler {

    @ExceptionHandler(WeightNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleWeightNotFoundException(WeightNotFoundException e) {
        ErrorResponse errorResponse = ErrorResponse.withMessage(e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleArgumentNotValidException(MethodArgumentNotValidException e) {
        ErrorResponse errorResponse = new ErrorResponse(getErrorFieldName(e),
                getDefaultErrorMessageForException(e));
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        ErrorResponse errorResponse = new ErrorResponse(e.getName(),
                "Please provide valid " + e.getName());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) throws Throwable {
        System.out.println(e.getRootCause());
        ErrorResponse errorResponse = new ErrorResponse("","");
        setErrorFieldAndMessageForRootCause(errorResponse, e.getRootCause());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    private String getDefaultErrorMessageForException(MethodArgumentNotValidException e) {
        if (e.getBindingResult().getFieldError() != null) {
            return e.getBindingResult().getFieldError().getDefaultMessage();
        } else {
            return "";
        }
    }

    private String getErrorFieldName(MethodArgumentNotValidException e) {
        String mess = e.getBindingResult().getFieldError().getField();

        if (e.getBindingResult().getFieldError() != null) {
            return e.getBindingResult().getFieldError().getField();
        } else {
            return "";
        }
    }

    private void setErrorFieldAndMessageForRootCause(ErrorResponse errorResponse, Throwable e) {
        if (e instanceof DateTimeParseException) {
        } else if (e instanceof InvalidFormatException) {
        } else {
        }
    }

    private String extractNameFromPathReference(String pathReference) {
        Pattern pattern = Pattern.compile("\\[\"(\\w+)\"\\]");
        Matcher matcher = pattern.matcher(pathReference);

        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "Field not found";
        }
    }
}
