package com.fittracker.fittracker.repository;

import com.fittracker.fittracker.entity.Weight;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface WeightRepository extends CrudRepository<Weight, Long> {

    Boolean existsByDateAndUserId(LocalDate date, UUID userId);

    Optional<Weight> findByDateAndUserId(LocalDate date, UUID userId);

    List<Weight> findByDateBetweenAndUserId(LocalDate startDate, LocalDate endDate, UUID userId);
}
