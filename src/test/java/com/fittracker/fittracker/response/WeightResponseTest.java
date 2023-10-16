package com.fittracker.fittracker.response;

import com.fittracker.fittracker.entity.Weight;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class WeightResponseTest {

    private final LocalDate TEST_DATE = LocalDate.of(2023,9,11);
    @Test
    void givenWeight_shouldReturnWeightResponse() {
        Double TEST_VALUE = 55.6;
        Weight weight = new Weight(TEST_DATE, TEST_VALUE);
        var expectedWeightResponse = new WeightResponse(TEST_DATE, TEST_VALUE);
        var actualWeightResponse = WeightResponse.fromWeight(weight);
        assertThat(actualWeightResponse).usingRecursiveComparison().isEqualTo(expectedWeightResponse);
    }
}