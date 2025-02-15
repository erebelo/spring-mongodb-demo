package com.erebelo.springmongodbdemo.context.converter;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.erebelo.springmongodbdemo.domain.enumeration.type.EnumValueType;
import org.junit.jupiter.api.Test;
import org.springframework.core.convert.converter.Converter;

class EnumValueTypeReadingConverterTest {

    @Test
    void testConvertStringToEnumValueType() {
        EnumValueTypeReadingConverter factory = new EnumValueTypeReadingConverter();
        Converter<String, TestEnum> converter = factory.getConverter(TestEnum.class);

        assertNotNull(converter);
        EnumValueType convertedValue = converter.convert("value-1");
        assertEquals(TestEnum.VALUE_1, convertedValue);
    }

    @Test
    void testConvertStringToANonEnumTypeThrowsIllegalStateException() {
        EnumValueTypeReadingConverter factory = new EnumValueTypeReadingConverter();
        Converter<String, EnumValueType> converter = factory.getConverter(EnumValueType.class);

        assertNotNull(converter);
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> converter.convert("value-1"))
                .withMessage("The targetType [EnumValueType] have to be an enum.");
    }

    @Test
    void testConvertNonValidStringThrowsIllegalStateException() {
        EnumValueTypeReadingConverter factory = new EnumValueTypeReadingConverter();
        Converter<String, TestEnum> converter = factory.getConverter(TestEnum.class);

        assertNotNull(converter);
        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(() -> converter.convert("value-invalid"))
                .withMessage("The value [value-invalid] doesn't match with any instance of the enum [TestEnum]");
    }

    enum TestEnum implements EnumValueType {
        VALUE_1("value-1");

        private final String value;

        TestEnum(String value) {
            this.value = value;
        }

        @Override
        public String getValue() {
            return value;
        }
    }
}
