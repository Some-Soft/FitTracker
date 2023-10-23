package com.fittracker.fittracker.controller;

import org.mockito.verification.VerificationMode;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;

public enum PostRequestTestData {
    CORRECT_REQUEST_AFTER_2023(CREATED, "{\"date\":\"2023-08-03\",\"value\":13.2}", "", times(1)),
    CORRECT_REQUEST_TODAY(CREATED, "{\"date\":\"" + LocalDate.now() + "\",\"value\":13.2}", "", times(1)),
    DATE_BAD_FORMAT(BAD_REQUEST, "{\"date\":\"202-08-03\",\"value\":13.2}", "{\"field\":\"date\",\"message\":\"Date must be in format: YYYY-MM-DD\"}", never()),
    DATE_INVALID_FORMAT(BAD_REQUEST, "{\"date\":\"03-08-2023\",\"value\":13.2}", "{\"field\":\"date\",\"message\":\"Date must be in format: YYYY-MM-DD\"}", never()),
    DATE_NULL(BAD_REQUEST, "{\"value\":13.2}", "{\"field\":\"date\",\"message\":\"Date must not be null\"}", never()),
    DATE_FUTURE(BAD_REQUEST, "{\"date\":\"" + LocalDate.of(3000, 1, 1) + "\",\"value\":13.2}", "{\"field\":\"date\",\"message\":\"Date cannot be in the future\"}", never()),
    DATE_BEFORE_2023(BAD_REQUEST, "{\"date\":\"2021-01-02\",\"value\":13.2}", "{\"field\":\"date\",\"message\":\"Date must be after 2022\"}", never()),
    DATE_END_OF_2022(BAD_REQUEST, "{\"date\":\"2022-12-31\",\"value\":13.2}", "{\"field\":\"date\",\"message\":\"Date must be after 2022\"}", never()),
    VALUE_NULL(BAD_REQUEST, "{\"date\":\"2023-08-03\"}", "{\"field\":\"value\",\"message\":\"Value must not be null\"}", never()),
    VALUE_NEGATIVE(BAD_REQUEST, "{\"date\":\"2023-08-03\",\"value\":-1}", "{\"field\":\"value\",\"message\":\"Value must be positive\"}", never()),
    VALUE_EXCEEDING_UPPER_LIMIT(BAD_REQUEST, "{\"date\":\"2023-08-03\",\"value\":700}", "{\"field\":\"value\",\"message\":\"Value must be less than 635\"}", never());

    private final HttpStatus responseStatus;
    private final String requestBody;
    private final String responseBody;
    private final VerificationMode numberOfServiceInvocations;

    PostRequestTestData(HttpStatus responseStatus, String requestBody, String responseBody, VerificationMode numberOfServiceInvocations) {
        this.responseStatus = responseStatus;
        this.requestBody = requestBody;
        this.responseBody = responseBody;
        this.numberOfServiceInvocations = numberOfServiceInvocations;
    }

    public HttpStatus getResponseStatus() {
        return responseStatus;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public VerificationMode getNumberOfServiceInvocations() {
        return numberOfServiceInvocations;
    }
}