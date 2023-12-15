package com.somesoft.fittracker.service;

import com.somesoft.fittracker.entity.Weight;
import com.somesoft.fittracker.exception.InvalidDateRangeException;
import com.somesoft.fittracker.exception.WeightAlreadyExistsException;
import com.somesoft.fittracker.exception.WeightNotFoundException;
import com.somesoft.fittracker.repository.WeightRepository;
import com.somesoft.fittracker.request.WeightRequest;
import com.somesoft.fittracker.response.WeightResponse;
import com.somesoft.fittracker.security.SecurityHelper;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WeightService {

    private final WeightRepository weightRepository;

    @Autowired
    public WeightService(WeightRepository weightRepository) {
        this.weightRepository = weightRepository;
    }

    public WeightResponse save(WeightRequest weightRequest) {
        if (weightRepository.existsByDateAndUserId(weightRequest.date(), SecurityHelper.getUserId())) {
            throw new WeightAlreadyExistsException(weightRequest.date());
        }

        Weight weight = weightRequest.toWeight();
        weight.setUserId(SecurityHelper.getUserId());
        Weight dbWeight = weightRepository.save(weight);

        return WeightResponse.fromWeight(dbWeight);
    }

    public WeightResponse findByDate(LocalDate date) {
        return WeightResponse.fromWeight(getWeightFromDatabase(date));
    }

    public WeightResponse update(WeightRequest weightRequest) {
        Weight dbWeight = getWeightFromDatabase(weightRequest.date());
        dbWeight.setValue(weightRequest.value());

        Weight weight = weightRepository.save(dbWeight);

        return WeightResponse.fromWeight(weight);
    }

    public void delete(LocalDate date) {
        Weight dbWeight = getWeightFromDatabase(date);

        weightRepository.delete(dbWeight);
    }

    public List<WeightResponse> findByDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new InvalidDateRangeException();
        }

        return weightRepository.findByDateBetweenAndUserIdOrderByDate(startDate, endDate, SecurityHelper.getUserId())
            .stream()
            .map(WeightResponse::fromWeight)
            .toList();
    }

    private Weight getWeightFromDatabase(LocalDate date) {
        return weightRepository.findByDateAndUserId(date, SecurityHelper.getUserId())
            .orElseThrow(() -> new WeightNotFoundException(date));
    }
}
