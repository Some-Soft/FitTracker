package com.somesoft.fittracker.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.somesoft.fittracker.dataprovider.Entity;
import com.somesoft.fittracker.dataprovider.Request;
import org.junit.jupiter.api.Test;

class WeightRequestTest {

    @Test
    void toWeight_givenWeightRequest_shouldReturnWeight() {
        WeightRequest weightRequest = Request.weightRequest();
        var expectedResult = Entity.weight();

        var result = weightRequest.toWeight();

        assertThat(result).usingRecursiveComparison().ignoringFields("userId").isEqualTo(expectedResult);
    }
}