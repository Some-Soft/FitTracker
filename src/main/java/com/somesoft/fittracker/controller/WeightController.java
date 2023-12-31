package com.somesoft.fittracker.controller;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import com.somesoft.fittracker.request.WeightRequest;
import com.somesoft.fittracker.response.WeightResponse;
import com.somesoft.fittracker.service.WeightService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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

    @PutMapping("/weight")
    @ResponseStatus(OK)
    public WeightResponse updateWeight(@RequestBody @Valid WeightRequest weightRequest) {
        return weightService.update(weightRequest);
    }

    @DeleteMapping("/weight")
    @ResponseStatus(NO_CONTENT)
    public void deleteWeight(@RequestParam @DateTimeFormat(iso = DATE) LocalDate date) {
        weightService.delete(date);
    }

    @GetMapping("/weights")
    @ResponseStatus(OK)
    public List<WeightResponse> getWeights(@RequestParam @DateTimeFormat(iso = DATE) LocalDate startDate,
        @RequestParam @DateTimeFormat(iso = DATE) LocalDate endDate) {
        return weightService.findByDateRange(startDate, endDate);
    }
}
