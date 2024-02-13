package com.somesoft.fittracker.controller;

import static com.somesoft.fittracker.dataprovider.Request.productRequest;
import static com.somesoft.fittracker.dataprovider.Request.productRequestWithCarbs;
import static com.somesoft.fittracker.dataprovider.Request.productRequestWithFat;
import static com.somesoft.fittracker.dataprovider.Request.productRequestWithKcal;
import static com.somesoft.fittracker.dataprovider.Request.productRequestWithName;
import static com.somesoft.fittracker.dataprovider.Request.productRequestWithProtein;
import static java.lang.String.format;
import static org.junit.jupiter.params.provider.Arguments.of;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.context.annotation.FilterType.ASSIGNABLE_TYPE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.somesoft.fittracker.exception.ErrorResponseMapper;
import com.somesoft.fittracker.request.ProductRequest;
import com.somesoft.fittracker.security.JwtAuthenticationFilter;
import com.somesoft.fittracker.service.ProductService;
import java.net.URI;
import java.util.stream.Stream;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.verification.VerificationMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;

@AutoConfigureMockMvc(addFilters = false)
@Import(ErrorResponseMapper.class)
@WebMvcTest(controllers = ProductController.class, excludeFilters = {
    @ComponentScan.Filter(type = ASSIGNABLE_TYPE, value = JwtAuthenticationFilter.class)})
public class ProductControllerValidationTest {

    @MockBean
    private ProductService productService;
    @Autowired
    private MockMvc mockMvc;

    private static final String ENDPOINT = "/api/v1/product";

    private static final ObjectMapper mapper = new ObjectMapper();

    @Nested
    class Get {

        @ParameterizedTest
        @MethodSource("getProductRequestTestDataProvider")
        void givenGetUriRequest_shouldReturnResponse(String params, HttpStatus responseStatus, String responseBody,
            VerificationMode numberOfServiceInvocations) throws Exception {
            mockMvc
                .perform(get(URI.create(ENDPOINT + params)))
                .andExpect(status().is(responseStatus.value()))
                .andExpect(content().string(responseBody));

            verify(productService, numberOfServiceInvocations).findById(any());
        }

        private static Stream<Arguments> getProductRequestTestDataProvider() {
            return Stream.of(
                of("/notUUID", BAD_REQUEST, errorResponseString("id", "Invalid data type"), never()),
                of("/57a407b2-4a54-4ae7-b93c-9e3227af26bg", BAD_REQUEST,
                    errorResponseString("id", "Invalid data type"), never()),
                of("/382cf280-8b7a-11ee-b9d1-0242ac120002", OK, "", times(1)),
                of("/0023cfab-f91d-4428-8e76-dfade3b9c3fc", OK, "", times(1))
            );
        }

    }

    @Nested
    class Post {

        @Test
        void givenProductRequestWithNameLongerThan64chars_shouldReturnErrorMessage() throws Exception {
            mockMvc
                .perform(post(URI.create(ENDPOINT))
                    .contentType(APPLICATION_JSON)
                    .content(mapper.writeValueAsString(productRequestWithName("a".repeat(65)))))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorResponseString("name", "Product name is limited to 64 characters")));

