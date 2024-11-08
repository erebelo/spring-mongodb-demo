package com.erebelo.springmongodbdemo.context.converter;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public enum LocalDateReadingConverter implements Converter<Date, LocalDate> {

    INSTANCE;

    @Override
    public LocalDate convert(Date source) {
        return LocalDate.ofInstant(source.toInstant(), ZoneOffset.UTC);
    }
}
