package com.fittracker.fittracker.repository;

import com.fittracker.fittracker.entity.Product;
import com.fittracker.fittracker.entity.ProductId;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends CrudRepository<Product, ProductId> {

    Optional<Product> findByIdAndUserId(UUID id, UUID userId);

    Optional<Product> findByIdAndUserIdAndActive(UUID id, UUID userId, boolean active);

    @Modifying
    @Query(value = "INSERT INTO fittracker.products (id, version, name, kcal, carbs, protein, fat, user_id) " +
        "VALUES (:#{#product.id}, :#{#product.version}, :#{#product.name}, :#{#product.kcal}, " +
        ":#{#product.carbs}, :#{#product.protein}, :#{#product.fat}, :#{#product.userId})",
        nativeQuery = true)
    void insert(@Param("product") Product product);

    default Optional<Product> saveNew(Product product) {
        insert(product);
        return findByIdAndUserIdAndActive(product.getId(), product.getUserId(), true);
    }

}
