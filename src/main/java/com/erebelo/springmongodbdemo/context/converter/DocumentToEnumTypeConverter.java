package com.erebelo.springmongodbdemo.context.converter;

import com.erebelo.springmongodbdemo.domain.enumeration.types.EnumType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.data.convert.ReadingConverter;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

@ReadingConverter
public enum DocumentToEnumTypeConverter implements ConverterFactory<String, EnumType> {

    INSTANCE;

    @Override
    @SuppressWarnings("unchecked")
    public <T extends EnumType> Converter<String, T> getConverter(final Class<T> targetType) {

        return source -> {
            if (!targetType.isEnum()) {
                throw new IllegalStateException(String.format("The targetType [%s] have to be an enum.", targetType.getSimpleName()));
            }

            try {
                return (T) getEnumType(targetType, source);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        };
    }

    private <T extends EnumType> EnumType getEnumType(Class<T> targetType, String source) throws IllegalAccessException, InvocationTargetException,
            NoSuchMethodException {
        EnumType enumType;

        try {
            var fromValueMethod = targetType.getDeclaredMethod("fromValue", String.class);
            enumType = (EnumType) fromValueMethod.invoke(null, source);
        } catch (NoSuchMethodException e) {
            var valuesMethod = targetType.getDeclaredMethod("values");

            enumType =
                    Arrays.stream((EnumType[]) valuesMethod.invoke(null)).filter(obj -> obj.getValue().equalsIgnoreCase(source)).findFirst().orElseThrow(() -> new IllegalStateException(String.format("The value [%s] doesn't match with any instance of the enum [%s]", source, targetType.getSimpleName())));
        }

        return enumType;
    }
}
