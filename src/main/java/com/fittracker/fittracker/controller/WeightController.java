package com.fittracker.fittracker.controller;

import com.fittracker.fittracker.request.WeightRequest;
import com.fittracker.fittracker.response.WeightResponse;
import com.fittracker.fittracker.service.WeightService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
public class WeightController {

    private final WeightService weightService;

    @Autowired
    public WeightController(WeightService weightService) {
        this.weightService = weightService;
    }

    @PostMapping("/weight")
    @ResponseStatus(HttpStatus.CREATED)
    public WeightResponse addWeight(@RequestBody @Valid WeightRequest weightRequest) {
        return weightService.save(weightRequest);
    }

    @GetMapping("/weight")
    @ResponseStatus(HttpStatus.OK)
    public WeightResponse getWeight(@RequestParam
                                    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return weightService.findByDate(date);
    }
}
