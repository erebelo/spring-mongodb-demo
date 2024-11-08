package com.erebelo.springmongodbdemo.context.converter;

import static org.assertj.core.api.Assertions.assertThat;

import org.bson.Document;
import org.junit.jupiter.api.Test;

class EnumCodeValueTypeWritingConverterTest {

    @Test
    void testConvertEnumCodeValueTypeToDocument() {
        EnumCodeValueTypeWritingConverter converter = new EnumCodeValueTypeWritingConverter();
        EnumCodeValueTypeReadingConverterTest.TestEnum source = EnumCodeValueTypeReadingConverterTest.TestEnum.VALUE_1;

        Document convertedDocument = converter.convert(source);

        assertThat(convertedDocument).isNotNull();
        assertThat(convertedDocument.getString("code")).isEqualTo("code-1");
        assertThat(convertedDocument.getString("value")).isEqualTo("value-1");
    }
}
