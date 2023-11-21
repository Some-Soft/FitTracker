package com.fittracker.fittracker.request;

import static com.fittracker.fittracker.dataprovider.Entity.weight;
import static com.fittracker.fittracker.dataprovider.Request.weightRequest;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class WeightRequestTest {

    @Test
    void toWeight_givenWeightRequest_shouldReturnWeight() {
        WeightRequest weightRequest = weightRequest();
        var expectedResult = weight();

        var result = weightRequest.toWeight();

        assertThat(result).usingRecursiveComparison().ignoringFields("userId").isEqualTo(expectedResult);

    }
}