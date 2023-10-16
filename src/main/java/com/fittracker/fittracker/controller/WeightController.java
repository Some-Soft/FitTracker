package com.fittracker.fittracker.controller;

import com.fittracker.fittracker.request.WeightRequest;
import com.fittracker.fittracker.response.WeightResponse;
import com.fittracker.fittracker.service.WeightService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequestMapping("/api/v1")
public class WeightController {

    private final WeightService weightService;

    @Autowired
    public WeightController(WeightService weightService) {
        this.weightService = weightService;
    }

    @PostMapping("/weight")
    @ResponseStatus(CREATED)
    public WeightResponse addWeight(@RequestBody @Valid WeightRequest weightRequest) {
        return weightService.save(weightRequest);
    }

    @GetMapping("/weight")
    @ResponseStatus(OK)
    public WeightResponse getWeight(@RequestParam @DateTimeFormat(iso = DATE) LocalDate date) {
        return weightService.findByDate(date);
    }
}
