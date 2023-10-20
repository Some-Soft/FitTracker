package com.fittracker.fittracker.request;

import com.fittracker.fittracker.entity.Weight;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;


@ExtendWith(MockitoExtension.class)
class WeightRequestTest {

    @InjectMocks
    WeightRequest weightRequest;

    @Test
    void givenWeightRequest_shouldReturnWeight() {
        Double testValue = 55.6;
        LocalDate testDate = LocalDate.of(2023,9,11);
        WeightRequest weightRequest = new WeightRequest(testDate, testValue);
        var expectedWeight = new Weight(testDate, testValue);
        var result = weightRequest.toWeight();
        assertThat(result).usingRecursiveComparison().isEqualTo(expectedWeight);
    }
}