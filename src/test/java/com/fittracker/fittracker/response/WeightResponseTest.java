package com.fittracker.fittracker.response;

import com.fittracker.fittracker.entity.Weight;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class WeightResponseTest {

    @Test
    void givenWeight_shouldReturnWeightResponse() {
        Double testValue = 55.6;
        LocalDate testDate = LocalDate.of(2023,9,11);
        Weight weight = new Weight(testDate, testValue, UUID.fromString("948cc727-68e5-455c-ab6d-942e585bde0d"));
        var expectedWeightResponse = new WeightResponse(testDate, testValue);

        var actualWeightResponse = WeightResponse.fromWeight(weight);

        assertThat(actualWeightResponse).isEqualTo(expectedWeightResponse);
    }
}