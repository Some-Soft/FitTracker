package com.fittracker.fittracker.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.time.LocalDateTime;
import java.util.UUID;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class ProductTest {

    private static final UUID TEST_UUID = UUID.fromString("084ce4d3-fbc2-45ff-9425-2f2ce116c512");

    private static final UUID ANOTHER_TEST_UUID = UUID.fromString("eabd0b51-87c4-40f2-a0a7-f242215275e2");
    private static final UUID TEST_USER_UUID = UUID.fromString("a425d08e-f37b-48d9-8acc-9580cd574ffd");

    private static final LocalDateTime TEST_TIMESTAMP = LocalDateTime.of(2023, 10, 10, 4, 20);

    @Nested
    class Equals {

        Product product = new Product(TEST_UUID, 0, "bread", 260, 60, 8, 1, TEST_USER_UUID, TEST_TIMESTAMP, true);
        Product equalProduct = new Product(ANOTHER_TEST_UUID, 0, "bread", 260, 60, 8, 1, TEST_USER_UUID, TEST_TIMESTAMP,
            true);
        Product differentProduct = new Product(TEST_UUID, 0, "bread", 130, 60, 8, 1, TEST_USER_UUID, TEST_TIMESTAMP,
            true);

        @Test
        void givenProductsOfTheSameNameKcalCarbsProteinAndFat_shouldReturnTrue() {
            assertEquals(product, equalProduct);
        }

        @Test
        void givenProductsDifferingOnNameKcalCarbsProteinOrFat_shouldReturnFalse() {
            assertNotEquals(product, differentProduct);
        }
    }

}