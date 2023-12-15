package com.somesoft.fittracker.entity;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.somesoft.fittracker.dataprovider.Entity;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ProductTest {

    private final Random random = new Random();

    private Product product;

    @Nested
    class HasEqualData {

        @BeforeEach
        public void beforeEach() {
            product = randomizeNotComparedFields(Entity.product());
        }

        @Test
        void givenProductsOfTheSameNameKcalCarbsProteinAndFat_shouldReturnTrue() {
            assertTrue(product.hasEqualData(Entity.product()));
        }

        @ParameterizedTest
        @MethodSource("unequalDataProvider")
        void givenProductsWithDifferentName_shouldReturnFalse(Product productToCompare) {
            assertFalse(product.hasEqualData(randomizeNotComparedFields(productToCompare)));
        }

        public static Stream<Arguments> unequalDataProvider() {
            return Stream.of(
                Arguments.of(Entity.productWithName("apple")),
                Arguments.of(Entity.productWithKcal(300)),
                Arguments.of(Entity.productWithCarbs(5)),
                Arguments.of(Entity.productWithProtein(70)),
                Arguments.of(Entity.productWithFat(50))
            );
        }

        private Product randomizeNotComparedFields(Product product) {
            product.setId(UUID.randomUUID());
            product.setVersion(random.nextInt());
            product.setUserId(UUID.randomUUID());
            product.setUpdatedAt(randomTimestamp());
            product.setActive(random.nextBoolean());

            return product;
        }

        private LocalDateTime randomTimestamp() {
            return LocalDateTime.now().plusSeconds(random.nextInt());
        }
    }

}