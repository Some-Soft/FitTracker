package com.fittracker.fittracker.dataprovider;

import static org.assertj.core.api.Assertions.assertThat;

public class TestHelper {

    public static void compareUpTo(Object result, Object expected, String... ignoredFields) {
        assertThat(result).usingRecursiveComparison().ignoringFields(ignoredFields).isEqualTo(expected);
    }

}
