package com.erebelo.springmongodbdemo.context.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.bson.Document;
import org.junit.jupiter.api.Test;

class EnumIdValueTypeWritingConverterTest {

    @Test
    void testConvertEnumIdValueTypeToDocument() {
        EnumIdValueTypeWritingConverter converter = new EnumIdValueTypeWritingConverter();
        EnumIdValueTypeReadingConverterTest.TestEnum source = EnumIdValueTypeReadingConverterTest.TestEnum.VALUE_1;

        Document convertedDocument = converter.convert(source);

        assertNotNull(convertedDocument);
        assertEquals(1, convertedDocument.getInteger("id"));
        assertEquals("value-1", convertedDocument.getString("value"));
    }
}
