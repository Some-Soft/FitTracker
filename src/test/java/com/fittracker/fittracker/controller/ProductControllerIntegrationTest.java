package com.fittracker.fittracker.controller;

import static com.fittracker.fittracker.dataprovider.Entity.product;
import static com.fittracker.fittracker.dataprovider.Request.productRequest;
import static com.fittracker.fittracker.dataprovider.Response.productResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import com.fittracker.fittracker.exception.ErrorResponse;
import com.fittracker.fittracker.repository.ProductRepository;
import com.fittracker.fittracker.response.ProductResponse;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;

public class ProductControllerIntegrationTest extends BaseIntegrationTest {

    private static final String ENDPOINT = "/api/v1/product";

    @Autowired
    private ProductRepository productRepository;

    @Override
    protected List<HttpMethod> getProtectedHttpMethods() {
        return List.of(GET, POST);
    }

    @Override
    protected String getEndpoint() {
        return ENDPOINT;
    }

    @BeforeEach
    void beforeEach() {
        productRepository.deleteAll();
    }

    @Nested
    class Post {

        @Test
        void givenValidPostRequest_shouldSaveAndReturnProduct() throws Exception {
            var expectedResponse = productResponse();
            var expectedProduct = product();

            var response = makeRequestWithBody(POST, productRequest(), CREATED, ProductResponse.class);

            assertThat(response).usingRecursiveComparison().ignoringFields("id").isEqualTo(expectedResponse);
            var allProducts = productRepository.findAll();
            assertThat(allProducts).hasSize(1);
            assertThat(allProducts.iterator().next()).usingRecursiveComparison().ignoringFields("id", "updatedAt")
                .isEqualTo(expectedProduct);
        }


    }

    @Nested
    class Get {

        @Test
        void givenValidGetRequestWithExistingId_shouldReturnProduct() throws Exception {
            productRepository.save(product());

            UUID productUuid = productRepository.findAll().iterator().next().getId();
            var expectedResponse = productResponse();
            var response = makeRequest(ENDPOINT + "/" + productUuid, GET, OK, ProductResponse.class);

            assertThat(response).usingRecursiveComparison().ignoringFields("id").isEqualTo(expectedResponse);
        }

        @Test
        void givenValidGetRequestWithNonexistentId_shouldReturnError() throws Exception {
            var expectedResponse = new ErrorResponse(null,
                "Product not found for id: 56d546e0-9fc7-4477-b418-524eee411524");

            var response = makeRequest(ENDPOINT + "/" + "56d546e0-9fc7-4477-b418-524eee411524", GET, NOT_FOUND,
                ErrorResponse.class);

            assertThat(response).isEqualTo(expectedResponse);
        }

    }

}
