package com.somesoft.fittracker.request;

import com.somesoft.fittracker.entity.Product;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record ProductRequest(
    @NotBlank(message = "Product name must not be blank")
    String name,
    @Max(value = 9999, message = "Cannot exceed 9999")
    @Min(value = 0, message = "Cannot be less than 0")
    int kcal,

    @Max(value = 9999, message = "Cannot exceed 9999")
    @Min(value = 0, message = "Cannot be less than 0")
    int carbs,
    @Max(value = 9999, message = "Cannot exceed 9999")
    @Min(value = 0, message = "Cannot be less than 0")
    int protein,
    @Max(value = 9999, message = "Cannot exceed 9999")
    @Min(value = 0, message = "Cannot be less than 0")
    int fat

) {

    public Product toProduct() {
        return new Product(this.name, this.kcal, this.carbs, this.protein, this.fat);
    }

}
