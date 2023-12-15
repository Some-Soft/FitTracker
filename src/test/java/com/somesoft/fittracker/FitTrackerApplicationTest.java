package com.somesoft.fittracker;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@Disabled("Doesn't work without database running, to be fixed later")
class FitTrackerApplicationTest {

    @Test
    void contextLoads() {
    }

}
