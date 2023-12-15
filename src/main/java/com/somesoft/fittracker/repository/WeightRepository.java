package com.somesoft.fittracker.repository;

import com.somesoft.fittracker.entity.Weight;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface WeightRepository extends CrudRepository<Weight, Long> {

    Boolean existsByDateAndUserId(LocalDate date, UUID userId);

    Optional<Weight> findByDateAndUserId(LocalDate date, UUID userId);

    List<Weight> findByDateBetweenAndUserIdOrderByDate(LocalDate startDate, LocalDate endDate, UUID userId);
}
