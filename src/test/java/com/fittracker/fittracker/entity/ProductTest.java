package com.fittracker.fittracker.entity;

import static com.fittracker.fittracker.dataprovider.Entity.product;
import static com.fittracker.fittracker.dataprovider.Entity.productWithCarbs;
import static com.fittracker.fittracker.dataprovider.Entity.productWithFat;
import static com.fittracker.fittracker.dataprovider.Entity.productWithKcal;
import static com.fittracker.fittracker.dataprovider.Entity.productWithName;
import static com.fittracker.fittracker.dataprovider.Entity.productWithProtein;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.of;

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
            product = randomizeNotComparedFields(product());
        }

        @Test
        void givenProductsOfTheSameNameKcalCarbsProteinAndFat_shouldReturnTrue() {
            assertTrue(product.hasEqualData(product()));
        }

        @ParameterizedTest
        @MethodSource("unequalDataProvider")
        void givenProductsWithDifferentName_shouldReturnFalse(Product productToCompare) {
            assertFalse(product.hasEqualData(randomizeNotComparedFields(productToCompare)));
        }

        public static Stream<Arguments> unequalDataProvider() {
            return Stream.of(
                of(productWithName("apple")),
                of(productWithKcal(300)),
                of(productWithCarbs(5)),
                of(productWithProtein(70)),
                of(productWithFat(50))
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