package com.erebelo.springmongodbdemo.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public ObjectMapper objectMapper() {
        var objectMapper = new ObjectMapper();

        // Register JavaTimeModule for LocalDate serialization/deserialization
        objectMapper.registerModule(new JavaTimeModule());

        // Configure the date format for LocalDate
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // Set a custom date format using SimpleDateFormat
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

        return objectMapper;
    }
}
