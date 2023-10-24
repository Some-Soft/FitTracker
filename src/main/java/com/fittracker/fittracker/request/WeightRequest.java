package com.fittracker.fittracker.request;

import com.fittracker.fittracker.entity.Weight;
import com.fittracker.fittracker.validation.DateAfter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;

public record WeightRequest(
        @DateTimeFormat(iso = DATE)
        @NotNull(message = "Date must not be null")
        @PastOrPresent(message = "Date cannot be in the future")
        @DateAfter(value = "2022-12-31", message = "Date must be after 2022")
        LocalDate date,
        @Min(value = 0, message = "Value must be positive")
        @Max(value = 635, message = "Value must be less or equal to 635")
        @NotNull(message = "Value must not be null")
        Double value
) {
        public Weight toWeight() {
                return new Weight(this.date, this.value);
        }
}