package com.somesoft.fittracker.service;

import static com.somesoft.fittracker.dataprovider.Entity.weight;
import static com.somesoft.fittracker.dataprovider.Entity.weightWithDate;
import static com.somesoft.fittracker.dataprovider.Entity.weightWithValue;
import static com.somesoft.fittracker.dataprovider.Request.weightRequest;
import static com.somesoft.fittracker.dataprovider.Request.weightRequestWithValue;
import static com.somesoft.fittracker.dataprovider.Response.weightResponse;
import static com.somesoft.fittracker.dataprovider.Response.weightResponseWithDate;
import static com.somesoft.fittracker.dataprovider.Response.weightResponseWithValue;
import static com.somesoft.fittracker.dataprovider.TestHelper.assertEqualRecursiveIgnoring;
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

import com.somesoft.fittracker.entity.Weight;
import com.somesoft.fittracker.exception.InvalidDateRangeException;
import com.somesoft.fittracker.exception.WeightAlreadyExistsException;
import com.somesoft.fittracker.exception.WeightNotFoundException;
import com.somesoft.fittracker.repository.WeightRepository;
import com.somesoft.fittracker.request.WeightRequest;
import com.somesoft.fittracker.security.SecurityHelper;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
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

    private static MockedStatic<SecurityHelper> mockedStatic;
    private static final LocalDate TEST_DATE = LocalDate.of(2023, 10, 10);
    private static final UUID TEST_UUID = UUID.fromString("948cc727-68e5-455c-ab6d-942e585bde0d");
    @Captor
    private ArgumentCaptor<Weight> weightCaptor;

    @Mock
    private WeightRepository weightRepository;
    @InjectMocks
    private WeightService weightService;


    @BeforeAll
    static void beforeAll() {
        mockedStatic = mockStatic(SecurityHelper.class);
        mockedStatic.when(SecurityHelper::getUserId).thenReturn(TEST_UUID);
    }

    @AfterAll
    static void AfterAll() {
        mockedStatic.close();
    }

    @AfterEach
    public void afterEach() {
        verifyNoMoreInteractions(weightRepository);
    }

    @Nested
    class FindByDate {

        @Test
        void givenDateFound_shouldReturnWeightResponse() {
            when(weightRepository.findByDateAndUserId(any(), any())).thenReturn(of(weight()));

            var result = weightService.findByDate(TEST_DATE);

            assertThat(result).isEqualTo(weightResponse());
            verify(weightRepository).findByDateAndUserId(TEST_DATE, TEST_UUID);
        }

        @Test
        void givenNoDateFound_shouldThrowWeightNotFoundException() {
            when(weightRepository.findByDateAndUserId(any(), any())).thenReturn(empty());

            assertThatThrownBy(() -> weightService.findByDate(TEST_DATE))
                .isInstanceOf(WeightNotFoundException.class)
                .hasMessageContaining("Weight not found for date: 2023-10-10");

            verify(weightRepository).findByDateAndUserId(TEST_DATE, TEST_UUID);
        }

    }

    @Nested
    class Save {


        @Test
        void givenNoDateFound_shouldSaveAndReturnWeightResponse() {
            when(weightRepository.existsByDateAndUserId(any(), any())).thenReturn(false);
            when(weightRepository.save(any())).thenReturn(weight());

            var result = weightService.save(weightRequest());

            assertThat(result).isEqualTo(weightResponse());
            verify(weightRepository).existsByDateAndUserId(TEST_DATE, TEST_UUID);
            verify(weightRepository).save(weightCaptor.capture());
            assertEqualRecursiveIgnoring(weightCaptor.getValue(), weight());
        }

        @Test
        void givenDateFound_shouldThrowException() {
            when(weightRepository.existsByDateAndUserId(any(), any())).thenReturn(true);

            assertThatThrownBy(() -> weightService.save(weightRequest()))
                .isInstanceOf(WeightAlreadyExistsException.class)
                .hasMessageContaining("Weight already exists for date: 2023-10-10");
            verify(weightRepository).existsByDateAndUserId(TEST_DATE, TEST_UUID);
        }
    }

    @Nested
    class Update {

        WeightRequest updatedWeightRequest = weightRequestWithValue(52.3);

        @Test
        void givenDateFound_shouldUpdateAndReturnWeightResponse() {
            Weight updatedWeight = weightWithValue(52.3);
            when(weightRepository.findByDateAndUserId(any(), any())).thenReturn(of(weight()));
            when(weightRepository.save(any())).thenReturn(updatedWeight);

            var expected = weightResponseWithValue(52.3);
            var result = weightService.update(updatedWeightRequest);

            assertThat(result).isEqualTo(expected);
            verify(weightRepository).findByDateAndUserId(TEST_DATE, TEST_UUID);
            verify(weightRepository).save(weightCaptor.capture());
            assertEqualRecursiveIgnoring(weightCaptor.getValue(), updatedWeight);
        }

        @Test
        void givenNoDateFound_shouldThrowException() {
            when(weightRepository.findByDateAndUserId(any(), any())).thenReturn(empty());

            assertThatThrownBy(() -> weightService.update(updatedWeightRequest))
                .isInstanceOf(WeightNotFoundException.class)
                .hasMessageContaining("Weight not found for date: 2023-10-10");
            verify(weightRepository).findByDateAndUserId(TEST_DATE, TEST_UUID);
        }
    }

    @Nested
    class Delete {

        @Test
        void givenDateFound_shouldDeleteAndReturnEmptyResponse() {
            when(weightRepository.findByDateAndUserId(any(), any())).thenReturn(of(weight()));

            weightService.delete(TEST_DATE);

            verify(weightRepository).findByDateAndUserId(TEST_DATE, TEST_UUID);
            verify(weightRepository).delete(weightCaptor.capture());
            assertEqualRecursiveIgnoring(weightCaptor.getValue(), weight());
        }

        @Test
        void givenNoDateFound_shouldThrowException() {
            when(weightRepository.findByDateAndUserId(any(), any())).thenReturn(empty());

            assertThatThrownBy(() -> weightService.delete(TEST_DATE))
                .isInstanceOf(WeightNotFoundException.class)
                .hasMessageContaining("Weight not found for date: 2023-10-10");
            verify(weightRepository).findByDateAndUserId(TEST_DATE, TEST_UUID);
        }
    }

    @Nested
    class FindByDateRange {

        private final LocalDate endDate = TEST_DATE.plusDays(3);
        private final Weight endWeight = weightWithDate(endDate);

        @Test
        void givenStartDateAfterEndDate_shouldThrowInvalidDateRangeException() {
            assertThatThrownBy(() -> weightService.findByDateRange(LocalDate.of(2023, 5, 1), LocalDate.of(2023, 1, 1)))
                .isInstanceOf(InvalidDateRangeException.class)
                .hasMessageContaining("Start date cannot be after end date");

            verifyNoInteractions(weightRepository);
        }

        @Test
        void givenValidDateRange_shouldReturnListOfWeightResponses() {
            when(weightRepository.findByDateBetweenAndUserIdOrderByDate(any(), any(), any()))
                .thenReturn(List.of(weight(), endWeight));

            var expected = List.of(weightResponse(), weightResponseWithDate(endDate));
            var result = weightService.findByDateRange(TEST_DATE, endDate);

            assertThat(result).isEqualTo(expected);
            verify(weightRepository).findByDateBetweenAndUserIdOrderByDate(TEST_DATE, endDate, TEST_UUID);
        }

        @Test
        void givenValidDateRangeWithNoWeights_shouldReturnEmptyList() {
            when(weightRepository.findByDateBetweenAndUserIdOrderByDate(any(), any(), any()))
                .thenReturn(List.of());

            var result = weightService.findByDateRange(TEST_DATE, endDate);

            assertThat(result).isEmpty();
            verify(weightRepository).findByDateBetweenAndUserIdOrderByDate(TEST_DATE, endDate, TEST_UUID);
        }

    }

}