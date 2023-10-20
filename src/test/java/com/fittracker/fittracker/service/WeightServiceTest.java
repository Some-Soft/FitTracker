package com.fittracker.fittracker.service;

import com.fittracker.fittracker.entity.Weight;
import com.fittracker.fittracker.exception.WeightAlreadyExistsException;
import com.fittracker.fittracker.exception.WeightNotFoundException;
import com.fittracker.fittracker.repository.WeightRepository;
import com.fittracker.fittracker.request.WeightRequest;
import com.fittracker.fittracker.response.WeightResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WeightServiceTest {

    @Mock
    private WeightRepository weightRepository;

    @InjectMocks
    private WeightService weightService;

    private static final LocalDate TEST_DATE = LocalDate.of(2023,10,10);

    private static final Double TEST_VALUE = 100.1;

    private static final Weight WEIGHT = new Weight(TEST_DATE, TEST_VALUE);

    private static final WeightRequest WEIGHT_REQUEST = new WeightRequest(TEST_DATE,TEST_VALUE);

    private static final WeightResponse WEIGHT_RESPONSE = new WeightResponse(TEST_DATE,TEST_VALUE);

    @Nested
    class FindByDate {
        @Test
        void givenDateFound_shouldReturnWeightResponse() {
            when(weightRepository.findByDate(any())).thenReturn(of(WEIGHT));

            var result = weightService.findByDate(LocalDate.of(2023, 10, 10));

            assertThat(result).isEqualTo(WEIGHT_RESPONSE);
            verify(weightRepository).findByDate(LocalDate.of(2023, 10, 10));
            verifyNoMoreInteractions(weightRepository);
        }

        @Test
        void givenNoDateFound_shouldThrowWeightNotFoundException() {
            when(weightRepository.findByDate(any())).thenReturn(empty());

            assertThatThrownBy(() -> weightService.findByDate(LocalDate.of(2023, 10, 10)))
                    .isInstanceOf(WeightNotFoundException.class)
                    .hasMessageContaining("Weight not found for date: 2023-10-10" );

            verify(weightRepository).findByDate(LocalDate.of(2023, 10, 10));
            verifyNoMoreInteractions(weightRepository);
        }

    }

    @Nested
    class Save {
        @Test
        void givenNoDateFound_shouldSaveAndReturnWeightResponse() {
            when(weightRepository.existsByDate(any())).thenReturn(false);
            when(weightRepository.save(any())).thenReturn(WEIGHT);

            var result = weightService.save(WEIGHT_REQUEST);

            assertThat(result).isEqualTo(WEIGHT_RESPONSE);
            verify(weightRepository).existsByDate(TEST_DATE);
            verify(weightRepository).save(any());
            verifyNoMoreInteractions(weightRepository);

        }
        @Test
        void givenDateFound_shouldThrowException() {
            when(weightRepository.existsByDate(any())).thenReturn(true);

            assertThatThrownBy(()-> weightService.save(WEIGHT_REQUEST))
                    .isInstanceOf(WeightAlreadyExistsException.class)
                    .hasMessageContaining("Weight already exists for date: 2023-10-10");
            verify(weightRepository).existsByDate(TEST_DATE);
            verifyNoMoreInteractions(weightRepository);
        }
        
    }
}
