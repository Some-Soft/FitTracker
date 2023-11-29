package com.fittracker.fittracker.response;

import static com.fittracker.fittracker.dataprovider.Entity.product;
import static com.fittracker.fittracker.dataprovider.Response.productResponse;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ProductResponseTest {

    @Test
    void fromProduct_givenProduct_shouldReturnProductResponse() {
        var expected = productResponse();
        var result = ProductResponse.fromProduct(product());

        assertThat(result).isEqualTo(expected);
    }
}