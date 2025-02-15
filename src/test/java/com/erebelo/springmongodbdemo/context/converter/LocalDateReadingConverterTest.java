package com.erebelo.springmongodbdemo.context.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import org.junit.jupiter.api.Test;

class LocalDateReadingConverterTest {

    @Test
    void testConvertDateToLocalDate() {
        Date date = Date.from(LocalDate.of(2024, 11, 8).atStartOfDay().toInstant(ZoneOffset.UTC));

        LocalDate convertedDate = new LocalDateReadingConverter().convert(date);

        assertEquals(LocalDate.of(2024, 11, 8), convertedDate);
    }
}
