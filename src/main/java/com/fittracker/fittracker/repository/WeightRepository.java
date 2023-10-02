package com.fittracker.fittracker.repository;

import com.fittracker.fittracker.entity.Weight;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface WeightRepository extends CrudRepository<Weight,Integer> {
    Boolean existsByDate(LocalDate date);

    Optional<Weight> findByDate(LocalDate date);
}
