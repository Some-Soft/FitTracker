package com.somesoft.fittracker.controller;


import static com.somesoft.fittracker.dataprovider.Entity.product;
import static com.somesoft.fittracker.dataprovider.Entity.productWithActive;
import static com.somesoft.fittracker.dataprovider.Request.productRequest;
import static com.somesoft.fittracker.dataprovider.Request.productRequestWithKcal;
import static com.somesoft.fittracker.dataprovider.Response.productResponse;
import static com.somesoft.fittracker.dataprovider.Response.productResponseWithKcal;
import static com.somesoft.fittracker.dataprovider.TestHelper.assertEqualRecursiveIgnoring;
import static java.lang.String.format;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

import com.somesoft.fittracker.entity.Product;
import com.somesoft.fittracker.exception.ErrorResponse;
import com.somesoft.fittracker.repository.ProductRepository;
import com.somesoft.fittracker.response.ProductResponse;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
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

    private UUID id;

    @BeforeEach
    void beforeEach() {
        productRepository.deleteAll();
        id = productRepository.save(product()).getId();
    }

    @Nested
    class Post {

        @Test
        void givenValidPostRequest_shouldSaveAndReturnProduct() throws Exception {
            var expectedResponse = productResponse();
            var expectedProduct = product();

            var response = makeRequestWithBody(POST, productRequest(), CREATED, ProductResponse.class);

            assertEqualRecursiveIgnoring(response, expectedResponse, "id");
            var allProducts = productRepository.findAll();
            assertThat(allProducts).hasSize(2);
            assertEqualRecursiveIgnoring(allProducts.iterator().next(), expectedProduct, "id", "updatedAt");
        }

    }

    @Nested
    class Get {

        @Test
        void givenGetRequestWithExistingIdAndActiveTrue_shouldReturnActiveProduct() throws Exception {
            makeRequestWithBodyAndPathVariable(id.toString(), PUT, productRequestWithKcal(300), OK,
                ProductResponse.class);

            var expectedResponse = productResponseWithKcal(300);
            var response = makeRequest(ENDPOINT + "/" + id, GET, OK, ProductResponse.class);

            assertEqualRecursiveIgnoring(response, expectedResponse, "id");
        }

        @Test
        void givenGetRequestWithNonexistentId_shouldReturnError() throws Exception {
            var expectedResponse = ErrorResponse.withMessage(
                "Product not found for id: 56d546e0-9fc7-4477-b418-524eee411524");

            var response = makeRequest(ENDPOINT + "/" + "56d546e0-9fc7-4477-b418-524eee411524", GET, NOT_FOUND,
                ErrorResponse.class);

            assertThat(response).isEqualTo(expectedResponse);
        }

        @Test
        void givenGetRequestWithExistingIdAndActiveFalse_shouldReturnError() throws Exception {
            makeRequest(ENDPOINT + "/" + id, DELETE, NO_CONTENT);
            var expectedResponse = ErrorResponse.withMessage("Product not found for id: " + id);

            var response = makeRequest(ENDPOINT + "/" + id, GET, NOT_FOUND,
                ErrorResponse.class);

            assertThat(productRepository.findAll().iterator().next().getId()).isEqualTo(id);
            assertThat(response).isEqualTo(expectedResponse);
        }

    }

    @Nested
    class Put {

        @Test
        void givenValidPutRequest_shouldUpdateAndReturnProductResponse() throws Exception {
            var expectedResponse = productResponseWithKcal(300);

            var response = makeRequestWithBodyAndPathVariable(id.toString(), PUT, productRequestWithKcal(300), OK,
                ProductResponse.class);

            assertEqualRecursiveIgnoring(response, expectedResponse, "id");
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

                productRequestWithKcal(300), NOT_FOUND, ErrorResponse.class);

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

    @Nested
    class Delete {

        @Test
        void givenValidDeleteRequest_shouldSetActiveToFalseAndReturnNoContent() throws Exception {
            var response = makeRequest(ENDPOINT + "/" + id, DELETE, NO_CONTENT);
            var expectedProduct = productWithActive(false);

            assertThat(response).isEmpty();
            var dbProducts = productRepository.findAll();
            assertThat(dbProducts).hasSize(1);
            assertEqualRecursiveIgnoring(dbProducts.iterator().next(), expectedProduct, "id", "updatedAt");
        }

        @Test
        void givenDeleteRequestWithNonexistentId_shouldReturnError() throws Exception {
            var response = makeRequest(ENDPOINT + "/fed4b756-cfc3-4d99-910f-5f27faad59fe", DELETE, NOT_FOUND,
                ErrorResponse.class);
            var expectedResponse = ErrorResponse.withMessage(
                "Product not found for id: fed4b756-cfc3-4d99-910f-5f27faad59fe");

            assertThat(response).isEqualTo(expectedResponse);
        }

        @Test
        void givenDeleteRequestForInactiveProduct_shouldReturnError() throws Exception {
            productRepository.save(product());

            makeRequest(ENDPOINT + "/" + id, DELETE, NO_CONTENT);
            var response = makeRequest(ENDPOINT + "/" + id, DELETE, NOT_FOUND,
                ErrorResponse.class);
            var expectedResponse = ErrorResponse.withMessage("Product not found for id: " + id);

            assertThat(response).isEqualTo(expectedResponse);
        }
    }

    private <T> T makeRequestWithBodyAndPathVariable(String pathVariable, HttpMethod httpMethod, Object requestBody,
        HttpStatus expectedStatus, Class<T> responseClass) throws Exception {
        return makeRequestWithBody(format("%s/%s", getEndpoint(), pathVariable), httpMethod, requestBody,
            expectedStatus, responseClass);
    }

}
