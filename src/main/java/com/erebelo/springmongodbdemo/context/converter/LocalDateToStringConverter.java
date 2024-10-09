package com.erebelo.springmongodbdemo.context.converter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public enum LocalDateToStringConverter implements Converter<LocalDate, String> {

    INSTANCE;

    @Override
    public String convert(LocalDate source) {
        return source.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