            verifyNoInteractions(productService);
        }

        @ParameterizedTest
        @MethodSource("postRequestWithBlankNames")
        void givenProductRequestWithBlankName_shouldReturnErrorMessage(ProductRequest productRequest)
            throws Exception {
            mockMvc
                .perform(post(URI.create(ENDPOINT))
                    .contentType(APPLICATION_JSON)
                    .content(mapper.writeValueAsString(productRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorResponseString("name", "Product name must not be blank")));

            verifyNoInteractions(productService);
        }

        private static Stream<Arguments> postRequestWithBlankNames() {
            return Stream.of(
                Arguments.of(productRequestWithName(null)),
                Arguments.of(productRequestWithName("")),
                Arguments.of(productRequestWithName("   "))
            );
        }

        @ParameterizedTest
        @MethodSource("postRequestWithNegativeValues")
        void givenPostRequestWithNegativeValue_shouldReturnError(ProductRequest productRequest, String field)
            throws Exception {
            mockMvc.perform(post(URI.create(ENDPOINT))
                    .contentType(APPLICATION_JSON)
                    .content(mapper.writeValueAsString(productRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorResponseString(field, "Cannot be less than 0")));

            verifyNoInteractions(productService);
        }

        private static Stream<Arguments> postRequestWithNegativeValues() {
            return Stream.of(
                of(productRequestWithKcal(-1), "kcal"),
                of(productRequestWithCarbs(-1), "carbs"),
                of(productRequestWithProtein(-1), "protein"),
                of(productRequestWithFat(-1), "fat")
            );
        }

        @ParameterizedTest
        @MethodSource("postRequestWithValuesOver9999")
        void givenPostRequestWithValueOver9999_shouldReturnError(ProductRequest productRequest, String field)
            throws Exception {
            mockMvc
                .perform(post(URI.create(ENDPOINT))
                    .contentType(APPLICATION_JSON)
                    .content(mapper.writeValueAsString(productRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorResponseString(field, "Cannot exceed 9999")));

            verifyNoInteractions(productService);
        }

        private static Stream<Arguments> postRequestWithValuesOver9999() {
            return Stream.of(
                of(productRequestWithKcal(10_000), "kcal"),
                of(productRequestWithCarbs(10_000), "carbs"),
                of(productRequestWithProtein(10_000), "protein"),
                of(productRequestWithFat(10_000), "fat")
            );
        }

        @Test
        void givenValidPostRequest_shouldReturnCreated() throws Exception {
            mockMvc
                .perform(post(URI.create(ENDPOINT))
                    .contentType(APPLICATION_JSON)
                    .content(mapper.writeValueAsString(productRequest())))
                .andExpect(status().isCreated())
                .andExpect(content().string(""));

            verify(productService).save(any());
        }
    }

    @Nested
    class Put {

        private final static String TEST_PATH_VARIABLE = "/382cf280-8b7a-11ee-b9d1-0242ac120002";

        @ParameterizedTest
        @MethodSource("putRequestWithBlankNames")
        void givenProductRequestWithBlankName_shouldReturnErrorMessage(ProductRequest productRequest)
            throws Exception {
            mockMvc
                .perform(put(URI.create(ENDPOINT + TEST_PATH_VARIABLE))
                    .contentType(APPLICATION_JSON)
                    .content(mapper.writeValueAsString(productRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorResponseString("name", "Product name must not be blank")));

            verifyNoInteractions(productService);
        }

        private static Stream<Arguments> putRequestWithBlankNames() {
            return Stream.of(
                Arguments.of(productRequestWithName(null)),
                Arguments.of(productRequestWithName("")),
                Arguments.of(productRequestWithName("   "))
            );
        }

        @ParameterizedTest
        @MethodSource("putRequestWithNegativeValues")
        void givenPutRequestWithNegativeValue_shouldReturnError(ProductRequest productRequest, String field)
            throws Exception {
            mockMvc.perform(put(URI.create(ENDPOINT + TEST_PATH_VARIABLE))
                    .contentType(APPLICATION_JSON)
                    .content(mapper.writeValueAsString(productRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorResponseString(field, "Cannot be less than 0")));

            verifyNoInteractions(productService);
        }

        private static Stream<Arguments> putRequestWithNegativeValues() {
            return Stream.of(
                of(productRequestWithKcal(-1), "kcal"),
                of(productRequestWithCarbs(-1), "carbs"),
                of(productRequestWithProtein(-1), "protein"),
                of(productRequestWithFat(-1), "fat")
            );
        }

        @ParameterizedTest
        @MethodSource("putRequestWithValuesOver9999")
        void givenPutRequestWithValueOver9999_shouldReturnError(ProductRequest productRequest, String field)
            throws Exception {
            mockMvc
                .perform(put(URI.create(ENDPOINT + TEST_PATH_VARIABLE))
                    .contentType(APPLICATION_JSON)
                    .content(mapper.writeValueAsString(productRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(errorResponseString(field, "Cannot exceed 9999")));

            verifyNoInteractions(productService);
        }

        private static Stream<Arguments> putRequestWithValuesOver9999() {
            return Stream.of(
                of(productRequestWithKcal(10_000), "kcal"),
                of(productRequestWithCarbs(10_000), "carbs"),
                of(productRequestWithProtein(10_000), "protein"),
                of(productRequestWithFat(10_000), "fat")
            );
        }

        @Test
        void givenValidPutRequest_shouldReturnOk() throws Exception {
            mockMvc
                .perform(put(URI.create(ENDPOINT + TEST_PATH_VARIABLE))
                    .contentType(APPLICATION_JSON)
                    .content(mapper.writeValueAsString(productRequest())))
                .andExpect(status().isOk())
                .andExpect(content().string(""));

            verify(productService).update(any(), any());
        }
    }

    @Nested
    class Delete {

        @ParameterizedTest
        @MethodSource("deleteProductRequestTestDataProvider")
        void givenDeleteUriRequest_shouldReturnResponse(String params, HttpStatus responseStatus, String responseBody,
            VerificationMode numberOfServiceInvocations) throws Exception {
            mockMvc
                .perform(delete(URI.create(ENDPOINT + params)))
                .andExpect(status().is(responseStatus.value()))
                .andExpect(content().string(responseBody));

            verify(productService, numberOfServiceInvocations).delete(any());
        }

        private static Stream<Arguments> deleteProductRequestTestDataProvider() {
            return Stream.of(
                of("/notUUID", BAD_REQUEST, errorResponseString("id", "Invalid data type"), never()),
                of("/57a407b2-4a54-4ae7-b93c-9e3227af26bg", BAD_REQUEST,
                    errorResponseString("id", "Invalid data type"), never()),
                of("/382cf280-8b7a-11ee-b9d1-0242ac120002", NO_CONTENT, "", times(1)),
                of("/0023cfab-f91d-4428-8e76-dfade3b9c3fc", NO_CONTENT, "", times(1))
            );
        }
    }

    private static String errorResponseString(String field, String message) {
        return format("{\"field\":\"%s\",\"message\":\"%s\"}", field, message);
    }

}
