package com.fittracker.fittracker.controller;

import org.mockito.verification.VerificationMode;
import org.springframework.http.HttpStatus;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.OK;

public enum GetRequestTestData {
    DATE_NULL_PARAM("", BAD_REQUEST, "{\"field\":\"date\",\"message\":\"Parameter must not be null\"}", never()),
    DATE_BAD_FORMAT("?date=123", BAD_REQUEST, "{\"field\":\"date\",\"message\":\"Invalid data type\"}", never()),
    DATE_CORRECT_FORMAT_BEFORE_2023("?date=2020-01-01",OK,"",times(1)),
    DATE_CORRECT_FORMAT_IN_THE_FUTURE("?date=2050-10-10",OK,"",times(1));
    private final String params;
    private final HttpStatus responseStatus;
    private final String responseBody;
    private final VerificationMode numberOfServiceInvocations;

    GetRequestTestData(String params, HttpStatus responseStatus, String responseBody, VerificationMode numberOfServiceInvocations) {
        this.params = params;
        this.responseStatus = responseStatus;
        this.responseBody = responseBody;
        this.numberOfServiceInvocations = numberOfServiceInvocations;
    }

    // Getters for the enum fields
    public String getParams() {
        return params;
    }

    public HttpStatus getResponseStatus() {
        return responseStatus;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public VerificationMode getNumberOfServiceInvocations() {
        return numberOfServiceInvocations;
    }

}
