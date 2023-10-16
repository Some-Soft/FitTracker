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
    private final LocalDate TEST_DATE = LocalDate.of(2023,9,11);
    @Test
    void givenWeightRequest_shouldReturnWeight() {
        Double TEST_VALUE = 55.6;
        WeightRequest weightRequest = new WeightRequest(TEST_DATE, TEST_VALUE);
        var expectedWeight = new Weight(TEST_DATE, TEST_VALUE);
        var actualWeight = weightRequest.toWeight();
        assertThat(actualWeight).usingRecursiveComparison().isEqualTo(expectedWeight);
    }
}