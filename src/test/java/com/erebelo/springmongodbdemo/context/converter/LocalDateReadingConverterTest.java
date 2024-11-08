package com.erebelo.springmongodbdemo.context.converter;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;
import org.junit.jupiter.api.Test;

class LocalDateReadingConverterTest {

    @Test
    void testConvertDateToLocalDate() {
        Date date = Date.from(LocalDate.of(2024, 11, 8).atStartOfDay().toInstant(ZoneOffset.UTC));

        LocalDate convertedDate = new LocalDateReadingConverter().convert(date);

        assertThat(convertedDate).isEqualTo(LocalDate.of(2024, 11, 8));
    }
}
