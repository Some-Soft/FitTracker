package com.fittracker.fittracker.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
class RestExceptionHandler {

    private final ErrorResponseMapper errorResponseMapper;

    private final Logger logger = LoggerFactory.getLogger(RestExceptionHandler.class);

    @Autowired
    RestExceptionHandler(ErrorResponseMapper errorResponseMapper) {
        this.errorResponseMapper = errorResponseMapper;
    }

    @ExceptionHandler(WeightNotFoundException.class)
    ResponseEntity<ErrorResponse> handleException(WeightNotFoundException e) {
        return new ResponseEntity<>(errorResponseMapper.map(e), NOT_FOUND);
    }

    @ExceptionHandler(ProductNotFoundException.class)
    ResponseEntity<ErrorResponse> handleException(ProductNotFoundException e) {
        return new ResponseEntity<>(errorResponseMapper.map(e), NOT_FOUND);
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

    @ExceptionHandler(BadCredentialsException.class)
    ResponseEntity<ErrorResponse> handleException(BadCredentialsException e) {
        return new ResponseEntity<>(errorResponseMapper.map(e), UNAUTHORIZED);
    }

    @ExceptionHandler(FitTrackerException.class)
    ResponseEntity<ErrorResponse> handleException(FitTrackerException e) {
        return new ResponseEntity<>(errorResponseMapper.map(e), BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    ResponseEntity<ErrorResponse> handleException(Exception e) {
        logger.error(e.getMessage(), e);
        return new ResponseEntity<>(errorResponseMapper.map(e), INTERNAL_SERVER_ERROR);
    }

}
