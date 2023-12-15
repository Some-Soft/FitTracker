package com.somesoft.fittracker.response;

import static com.somesoft.fittracker.dataprovider.Entity.weight;
import static com.somesoft.fittracker.dataprovider.Response.weightResponse;
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