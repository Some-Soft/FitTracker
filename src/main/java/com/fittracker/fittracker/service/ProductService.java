package com.fittracker.fittracker.service;

import com.fittracker.fittracker.entity.Product;
import com.fittracker.fittracker.exception.ProductNotFoundException;
import com.fittracker.fittracker.exception.ProductNotUpdatedException;
import com.fittracker.fittracker.exception.ProductPersistenceException;
import com.fittracker.fittracker.repository.ProductRepository;
import com.fittracker.fittracker.request.ProductRequest;
import com.fittracker.fittracker.response.ProductResponse;
import com.fittracker.fittracker.security.SecurityHelper;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse save(ProductRequest productRequest) {
        Product product = productRequest.toProduct();
        product.setUserId(SecurityHelper.getUserId());
        Product dbProduct = productRepository.save(product);

        return ProductResponse.fromProduct(dbProduct);
    }

    public ProductResponse findById(UUID id) {
        return productRepository.findByIdAndUserId(id, SecurityHelper.getUserId())
            .map(ProductResponse::fromProduct)
            .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Transactional
    public ProductResponse update(UUID id, ProductRequest productRequest) {
        Product dbProduct = productRepository.findByIdAndUserIdAndActiveIsTrue(
                id, SecurityHelper.getUserId())
            .orElseThrow(() -> new ProductNotFoundException(id));

        Product updatedProduct = productRequest.toProduct();

        if (dbProduct.hasEqualData(updatedProduct)) {
            throw new ProductNotUpdatedException();
        }

        dbProduct.setActive(false);
        productRepository.save(dbProduct);

        return saveUpdatedProduct(dbProduct, updatedProduct);
    }

    private ProductResponse saveUpdatedProduct(Product dbProduct, Product updatedProduct) {
        updatedProduct.setId(dbProduct.getId());
        updatedProduct.setUserId(SecurityHelper.getUserId());
        updatedProduct.setVersion(dbProduct.getVersion() + 1);

        return productRepository.saveNew(updatedProduct)
            .map(ProductResponse::fromProduct)
            .orElseThrow(() -> new ProductPersistenceException(updatedProduct));
    }

}
