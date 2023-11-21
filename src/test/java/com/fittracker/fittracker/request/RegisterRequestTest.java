package com.fittracker.fittracker.request;

import static com.fittracker.fittracker.dataprovider.Entity.user;
import static com.fittracker.fittracker.dataprovider.Request.registerRequest;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RegisterRequestTest {

    @Test
    void givenRegisterRequest_toUserShouldReturnUser() {
        RegisterRequest request = registerRequest();

        var expected = user();
        var result = request.toUser();

        assertThat(result).usingRecursiveComparison().ignoringFields("id", "password").isEqualTo(expected);
    }
}