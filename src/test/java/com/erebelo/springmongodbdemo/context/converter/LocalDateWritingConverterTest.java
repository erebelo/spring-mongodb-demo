package com.erebelo.springmongodbdemo.context.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import org.junit.jupiter.api.Test;

class LocalDateWritingConverterTest {

    @Test
    void testConvertLocalDateToDate() {
        Date convertedDate = new LocalDateWritingConverter().convert(LocalDate.of(2024, 11, 8));

        assertEquals(LocalDate.of(2024, 11, 8).atStartOfDay().toInstant(ZoneOffset.UTC), convertedDate.toInstant());
    }
}
