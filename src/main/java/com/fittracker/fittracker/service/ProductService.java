package com.fittracker.fittracker.service;

import com.fittracker.fittracker.entity.Product;
import com.fittracker.fittracker.exception.ProductAlreadyExistsException;
import com.fittracker.fittracker.exception.ProductNotFoundException;
import com.fittracker.fittracker.repository.ProductRepository;
import com.fittracker.fittracker.request.ProductRequest;
import com.fittracker.fittracker.response.ProductResponse;
import com.fittracker.fittracker.security.SecurityHelper;
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

    public ProductResponse update(UUID id, ProductRequest productRequest) {
        Product dbProduct = productRepository.findByIdAndUserIdAndActive(
            id, SecurityHelper.getUserId(), true)
            .orElseThrow(() -> new ProductNotFoundException(id));

        Product newProduct = productRequest.toProduct();

        if (dbProduct.equals(newProduct)){
            throw new ProductAlreadyExistsException();
        }

        newProduct.setUserId(SecurityHelper.getUserId());
        Product product = productRepository.save(newProduct);
        product.setId(id);

        return ProductResponse.fromProduct(product);
    }

    private void copyProductFields(Product copyTo, Product copyFrom){
        copyTo.setName(copyFrom.getName());
        copyTo.setKcal(copyFrom.getKcal());
        copyTo.setCarbs(copyFrom.getCarbs());
        copyTo.setProtein(copyFrom.getProtein());
        copyTo.setFat(copyFrom.getFat());
    }
}
