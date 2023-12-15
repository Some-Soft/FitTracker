package com.somesoft.fittracker.entity;

import static com.somesoft.fittracker.dataprovider.Entity.user;
import static com.somesoft.fittracker.dataprovider.Entity.userDetails;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class UserDetailsTest {

    @Test
    void fromUser_givenUser_shouldReturnUserDetails() {
        User user = user();

        var expected = userDetails();
        var result = UserDetails.fromUser(user);

        assertThat(result).isEqualTo(expected);
    }
}