package com.erebelo.springmongodbdemo.context.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import org.junit.jupiter.api.Test;

class LocalDateWritingConverterTest {

    @Test
    void testConvertLocalDateToDate() {
        Date convertedDate = new LocalDateWritingConverter().convert(LocalDate.of(2024, 11, 8));

        assertThat(convertedDate.toInstant())
                .isEqualTo(LocalDate.of(2024, 11, 8).atStartOfDay().toInstant(ZoneOffset.UTC));
    }
}
