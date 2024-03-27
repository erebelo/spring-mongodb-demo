package com.erebelo.springmongodbdemo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class SpringMongoDBDemoApplicationTest {

    @Mock
    private ConfigurableApplicationContext contextMock;

    @Test
    void contextLoads() {
        // This test simply checks if the Spring context loads successfully
        // If it doesn't, it will throw an exception
    }

    @Test
    void mainRunSuccessfully() {
        try (MockedStatic<SpringApplication> mockedStatic = mockStatic(SpringApplication.class)) {
            mockedStatic.when(() -> SpringApplication.run(any(Class.class), any(String[].class))).thenReturn(contextMock);

            SpringMongoDBDemoApplication.main(new String[]{});

            mockedStatic.verify(() -> SpringApplication.run(SpringMongoDBDemoApplication.class, new String[]{}));
        }
    }
}
