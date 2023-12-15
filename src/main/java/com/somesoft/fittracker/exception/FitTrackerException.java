package com.somesoft.fittracker.exception;

abstract class FitTrackerException extends RuntimeException {

    protected FitTrackerException(String message) {
        super(message);
    }

}
