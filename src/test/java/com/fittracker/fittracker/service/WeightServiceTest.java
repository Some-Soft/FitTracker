package com.fittracker.fittracker.service;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.fittracker.fittracker.entity.Weight;
import com.fittracker.fittracker.exception.InvalidDateRangeException;
import com.fittracker.fittracker.exception.WeightAlreadyExistsException;
import com.fittracker.fittracker.exception.WeightNotFoundException;
import com.fittracker.fittracker.repository.WeightRepository;
import com.fittracker.fittracker.request.WeightRequest;
import com.fittracker.fittracker.response.WeightResponse;
import com.fittracker.fittracker.security.SecurityHelper;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class WeightServiceTest {

    private static final LocalDate TEST_DATE = LocalDate.of(2023, 10, 10);
    private static final Double TEST_VALUE = 100.1;

    private static final UUID TEST_UUID = UUID.fromString("948cc727-68e5-455c-ab6d-942e585bde0d");
    private static final WeightRequest WEIGHT_REQUEST = new WeightRequest(TEST_DATE, TEST_VALUE);
    private static final WeightResponse WEIGHT_RESPONSE = new WeightResponse(TEST_DATE, TEST_VALUE);

    private static MockedStatic<SecurityHelper> mockedStatic;

    @Captor
    private ArgumentCaptor<Weight> weightCaptor;

    @Mock
    private WeightRepository weightRepository;
    @InjectMocks
    private WeightService weightService;
    private Weight weight;

    @BeforeAll
    static void beforeAll() {
        mockedStatic = mockStatic(SecurityHelper.class);
        mockedStatic.when(SecurityHelper::getUserId).thenReturn(TEST_UUID);
    }

    @AfterAll
    static void AfterAll() {
        mockedStatic.close();
    }

    @BeforeEach
    void beforeEach() {
        weight = new Weight(TEST_DATE, TEST_VALUE, TEST_UUID);
    }

    @Nested
    class FindByDate {

        @Test
        void givenDateFound_shouldReturnWeightResponse() {
            when(weightRepository.findByDateAndUserId(any(), any())).thenReturn(of(weight));

            var result = weightService.findByDate(LocalDate.of(2023, 10, 10));

            assertThat(result).isEqualTo(WEIGHT_RESPONSE);
            verify(weightRepository).findByDateAndUserId(LocalDate.of(2023, 10, 10), TEST_UUID);
            verifyNoMoreInteractions(weightRepository);
        }

        @Test
        void givenNoDateFound_shouldThrowWeightNotFoundException() {
            when(weightRepository.findByDateAndUserId(any(), any())).thenReturn(empty());

            assertThatThrownBy(() -> weightService.findByDate(LocalDate.of(2023, 10, 10)))
                .isInstanceOf(WeightNotFoundException.class)
                .hasMessageContaining("Weight not found for date: 2023-10-10");

            verify(weightRepository).findByDateAndUserId(LocalDate.of(2023, 10, 10), TEST_UUID);
            verifyNoMoreInteractions(weightRepository);
        }

    }

    @Nested
    class Save {


        @Test
        void givenNoDateFound_shouldSaveAndReturnWeightResponse() {
            when(weightRepository.existsByDateAndUserId(any(), any())).thenReturn(false);
            when(weightRepository.save(any())).thenReturn(weight);

            var result = weightService.save(WEIGHT_REQUEST);

            assertThat(result).isEqualTo(WEIGHT_RESPONSE);
            verify(weightRepository).existsByDateAndUserId(TEST_DATE, TEST_UUID);
            verify(weightRepository).save(weightCaptor.capture());

            assertThat(weightCaptor.getValue()).usingRecursiveComparison().isEqualTo(weight);
            verifyNoMoreInteractions(weightRepository);
        }

        @Test
        void givenDateFound_shouldThrowException() {
            when(weightRepository.existsByDateAndUserId(any(), any())).thenReturn(true);

            assertThatThrownBy(() -> weightService.save(WEIGHT_REQUEST))
                .isInstanceOf(WeightAlreadyExistsException.class)
                .hasMessageContaining("Weight already exists for date: 2023-10-10");
            verify(weightRepository).existsByDateAndUserId(TEST_DATE, TEST_UUID);
            verifyNoMoreInteractions(weightRepository);
        }
    }

    @Nested
    class Update {

        WeightRequest updatedWeightRequest = new WeightRequest(TEST_DATE, TEST_VALUE + 1);

        @Test
        void givenDateFound_shouldUpdateAndReturnWeightResponse() {
            Weight updatedWeight = new Weight(TEST_DATE, TEST_VALUE + 1, TEST_UUID);
            when(weightRepository.findByDateAndUserId(any(), any())).thenReturn(of(weight));
            when(weightRepository.save(any())).thenReturn(updatedWeight);

            var expected = new WeightResponse(TEST_DATE, TEST_VALUE + 1);
            var result = weightService.update(updatedWeightRequest);

            assertThat(result).isEqualTo(expected);
            verify(weightRepository).findByDateAndUserId(TEST_DATE, TEST_UUID);
            verify(weightRepository).save(weightCaptor.capture());
            assertThat(weightCaptor.getValue()).usingRecursiveComparison().isEqualTo(updatedWeight);
            verifyNoMoreInteractions(weightRepository);

        }

        @Test
        void givenNoDateFound_shouldThrowException() {
            when(weightRepository.findByDateAndUserId(any(), any())).thenReturn(empty());

            assertThatThrownBy(() -> weightService.update(updatedWeightRequest))
                .isInstanceOf(WeightNotFoundException.class)
                .hasMessageContaining("Weight not found for date: 2023-10-10");
            verify(weightRepository).findByDateAndUserId(TEST_DATE, TEST_UUID);
            verifyNoMoreInteractions(weightRepository);
        }
    }

    @Nested
    class Delete {

        @Test
        void givenDateFound_shouldDeleteAndReturnEmptyResponse() {
            when(weightRepository.findByDateAndUserId(any(), any())).thenReturn(of(weight));

            weightService.delete(TEST_DATE);

            verify(weightRepository).findByDateAndUserId(TEST_DATE, TEST_UUID);
            verify(weightRepository).delete(weight);
            verifyNoMoreInteractions(weightRepository);
        }

        @Test
        void givenNoDateFound_shouldThrowException() {
            when(weightRepository.findByDateAndUserId(any(), any())).thenReturn(empty());

            assertThatThrownBy(() -> weightService.delete(TEST_DATE))
                .isInstanceOf(WeightNotFoundException.class)
                .hasMessageContaining("Weight not found for date: 2023-10-10");
            verify(weightRepository).findByDateAndUserId(TEST_DATE, TEST_UUID);
            verifyNoMoreInteractions(weightRepository);
        }
    }

    @Nested
    class FindByDateRange {

        private final LocalDate endDate = TEST_DATE.plusDays(3);
        private final Weight endWeight = new Weight(endDate, TEST_VALUE, TEST_UUID);

        @Test
        void givenStartDateAfterEndDate_shouldThrowInvalidDateRangeException() {
            assertThatThrownBy(() -> weightService.findByDateRange(LocalDate.of(2023, 5, 1), LocalDate.of(2023, 1, 1)))
                .isInstanceOf(InvalidDateRangeException.class)
                .hasMessageContaining("Start date cannot be after end date");

            verifyNoInteractions(weightRepository);
        }

        @Test
        void givenValidDateRange_shouldReturnListOfWeightResponses() {
            when(weightRepository.findByDateBetweenAndUserId(any(), any(), any()))
                .thenReturn(List.of(weight, endWeight));

            var expected = List.of(new WeightResponse(TEST_DATE, TEST_VALUE), new WeightResponse(endDate, TEST_VALUE));
            var result = weightService.findByDateRange(TEST_DATE, endDate);

            assertThat(result).isEqualTo(expected);
            verify(weightRepository).findByDateBetweenAndUserId(TEST_DATE, endDate, TEST_UUID);
            verifyNoMoreInteractions(weightRepository);
        }

        @Test
        void givenValidDateRangeWithNoWeights_shouldReturnEmptyList() {
            when(weightRepository.findByDateBetweenAndUserId(any(), any(), any()))
                .thenReturn(List.of());

            var result = weightService.findByDateRange(TEST_DATE, endDate);

            assertThat(result).isEmpty();
            verify(weightRepository).findByDateBetweenAndUserId(TEST_DATE, endDate, TEST_UUID);

            verifyNoMoreInteractions(weightRepository);
        }

    }

}