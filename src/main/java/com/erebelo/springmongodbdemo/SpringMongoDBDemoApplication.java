package com.erebelo.springmongodbdemo;

import jakarta.annotation.PostConstruct;
import java.util.TimeZone;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.erebelo"})
public class SpringMongoDBDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringMongoDBDemoApplication.class, args);
    }

    @PostConstruct
    public void init() {
        /*
         * Set default timezone to São Paulo for the application. Note: Although the
         * application operates in São Paulo timezone, LocalDateTime fields will be
         * converted to UTC when persisted in MongoDB, stored without any offset,
         * keeping MongoDB's default behavior.
         */
        TimeZone.setDefault(TimeZone.getTimeZone("America/Sao_Paulo"));
    }
}
