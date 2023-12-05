package com.fittracker.fittracker.request;

import static com.fittracker.fittracker.dataprovider.Entity.product;
import static com.fittracker.fittracker.dataprovider.Request.productRequest;
import static com.fittracker.fittracker.dataprovider.TestHelper.compareUpTo;

import org.junit.jupiter.api.Test;

class ProductRequestTest {

    @Test
    void toProduct_givenProductRequest_shouldReturnProduct() {
        ProductRequest productRequest = productRequest();

        var expected = product();
        var result = productRequest.toProduct();

        compareUpTo(result, expected, "id", "userId", "updatedAt", "active");
    }
}