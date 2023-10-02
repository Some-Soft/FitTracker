package com.fittracker.fittracker.service;

import com.fittracker.fittracker.entity.Weight;
import com.fittracker.fittracker.exception.WeightAlreadyExistsException;
import com.fittracker.fittracker.exception.WeightNotFoundException;
import com.fittracker.fittracker.repository.WeightRepository;
import com.fittracker.fittracker.request.WeightRequest;
import com.fittracker.fittracker.response.WeightResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class WeightService {

    private final WeightRepository weightRepository;

    @Autowired
    public WeightService(WeightRepository weightRepository) {
        this.weightRepository = weightRepository;
    }

    public WeightResponse save(WeightRequest weightRequest) {
        if (weightRepository.existsByDate(weightRequest.date())) {
            throw new WeightAlreadyExistsException(weightRequest.date());
        }
        Weight weight = weightRepository.save(weightRequest.toWeight());

        return WeightResponse.fromWeight(weight);
    }

    public WeightResponse findByDate(LocalDate date) {
        return weightRepository.findByDate(date)
                .map(WeightResponse::fromWeight)
                .orElseThrow(()-> new WeightNotFoundException(date));
    }
}
