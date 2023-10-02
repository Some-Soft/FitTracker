package com.fittracker.fittracker.service;

import com.fittracker.fittracker.entity.Weight;
import com.fittracker.fittracker.exception.WeightNotFoundException;
import com.fittracker.fittracker.repository.WeightRepository;
import com.fittracker.fittracker.request.WeightRequest;
import com.fittracker.fittracker.response.WeightResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;


@Service
public class WeightService {

    private final WeightRepository weightRepository;

    @Autowired
    public WeightService(WeightRepository weightRepository) {
        this.weightRepository = weightRepository;
    }

    public WeightResponse save(WeightRequest weightRequest) {
        Weight weight = mapWeightRequestToWeight(weightRequest);
        Weight dbWeight = weightRepository.save(weight);
        return new WeightResponse(dbWeight.getDate(), dbWeight.getValue());
    }

    private Weight mapWeightRequestToWeight(WeightRequest weightRequest) {
        Weight weight = new Weight();
        weight.setDate(weightRequest.date());
        weight.setValue(weightRequest.value());

        return weight;
    }

    public WeightResponse findByDate(LocalDate date) {
        Weight weight = Optional.ofNullable(weightRepository.findByDate(date))
                .orElseThrow(()-> new WeightNotFoundException(date));
        return new WeightResponse(weight.getDate(),weight.getValue());
    }
}
