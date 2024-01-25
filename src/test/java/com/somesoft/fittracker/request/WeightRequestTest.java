package com.somesoft.fittracker.request;

import static com.somesoft.fittracker.dataprovider.Entity.weight;
import static com.somesoft.fittracker.dataprovider.Request.weightRequest;
import static com.somesoft.fittracker.dataprovider.TestHelper.assertEqualRecursiveIgnoring;

import org.junit.jupiter.api.Test;

class WeightRequestTest {

    @Test
    void toWeight_givenWeightRequest_shouldReturnWeight() {

        WeightRequest weightRequest = weightRequest();
        var expectedResult = weight();

        var result = weightRequest.toWeight();

        assertEqualRecursiveIgnoring(result, expectedResult, "userId");
    }
}