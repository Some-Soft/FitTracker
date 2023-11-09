package com.fittracker.fittracker.service;

import com.fittracker.fittracker.entity.Weight;
import com.fittracker.fittracker.exception.WeightAlreadyExistsException;
import com.fittracker.fittracker.exception.WeightNotFoundException;
import com.fittracker.fittracker.repository.WeightRepository;
import com.fittracker.fittracker.request.WeightRequest;
import com.fittracker.fittracker.response.WeightResponse;
import com.fittracker.fittracker.security.SecurityHelper;
import org.junit.jupiter.api.AfterAll;
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

import java.time.LocalDate;
import java.util.UUID;

import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WeightServiceTest {

    private static final LocalDate TEST_DATE = LocalDate.of(2023,10,10);
    private static final Double TEST_VALUE = 100.1;

    private static final UUID TEST_UUID = UUID.fromString("948cc727-68e5-455c-ab6d-942e585bde0d");
    private static final Weight WEIGHT = new Weight(TEST_DATE, TEST_VALUE,TEST_UUID);
    private static final WeightRequest WEIGHT_REQUEST = new WeightRequest(TEST_DATE,TEST_VALUE);
    private static final WeightResponse WEIGHT_RESPONSE = new WeightResponse(TEST_DATE,TEST_VALUE);

    private static MockedStatic<SecurityHelper> mockedStatic;

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

    @Nested
    class FindByDate {
        @Test
        void givenDateFound_shouldReturnWeightResponse() {
            when(weightRepository.findByDateAndUserId(any(),any())).thenReturn(of(WEIGHT));

            var result = weightService.findByDate(LocalDate.of(2023, 10, 10));

            assertThat(result).isEqualTo(WEIGHT_RESPONSE);
            verify(weightRepository).findByDateAndUserId(LocalDate.of(2023, 10, 10),TEST_UUID);
            verifyNoMoreInteractions(weightRepository);
        }

        @Test
        void givenNoDateFound_shouldThrowWeightNotFoundException() {
            when(weightRepository.findByDateAndUserId(any(),any())).thenReturn(empty());

            assertThatThrownBy(() -> weightService.findByDate(LocalDate.of(2023, 10, 10)))
                    .isInstanceOf(WeightNotFoundException.class)
                    .hasMessageContaining("Weight not found for date: 2023-10-10" );

            verify(weightRepository).findByDateAndUserId(LocalDate.of(2023, 10, 10),TEST_UUID);
            verifyNoMoreInteractions(weightRepository);
        }

    }

    @Nested
    class Save {

        @Captor
        ArgumentCaptor<Weight> weightCaptor;
        @Test
        void givenNoDateFound_shouldSaveAndReturnWeightResponse() {
            when(weightRepository.existsByDateAndUserId(any(),any())).thenReturn(false);
            when(weightRepository.save(any())).thenReturn(WEIGHT);

            var result = weightService.save(WEIGHT_REQUEST);

            assertThat(result).isEqualTo(WEIGHT_RESPONSE);
            verify(weightRepository).existsByDateAndUserId(TEST_DATE,TEST_UUID);
            verify(weightRepository).save(weightCaptor.capture());

            assertThat(weightCaptor.getValue()).usingRecursiveComparison().isEqualTo(WEIGHT);
            verifyNoMoreInteractions(weightRepository);

        }
        @Test
        void givenDateFound_shouldThrowException() {
            when(weightRepository.existsByDateAndUserId(any(),any())).thenReturn(true);

            assertThatThrownBy(()-> weightService.save(WEIGHT_REQUEST))
                    .isInstanceOf(WeightAlreadyExistsException.class)
                    .hasMessageContaining("Weight already exists for date: 2023-10-10");
            verify(weightRepository).existsByDateAndUserId(TEST_DATE,TEST_UUID);
            verifyNoMoreInteractions(weightRepository);
        }
    }
}
