package com.erebelo.springmongodbdemo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringMongoDBDemoApplicationTest {

    @Test
    void contextLoads() {
        // This test simply checks if the Spring context loads successfully
        // If it doesn't, it will throw an exception
    }

    @Test
    void mainRunsSuccessfully() {
        SpringMongoDBDemoApplication.main(new String[]{});
    }
}
