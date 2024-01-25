package com.somesoft.fittracker.service;

import static com.somesoft.fittracker.dataprovider.Entity.product;
import static com.somesoft.fittracker.dataprovider.Entity.productWithActive;
import static com.somesoft.fittracker.dataprovider.Entity.productWithKcal;
import static com.somesoft.fittracker.dataprovider.Request.productRequest;
import static com.somesoft.fittracker.dataprovider.Request.productRequestWithKcal;
import static com.somesoft.fittracker.dataprovider.Response.productResponse;
import static com.somesoft.fittracker.dataprovider.Response.productResponseWithKcal;
import static com.somesoft.fittracker.dataprovider.TestHelper.assertEqualRecursiveIgnoring;
import static java.util.Optional.empty;
import static java.util.Optional.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.somesoft.fittracker.entity.Product;
import com.somesoft.fittracker.exception.ProductNotFoundException;
import com.somesoft.fittracker.exception.ProductNotUpdatedException;
import com.somesoft.fittracker.exception.ProductPersistenceException;
import com.somesoft.fittracker.repository.ProductRepository;
import com.somesoft.fittracker.security.SecurityHelper;
import java.util.UUID;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
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
    private static final UUID TEST_PRODUCT_UUID = UUID.fromString("382cf280-8b7a-11ee-b9d1-0242ac120002");
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

    @AfterEach
    public void afterEach() {
        verifyNoMoreInteractions(productRepository);
    }

    @Nested
    class FindById {

        @Test
        void givenIdFound_shouldReturnProductResponse() {

            when(productRepository.findByIdAndUserIdAndActiveIsTrue(any(), any())).thenReturn(of(product()));

            var result = productService.findById(TEST_PRODUCT_UUID);

            assertThat(result).isEqualTo(productResponse());
            verify(productRepository).findByIdAndUserIdAndActiveIsTrue(TEST_PRODUCT_UUID, TEST_USER_UUID);
        }

        @Test
        void givenNoIdFound_shouldThrowProductNotFoundException() {
            when(productRepository.findByIdAndUserIdAndActiveIsTrue(any(), any())).thenReturn(empty());

            assertThatThrownBy(() -> productService.findById(TEST_PRODUCT_UUID))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product not found for id: 382cf280-8b7a-11ee-b9d1-0242ac120002");

            verify(productRepository).findByIdAndUserIdAndActiveIsTrue(TEST_PRODUCT_UUID, TEST_USER_UUID);
        }
    }

    @Test
    void save_givenProductRequest_shouldSaveAndReturnProductResponse() {
        when(productRepository.save(any())).thenReturn(product());

        var result = productService.save(productRequest());

        assertThat(result).isEqualTo(productResponse());
        verify(productRepository).save(productCaptor.capture());

        assertEqualRecursiveIgnoring(productCaptor.getValue(), product(), "id", "updatedAt", "active");
    }

    @Nested
    class Update {

        @Test
        void givenProductFound_shouldUpdateAndReturnUpdatedProduct() {
            when(productRepository.findByIdAndUserIdAndActiveIsTrue(any(), any())).thenReturn(of(product()));
            when(productRepository.saveNew(any())).thenReturn(of(productWithKcal(300)));

            var response = productService.update(TEST_PRODUCT_UUID, productRequestWithKcal(300));
            var expectedResponse = productResponseWithKcal(300);
            var newProduct = productWithKcal(300);
            newProduct.setVersion(1);
            newProduct.setActive(false);

            assertThat(response).isEqualTo(expectedResponse);
            verify(productRepository).findByIdAndUserIdAndActiveIsTrue(TEST_PRODUCT_UUID, TEST_USER_UUID);
            verify(productRepository).save(productCaptor.capture());
            assertEqualRecursiveIgnoring(productCaptor.getValue(), productWithActive(false));
            verify(productRepository).saveNew(productCaptor.capture());
            assertEqualRecursiveIgnoring(productCaptor.getValue(), newProduct, "updatedAt");
        }

        @Test
        void givenNoProductFound_shouldThrowProductNotFoundException() {
            when(productRepository.findByIdAndUserIdAndActiveIsTrue(any(), any()))
                .thenReturn(empty());

            assertThatThrownBy(() -> productService.update(TEST_PRODUCT_UUID, productRequestWithKcal(300)))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product not found for id: 382cf280-8b7a-11ee-b9d1-0242ac120002");

            verify(productRepository).findByIdAndUserIdAndActiveIsTrue(TEST_PRODUCT_UUID, TEST_USER_UUID);
        }

        @Test
        void givenRequestWithSameDataAsActiveProduct_shouldThrowProductNotUpdatedException() {
            when(productRepository.findByIdAndUserIdAndActiveIsTrue(any(), any()))
                .thenReturn(of(product()));

            assertThatThrownBy(() -> productService.update(TEST_PRODUCT_UUID, productRequest()))
                .isInstanceOf(ProductNotUpdatedException.class)
                .hasMessageContaining("Updated product must differ");

            verify(productRepository).findByIdAndUserIdAndActiveIsTrue(TEST_PRODUCT_UUID, TEST_USER_UUID);
        }

        @Test
        void givenEmptyEcho_shouldThrowTransactionSystemException() {
            when(productRepository.findByIdAndUserIdAndActiveIsTrue(any(), any()))
                .thenReturn(of(product()));
            when(productRepository.saveNew(any())).thenReturn(empty());

            assertThatThrownBy(() -> productService.update(TEST_PRODUCT_UUID, productRequestWithKcal(300)))
                .isInstanceOf(ProductPersistenceException.class)
                .hasMessageContaining(
                    "Failed to persist product: Product{id=382cf280-8b7a-11ee-b9d1-0242ac120002, version=1, name='bread', "
                        + "kcal=300, carbs=58, protein=8, fat=0, userId=948cc727-68e5-455c-ab6d-942e585bde0d, updatedAt=null, active=false}");

            verify(productRepository).findByIdAndUserIdAndActiveIsTrue(TEST_PRODUCT_UUID, TEST_USER_UUID);
            verify(productRepository).save(any());
            verify(productRepository).saveNew(any());
        }
    }

    @Nested
    class Delete {

        @Test
        void givenActiveIdFound_shouldSetActiveToFalseAndSave() {
            when(productRepository.findByIdAndUserIdAndActiveIsTrue(any(), any()))
                .thenReturn(of(product()));

            productService.delete(TEST_PRODUCT_UUID);

            verify(productRepository).findByIdAndUserIdAndActiveIsTrue(TEST_PRODUCT_UUID, TEST_USER_UUID);
            verify(productRepository).save(productCaptor.capture());
            assertEqualRecursiveIgnoring(productCaptor.getValue(), productWithActive(false));
        }

        @Test
        void givenNoActiveIdFound_shouldReturnError() {
            when(productRepository.findByIdAndUserIdAndActiveIsTrue(any(), any()))
                .thenReturn(empty());

            assertThatThrownBy(() -> productService.delete(TEST_PRODUCT_UUID))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("Product not found for id: 382cf280-8b7a-11ee-b9d1-0242ac120002");

            verify(productRepository).findByIdAndUserIdAndActiveIsTrue(TEST_PRODUCT_UUID, TEST_USER_UUID);
        }
    }


}