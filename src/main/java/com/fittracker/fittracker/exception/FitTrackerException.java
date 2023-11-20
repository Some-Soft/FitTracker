package com.fittracker.fittracker.exception;

abstract class FitTrackerException extends RuntimeException {

    protected FitTrackerException(String message) {
        super(message);
    }

}
