package com.somesoft.fittracker.service;

import com.somesoft.fittracker.entity.Product;
import com.somesoft.fittracker.exception.ProductNotFoundException;
import com.somesoft.fittracker.exception.ProductNotUpdatedException;
import com.somesoft.fittracker.exception.ProductPersistenceException;
import com.somesoft.fittracker.repository.ProductRepository;
import com.somesoft.fittracker.request.ProductRequest;
import com.somesoft.fittracker.response.ProductResponse;
import com.somesoft.fittracker.security.SecurityHelper;
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
        return productRepository.findByIdAndUserIdAndActiveIsTrue(id, SecurityHelper.getUserId())
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

    public void delete(UUID id) {
        Product dbProduct = productRepository.findByIdAndUserIdAndActiveIsTrue(
                id, SecurityHelper.getUserId())
            .orElseThrow(() -> new ProductNotFoundException(id));

        dbProduct.setActive(false);

        productRepository.save(dbProduct);
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
