package com.erebelo.springmongodbdemo.context.converter;

import static org.assertj.core.api.Assertions.assertThat;

import org.bson.Document;
import org.junit.jupiter.api.Test;

class EnumIdValueTypeWritingConverterTest {

    @Test
    void testConvertEnumIdValueTypeToDocument() {
        EnumIdValueTypeWritingConverter converter = new EnumIdValueTypeWritingConverter();
        EnumIdValueTypeReadingConverterTest.TestEnum source = EnumIdValueTypeReadingConverterTest.TestEnum.VALUE_1;

        Document convertedDocument = converter.convert(source);

        assertThat(convertedDocument).isNotNull();
        assertThat(convertedDocument.getInteger("id")).isEqualTo(1);
        assertThat(convertedDocument.getString("value")).isEqualTo("value-1");
    }
}
