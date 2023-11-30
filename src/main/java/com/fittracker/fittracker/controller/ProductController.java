package com.fittracker.fittracker.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.fittracker.fittracker.request.ProductRequest;
import com.fittracker.fittracker.response.ProductResponse;
import com.fittracker.fittracker.service.ProductService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ProductController {

    private final ProductService productService;

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/product")
    @ResponseStatus(CREATED)
    public ProductResponse addProduct(@RequestBody @Valid ProductRequest productRequest) {
        return productService.save(productRequest);
    }

    @GetMapping("/product/{id}")
    @ResponseStatus(OK)
    public ProductResponse getProduct(@PathVariable UUID id) {
        return productService.findById(id);
    }

    //todo: integration
    @PutMapping("/product/{id}")
    public ProductResponse updateProduct(@PathVariable UUID id, @RequestBody @Valid ProductRequest productRequest) {
        return productService.update(id, productRequest);
    }
}
