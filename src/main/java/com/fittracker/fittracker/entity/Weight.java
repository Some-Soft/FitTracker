package com.fittracker.fittracker.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.util.UUID;

import static jakarta.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "weights", schema = "fittracker")
public class Weight {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private LocalDate date;

    private Double value;

    private UUID userId;

    public Weight() {
    }

    public Weight(LocalDate date, Double value) {
        this.date = date;
        this.value = value;
    }

    public Weight(LocalDate date, Double value, UUID uuid) {
        this.date = date;
        this.value = value;
        this.userId = uuid;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getUserId(){
        return this.userId;
    }
}
