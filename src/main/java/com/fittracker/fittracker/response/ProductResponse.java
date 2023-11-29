package com.fittracker.fittracker.response;

import com.fittracker.fittracker.entity.Product;
import java.util.UUID;

public record ProductResponse(UUID id, String name, int kcal, int carbs, int protein, int fat) {

    public static ProductResponse fromProduct(Product product) {
        return new ProductResponse(product.getId(), product.getName(), product.getKcal(), product.getCarbs(),
            product.getProtein(),
            product.getFat());
    }

}
