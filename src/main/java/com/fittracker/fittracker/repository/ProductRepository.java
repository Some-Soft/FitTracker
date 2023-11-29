package com.fittracker.fittracker.repository;

import com.fittracker.fittracker.entity.Product;
import com.fittracker.fittracker.entity.ProductId;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.repository.CrudRepository;

public interface ProductRepository extends CrudRepository<Product, ProductId> {

    Optional<Product> findByIdAndUserId(UUID id, UUID userId);

}
