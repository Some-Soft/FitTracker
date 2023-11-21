package com.fittracker.fittracker.response;

import static com.fittracker.fittracker.dataprovider.Entity.weight;
import static com.fittracker.fittracker.dataprovider.Response.weightResponse;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class WeightResponseTest {

    @Test
    void givenWeight_shouldReturnWeightResponse() {
        var expectedWeightResponse = weightResponse();
        var actualWeightResponse = WeightResponse.fromWeight(weight());

        assertThat(actualWeightResponse).isEqualTo(expectedWeightResponse);
    }
}