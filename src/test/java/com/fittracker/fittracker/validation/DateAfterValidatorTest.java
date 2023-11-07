package com.fittracker.fittracker.validation;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DateAfterValidatorTest {

    @InjectMocks
    private DateAfterValidator dateAfterValidator;

    @Mock
    private DateAfter dateAfter;

    @BeforeEach
    void beforeEach() {
        when(dateAfter.value()).thenReturn("2022-12-31");
        dateAfterValidator.initialize(dateAfter);
    }

    @Test
    void givenDateAfterStartingDate_shouldReturnTrue() {
        LocalDate testDate = LocalDate.of(2023,1,1);
        assertTrue(dateAfterValidator.isValid(testDate, null));
    }

    @Test
    void givenDateEqualToStartingDate_shouldReturnFalse() {
        LocalDate testDate = LocalDate.of(2022,12,31);
        assertFalse(dateAfterValidator.isValid(testDate, null));
    }
}