package com.fittracker.fittracker.response;

import static com.fittracker.fittracker.dataprovider.Entity.userWithPassword;
import static com.fittracker.fittracker.dataprovider.Response.registerResponse;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class RegisterResponseTest {

    @Test
    void givenUser_shouldReturnRegisterResponse() {
        var expected = registerResponse();
        var result = RegisterResponse.fromUser(userWithPassword("password"));

        assertThat(result).isEqualTo(expected);
    }
}