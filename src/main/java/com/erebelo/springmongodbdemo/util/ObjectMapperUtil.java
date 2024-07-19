package com.erebelo.springmongodbdemo.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.SimpleDateFormat;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ObjectMapperUtil {

    public static final ObjectMapper objectMapper;

    private static final String ISO_LOCAL_DATE_FORMAT = "yyyy-MM-dd";

    static {
        objectMapper = new ObjectMapper();

        // Register JavaTimeModule for LocalDate serialization/deserialization
        objectMapper.registerModule(new JavaTimeModule());

        // Set the ObjectMapper to include only non-null properties during serialization
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

        // Configure the date format for LocalDate
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Set a custom date format using SimpleDateFormat
        objectMapper.setDateFormat(new SimpleDateFormat(ISO_LOCAL_DATE_FORMAT));
    }
}
