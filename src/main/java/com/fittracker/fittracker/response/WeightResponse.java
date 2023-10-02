package com.fittracker.fittracker.response;

import com.fittracker.fittracker.entity.Weight;

import java.time.LocalDate;

public record WeightResponse(LocalDate date, Double value) {
    public static WeightResponse fromWeight(Weight weight) {
        return new WeightResponse(weight.getDate(), weight.getValue());
    }
}
