package com.fittracker.fittracker.repository;

import com.fittracker.fittracker.entity.Weight;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;

public interface WeightRepository extends CrudRepository<Weight,Integer> {
    Weight findByDate(LocalDate date);
}
