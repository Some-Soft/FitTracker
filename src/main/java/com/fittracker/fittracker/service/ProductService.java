package com.fittracker.fittracker.service;

import com.fittracker.fittracker.entity.Product;
import com.fittracker.fittracker.exception.ProductAlreadyExistsException;
import com.fittracker.fittracker.exception.ProductNotFoundException;
import com.fittracker.fittracker.repository.ProductRepository;
import com.fittracker.fittracker.request.ProductRequest;
import com.fittracker.fittracker.response.ProductResponse;
import com.fittracker.fittracker.security.SecurityHelper;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionSystemException;

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
        Product dbProduct = productRepository.findByIdAndUserIdAndActive(
                id, SecurityHelper.getUserId(), true)
            .orElseThrow(() -> new ProductNotFoundException(id));

        Product newProduct = productRequest.toProduct();

        if (dbProduct.equals(newProduct)) {
            throw new ProductAlreadyExistsException();
        }

        dbProduct.setActive(false);
        productRepository.save(dbProduct);

        newProduct.setId(id);
        newProduct.setUserId(SecurityHelper.getUserId());
        newProduct.setVersion(dbProduct.getVersion() + 1);

        Product product = productRepository.saveNew(newProduct)
            .orElseThrow(() -> new TransactionSystemException("Cannot find updated product"));

        return ProductResponse.fromProduct(product);
    }

}
