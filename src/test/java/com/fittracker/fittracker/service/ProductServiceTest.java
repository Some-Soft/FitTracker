package com.fittracker.fittracker.service;

import static com.fittracker.fittracker.dataprovider.Entity.product;
import static com.fittracker.fittracker.dataprovider.Request.productRequest;
import static com.fittracker.fittracker.dataprovider.Response.productResponse;
import static java.util.Optional.empty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.fittracker.fittracker.entity.Product;
import com.fittracker.fittracker.exception.ProductNotFoundException;
import com.fittracker.fittracker.repository.ProductRepository;
import com.fittracker.fittracker.security.SecurityHelper;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    private static MockedStatic<SecurityHelper> mockedStatic;
    private static final UUID TEST_PRODUCT_UUID = UUID.fromString("4eea531d-264b-4330-97de-63792a781947");
    private static final UUID TEST_USER_UUID = UUID.fromString("948cc727-68e5-455c-ab6d-942e585bde0d");
    @Captor
    private ArgumentCaptor<Product> productCaptor;
    @Mock
    private ProductRepository productRepository;
    @InjectMocks
    private ProductService productService;

    @BeforeAll
    static void beforeAll() {
        mockedStatic = mockStatic(SecurityHelper.class);
        mockedStatic.when(SecurityHelper::getUserId).thenReturn(TEST_USER_UUID);
    }

    @AfterAll
    static void AfterAll() {
        mockedStatic.close();
    }

    @Nested
    class FindById {

        @Test
        void givenIdFound_shouldReturnProductResponse() {
            when(productRepository.findByIdAndUserId(any(), any())).thenReturn(
                Optional.of(product()));

            var result = productService.findById(TEST_PRODUCT_UUID);

            assertThat(result).isEqualTo(productResponse());
            verify(productRepository).findByIdAndUserId(TEST_PRODUCT_UUID, TEST_USER_UUID);
            verifyNoMoreInteractions(productRepository);
        }

        @Test
        void givenNoIdFound_shouldThrowProductNotFoundException() {
            when(productRepository.findByIdAndUserId(any(), any())).thenReturn(empty());

            assertThatThrownBy(() -> productService.findById(TEST_PRODUCT_UUID))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product not found for id: 4eea531d-264b-4330-97de-63792a781947");

            verify(productRepository).findByIdAndUserId(TEST_PRODUCT_UUID, TEST_USER_UUID);
            verifyNoMoreInteractions(productRepository);
        }
    }

    @Test
    void save_givenProductRequest_shouldSaveAndReturnProductResponse() {
        when(productRepository.save(any())).thenReturn(product());

        var result = productService.save(productRequest());

        assertThat(result).isEqualTo(productResponse());
        verify(productRepository).save(productCaptor.capture());

        assertThat(productCaptor.getValue()).usingRecursiveComparison().ignoringFields("id", "updatedAt")
            .isEqualTo(product());
        verifyNoMoreInteractions(productRepository);
    }

}