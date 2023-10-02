package com.fittracker.fittracker.exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
class RestExceptionHandler {

    private final ErrorResponseMapper errorResponseMapper;

    @Autowired
    RestExceptionHandler(ErrorResponseMapper errorResponseMapper) {
        this.errorResponseMapper = errorResponseMapper;
    }

    @ExceptionHandler(WeightNotFoundException.class)
    ResponseEntity<ErrorResponse> handleException(WeightNotFoundException e) {
        return new ResponseEntity<>(errorResponseMapper.map(e), NOT_FOUND);
    }

    @ExceptionHandler(WeightAlreadyExistsException.class)
    ResponseEntity<ErrorResponse> handleException(WeightAlreadyExistsException e) {
        return new ResponseEntity<>(errorResponseMapper.map(e), BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException e) {
        return new ResponseEntity<>(errorResponseMapper.map(e), BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<ErrorResponse> handleException(MethodArgumentTypeMismatchException e) {
        return new ResponseEntity<>(errorResponseMapper.map(e), BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<ErrorResponse> handleException(HttpMessageNotReadableException e) {
        return new ResponseEntity<>(errorResponseMapper.map(e), BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    ResponseEntity<ErrorResponse> handleException(MissingServletRequestParameterException e) {
        return new ResponseEntity<>(errorResponseMapper.map(e), BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> handleException(Exception e) {
        return new ResponseEntity<>(errorResponseMapper.map(e), INTERNAL_SERVER_ERROR);
    }
}
