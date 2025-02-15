package com.erebelo.springmongodbdemo.context.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.bson.Document;
import org.junit.jupiter.api.Test;

class EnumCodeValueTypeWritingConverterTest {

    @Test
    void testConvertEnumCodeValueTypeToDocument() {
        EnumCodeValueTypeWritingConverter converter = new EnumCodeValueTypeWritingConverter();
        EnumCodeValueTypeReadingConverterTest.TestEnum source = EnumCodeValueTypeReadingConverterTest.TestEnum.VALUE_1;

        Document convertedDocument = converter.convert(source);

        assertNotNull(convertedDocument);
        assertEquals("code-1", convertedDocument.getString("code"));
        assertEquals("value-1", convertedDocument.getString("value"));
    }
}
