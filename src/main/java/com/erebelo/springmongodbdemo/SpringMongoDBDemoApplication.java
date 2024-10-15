package com.erebelo.springmongodbdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@EnableMongoAuditing
@SpringBootApplication(scanBasePackages = {"com.erebelo"})
public class SpringMongoDBDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringMongoDBDemoApplication.class, args);
    }
}
