package com.erebelo.springmongodbdemo.context.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class EnumValueTypeWritingConverterTest {

    @Test
    void testConvertEnumValueTypeToString() {
        String convertedValue = new EnumValueTypeWritingConverter().convert(() -> "value");

        assertEquals("value", convertedValue);
    }
}
