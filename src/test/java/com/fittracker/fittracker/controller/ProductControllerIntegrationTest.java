package com.fittracker.fittracker.controller;

import static com.fittracker.fittracker.dataprovider.Entity.product;
import static com.fittracker.fittracker.dataprovider.Request.productRequest;
import static com.fittracker.fittracker.dataprovider.Request.productRequestWithKcal;
import static com.fittracker.fittracker.dataprovider.Response.productResponse;
import static com.fittracker.fittracker.dataprovider.Response.productResponseWithKcal;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.OK;

import com.fittracker.fittracker.entity.Product;
import com.fittracker.fittracker.exception.ErrorResponse;
import com.fittracker.fittracker.repository.ProductRepository;
import com.fittracker.fittracker.response.ProductResponse;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

public class ProductControllerIntegrationTest extends BaseIntegrationTest {

    private static final String ENDPOINT = "/api/v1/product";

    @Autowired
    private ProductRepository productRepository;

    @Override
    protected List<HttpMethod> getProtectedHttpMethods() {
        return List.of(GET, POST, PUT);
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
            assertThat(allProducts.iterator().next()).usingRecursiveComparison()
                .ignoringFields("id", "updatedAt")
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
            var expectedResponse = ErrorResponse.withMessage(
                "Product not found for id: 56d546e0-9fc7-4477-b418-524eee411524");

            var response = makeRequest(ENDPOINT + "/" + "56d546e0-9fc7-4477-b418-524eee411524", GET, NOT_FOUND,
                ErrorResponse.class);

            assertThat(response).isEqualTo(expectedResponse);
        }


    }

    @Nested
    class Put {

        private UUID id;

        @BeforeEach
        void beforeEach() {
            id = productRepository.save(product()).getId();
        }

        @Test
        void givenValidPutRequest_shouldUpdateAndReturnProductResponse() throws Exception {
            var expectedResponse = productResponseWithKcal(300);

            var response = makeRequestWithBodyAndPathVariable(id.toString(), PUT, productRequestWithKcal(300), OK,
                ProductResponse.class);

            assertThat(response).usingRecursiveComparison().ignoringFields("id").isEqualTo(expectedResponse);
            assertThat(response.id()).isEqualTo(id);
            var dbProducts = productRepository.findAll();
            assertThat(dbProducts).usingRecursiveFieldByFieldElementComparatorIgnoringFields("updatedAt")
                .containsExactlyInAnyOrder(
                    new Product(id, 1, "bread", 300, 58, 8, 0,
                        UUID.fromString("948cc727-68e5-455c-ab6d-942e585bde0d"), null, true),
                    new Product(id, 0, "bread", 245, 58, 8, 0,
                        UUID.fromString("948cc727-68e5-455c-ab6d-942e585bde0d"),
                        LocalDateTime.of(2023, 10, 10, 4, 20), false));
        }

        @Test
        void givenValidPutRequestWithNonExistentProductId_shouldReturnError() throws Exception {
            var expectedResponse = ErrorResponse.withMessage(
                "Product not found for id: d854127f-bc8d-4230-bcd2-aeae44760dda");

            var response = makeRequestWithBodyAndPathVariable("d854127f-bc8d-4230-bcd2-aeae44760dda", PUT,
                productRequestWithKcal(300), NOT_FOUND,
                ErrorResponse.class);

            assertThat(response).isEqualTo(expectedResponse);
        }

        @Test
        void givenValidPutRequestWithNoChanges_shouldReturnError() throws Exception {
            String id = productRepository.findAll().iterator().next().getId().toString();
            var expectedResponse = ErrorResponse.withMessage("Updated product must differ");

            var response = makeRequestWithBodyAndPathVariable(id, PUT, productRequest(), BAD_REQUEST,
                ErrorResponse.class);

            assertThat(response).isEqualTo(expectedResponse);
        }
    }

    private Product findLatestInsertedProduct(Iterable<Product> products) {
        return StreamSupport.stream(products.spliterator(), false)
            .max(Comparator.comparing(Product::getUpdatedAt))
            .orElseThrow(() -> new RuntimeException("Products are empty"));
    }

    private <T> T makeRequestWithBodyAndPathVariable(String pathVariable, HttpMethod httpMethod, Object requestBody,
        HttpStatus expectedStatus, Class<T> responseClass) throws Exception {
        return makeRequestWithBody(format("%s/%s", getEndpoint(), pathVariable), httpMethod, requestBody,
            expectedStatus, responseClass);
    }

}
