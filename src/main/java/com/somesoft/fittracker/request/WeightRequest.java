package com.somesoft.fittracker.request;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;

import com.somesoft.fittracker.entity.Weight;
import com.somesoft.fittracker.validation.DateAfter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import java.time.LocalDate;
import org.springframework.format.annotation.DateTimeFormat;

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