package com.erebelo.springmongodbdemo.context.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

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

        assertThat(converter).isNotNull();
        EnumCodeValueType convertedValue = converter.convert(document);
        assertThat(convertedValue).isEqualTo(TestEnum.VALUE_1);
    }

    @Test
    void testConvertDocumentToANonEnumTypeThrowsIllegalStateException() {
        EnumCodeValueTypeReadingConverter factory = new EnumCodeValueTypeReadingConverter();
        Converter<Document, EnumCodeValueType> converter = factory.getConverter(EnumCodeValueType.class);

        Document document = new Document("code", "code-1");

        assertThat(converter).isNotNull();
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> converter.convert(document))
                .withMessage("The targetType [EnumCodeValueType] have to be an enum.");
    }

    @Test
    void testConvertNonValidDocumentThrowsIllegalStateException() {
        EnumCodeValueTypeReadingConverter factory = new EnumCodeValueTypeReadingConverter();
        Converter<Document, TestEnum> converter = factory.getConverter(TestEnum.class);

        Document document = new Document("code", "code-invalid");

        assertThat(converter).isNotNull();
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> converter.convert(document))
                .withMessage("The value [code-invalid] doesn't match with any instance of the enum [TestEnum]");
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
