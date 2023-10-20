package com.erebelo.springmongodbdemo.context.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@ReadingConverter
public enum StringToLocalDateConverter implements Converter<String, LocalDate> {

    INSTANCE;

    @Override
    public LocalDate convert(String source) {
        return LocalDate.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}

