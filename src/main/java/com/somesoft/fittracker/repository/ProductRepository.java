package com.somesoft.fittracker.repository;

import com.somesoft.fittracker.entity.Product;
import com.somesoft.fittracker.entity.ProductId;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends CrudRepository<Product, ProductId> {

    Optional<Product> findByIdAndUserIdAndActiveIsTrue(UUID id, UUID userId);

    Optional<Product> findByNameAndUserIdAndActiveIsTrue(String name, UUID userId);

    @Query(value = "INSERT INTO fittracker.products (id, version, name, kcal, carbs, protein, fat, user_id) " +
        "VALUES (:#{#product.id}, :#{#product.version}, :#{#product.name}, :#{#product.kcal}, " +
        ":#{#product.carbs}, :#{#product.protein}, :#{#product.fat}, :#{#product.userId}) RETURNING *;",
        nativeQuery = true)
    Optional<Product> saveNew(@Param("product") Product product);
}
