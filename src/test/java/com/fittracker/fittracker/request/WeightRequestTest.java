package com.fittracker.fittracker.request;

import static org.assertj.core.api.Assertions.assertThat;

import com.fittracker.fittracker.entity.Weight;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class WeightRequestTest {

    @Test
    void toWeight_givenWeightRequest_shouldReturnWeight() {
        Double testValue = 55.6;
        LocalDate testDate = LocalDate.of(2023, 9, 11);
        WeightRequest weightRequest = new WeightRequest(testDate, testValue);
        var expectedResult = new Weight(testDate, testValue);

        var result = weightRequest.toWeight();

        assertThat(result).usingRecursiveComparison().isEqualTo(expectedResult);

    }
}