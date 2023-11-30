package com.fittracker.fittracker.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "products", schema = "fittracker")
@IdClass(ProductId.class)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Id
    private int version;
    private String name;
    private int kcal;
    private int carbs;
    private int protein;
    private int fat;
    private UUID userId;

    @Column(insertable = false)
    private LocalDateTime updatedAt;

    @Column(insertable = false)
    private boolean active;

    public Product() {
    }

    public Product(String name, int kcal, int carbs, int protein, int fat) {
        this.name = name;
        this.kcal = kcal;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
    }

    public Product(String name, int kcal, int carbs, int protein, int fat, UUID userId, LocalDateTime updatedAt) {
        this.name = name;
        this.kcal = kcal;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
        this.userId = userId;
        this.updatedAt = updatedAt;
    }

    public Product(UUID id, int version, String name, int kcal, int carbs, int protein, int fat, UUID userId,
        LocalDateTime updatedAt, boolean active) {
        this.id = id;
        this.version = version;
        this.name = name;
        this.kcal = kcal;
        this.carbs = carbs;
        this.protein = protein;
        this.fat = fat;
        this.userId = userId;
        this.updatedAt = updatedAt;
        this.active = active;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getKcal() {
        return kcal;
    }

    public void setKcal(int kcal) {
        this.kcal = kcal;
    }

    public int getCarbs() {
        return carbs;
    }

    public void setCarbs(int carbs) {
        this.carbs = carbs;
    }

    public int getProtein() {
        return protein;
    }

    public void setProtein(int protein) {
        this.protein = protein;
    }

    public int getFat() {
        return fat;
    }

    public void setFat(int fat) {
        this.fat = fat;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Product product = (Product) o;
        return kcal == product.kcal && carbs == product.carbs && protein == product.protein && fat == product.fat
            && Objects.equals(name, product.name);
    }

}
