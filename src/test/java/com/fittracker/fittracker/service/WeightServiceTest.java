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

    private final LocalDate TEST_DATE = LocalDate.of(2023,10,10);

    private final Double TEST_VALUE = 100.1;

    private final Weight weight = new Weight(TEST_DATE, TEST_VALUE);

    private final WeightRequest weightRequest = new WeightRequest(TEST_DATE,TEST_VALUE);

    private final WeightResponse weightResponse = new WeightResponse(TEST_DATE,TEST_VALUE);

    @Nested
    class FindByDate {
        @Test
        void givenDateFound_shouldReturnWeightResponse() {
            when(weightRepository.findByDate(any())).thenReturn(of(weight));

            var result = weightService.findByDate(LocalDate.of(2023, 10, 10));

            assertThat(result).isEqualTo(weightResponse);
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
            when(weightRepository.save(any())).thenReturn(weight);

            var result = weightService.save(weightRequest);

            assertThat(result).isEqualTo(weightResponse);
            verify(weightRepository).existsByDate(TEST_DATE);
            verify(weightRepository).save(any());
            verifyNoMoreInteractions(weightRepository);

        }
        @Test
        void givenDateFound_shouldThrowException() {
            when(weightRepository.existsByDate(any())).thenReturn(true);

            assertThatThrownBy(()-> weightService.save(weightRequest))
                    .isInstanceOf(WeightAlreadyExistsException.class)
                    .hasMessageContaining("Weight already exists for date: 2023-10-10");
            verify(weightRepository).existsByDate(TEST_DATE);
            verifyNoMoreInteractions(weightRepository);
        }
        
    }
}
