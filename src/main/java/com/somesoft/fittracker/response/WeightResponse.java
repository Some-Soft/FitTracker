package com.somesoft.fittracker.response;

import com.somesoft.fittracker.entity.Weight;
import java.time.LocalDate;

public record WeightResponse(LocalDate date, Double value) {

    public static WeightResponse fromWeight(Weight weight) {
        return new WeightResponse(weight.getDate(), weight.getValue());
    }

}
