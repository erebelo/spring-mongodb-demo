package com.erebelo.springmongodbdemo.context.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.erebelo.springmongodbdemo.domain.enumeration.type.EnumCodeValueType;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;

class EnumCodeValueTypeReadingConverterTest {

    @Test
    void testConvertDocumentToEnumCodeValueType() {
        EnumCodeValueTypeReadingConverter factory = new EnumCodeValueTypeReadingConverter();
        Converter<Document, TestEnum> converter = factory.getConverter(TestEnum.class);
        Document document = new Document("code", "value-1");

        EnumCodeValueType convertedValue = converter.convert(document);

        assertNotNull(converter);
        assertEquals(TestEnum.VALUE_1, convertedValue);
    }

    @Test
    void testConvertDocumentToANonEnumTypeThrowsIllegalStateException() {
        EnumCodeValueTypeReadingConverter factory = new EnumCodeValueTypeReadingConverter();
        Converter<Document, EnumCodeValueType> converter = factory.getConverter(EnumCodeValueType.class);
        Document document = new Document("code", "code-1");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> converter.convert(document));

        assertNotNull(converter);
        assertEquals("The targetType [EnumCodeValueType] have to be an enum.", exception.getMessage());
    }

    @Test
    void testConvertNonValidDocumentThrowsIllegalStateException() {
        EnumCodeValueTypeReadingConverter factory = new EnumCodeValueTypeReadingConverter();
        Converter<Document, TestEnum> converter = factory.getConverter(TestEnum.class);
        Document document = new Document("code", "code-invalid");

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> converter.convert(document));

        assertNotNull(converter);
        assertEquals("The value [code-invalid] doesn't match with any instance of the enum [TestEnum]",
                exception.getMessage());
    }

    enum TestEnum implements EnumCodeValueType {
        VALUE_1("code-1", "value-1");

        private final String code;
        private final String value;

        TestEnum(String code, String value) {
            this.code = code;
            this.value = value;
        }

        @Override
        public String getCode() {
            return code;
        }

        @Override
        public String getValue() {
            return value;
        }
    }
}
