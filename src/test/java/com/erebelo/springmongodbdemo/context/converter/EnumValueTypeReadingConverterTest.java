package com.erebelo.springmongodbdemo.context.converter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

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

        IllegalStateException exception = assertThrows(IllegalStateException.class, () -> converter.convert("value-1"));

        assertNotNull(converter);
        assertEquals("The targetType [EnumValueType] have to be an enum.", exception.getMessage());
    }

    @Test
    void testConvertNonValidStringThrowsIllegalStateException() {
        EnumValueTypeReadingConverter factory = new EnumValueTypeReadingConverter();
        Converter<String, TestEnum> converter = factory.getConverter(TestEnum.class);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> converter.convert("value-invalid"));

        assertNotNull(converter);
        assertEquals("The value [value-invalid] doesn't match with any instance of the enum [TestEnum]",
                exception.getMessage());
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
