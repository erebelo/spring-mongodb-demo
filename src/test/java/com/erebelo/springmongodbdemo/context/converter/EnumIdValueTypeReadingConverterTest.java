package com.erebelo.springmongodbdemo.context.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import com.erebelo.springmongodbdemo.domain.enumeration.type.EnumIdValueType;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;

class EnumIdValueTypeReadingConverterTest {

    @Test
    void testConvertDocumentToEnumIdValueType() {
        EnumIdValueTypeReadingConverter factory = new EnumIdValueTypeReadingConverter();
        Converter<Document, TestEnum> converter = factory.getConverter(TestEnum.class);

        Document document = new Document("value", "value-1");

        assertThat(converter).isNotNull();
        EnumIdValueType convertedValue = converter.convert(document);
        assertThat(convertedValue).isEqualTo(TestEnum.VALUE_1);
    }

    @Test
    void testConvertDocumentToANonEnumTypeThrowsIllegalStateException() {
        EnumIdValueTypeReadingConverter factory = new EnumIdValueTypeReadingConverter();
        Converter<Document, EnumIdValueType> converter = factory.getConverter(EnumIdValueType.class);

        Document document = new Document("value", "value-1");

        assertThat(converter).isNotNull();
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> converter.convert(document))
                .withMessage("The targetType [EnumIdValueType] was expected to be an enum.");
    }

    @Test
    void testConvertNonValidDocumentThrowsIllegalStateException() {
        EnumIdValueTypeReadingConverter factory = new EnumIdValueTypeReadingConverter();
        Converter<Document, TestEnum> converter = factory.getConverter(TestEnum.class);

        Document document = new Document("value", "value-invalid");

        assertThat(converter).isNotNull();
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> converter.convert(document))
                .withMessage("The value [value-invalid] doesn't match any instance of enum [TestEnum]");
    }

    enum TestEnum implements EnumIdValueType {
        VALUE_1(1, "value-1");

        private final Integer id;
        private final String value;

        TestEnum(Integer id, String value) {
            this.id = id;
            this.value = value;
        }

        @Override
        public Integer getId() {
            return id;
        }

        @Override
        public String getValue() {
            return value;
        }
    }
}
