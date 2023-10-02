package com.fittracker.fittracker.request;

import com.fittracker.fittracker.validation.DateAfter;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record WeightRequest(@DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
                            @NotNull(message = "must provide date")
                            @PastOrPresent(message = "cannot be in the future")
                            @DateAfter("2022-12-31")
                            LocalDate date,
                            @Min(value = 0, message = "must be positive")
                            @Max(value = 635, message = "must be less than 635")
                            @NotNull(message = "must provide weight")
                            Double value
                            ) {

}
