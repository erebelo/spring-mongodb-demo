package com.erebelo.springmongodbdemo.context.converter;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EnumValueTypeWritingConverterTest {

    @Test
    void testConvertEnumValueTypeToString() {
        String convertedValue = new EnumValueTypeWritingConverter().convert(() -> "value");

        assertThat(convertedValue).isEqualTo("value");
    }
}
