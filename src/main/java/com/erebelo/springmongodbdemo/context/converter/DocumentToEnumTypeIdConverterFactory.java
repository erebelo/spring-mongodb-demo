package com.erebelo.springmongodbdemo.context.converter;

import com.erebelo.springmongodbdemo.domain.enumeration.EnumTypeId;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.data.convert.ReadingConverter;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

@ReadingConverter
public enum DocumentToEnumTypeIdConverterFactory implements ConverterFactory<Document, EnumTypeId> {

    INSTANCE;

    @Override
    public <T extends EnumTypeId> Converter<Document, T> getConverter(Class<T> targetType) {
        return source -> {
            if (!targetType.isEnum()) {
                throw new IllegalStateException(String.format("The targetType [%s] was expected to be an enum.", targetType.getSimpleName()));
            }

            try {
                String code = source.get("value", String.class);
                return (T) getMongoEnumType(targetType, code);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        };
    }

    private <T extends EnumTypeId> Object getMongoEnumType(Class<T> targetType, String source) throws NoSuchMethodException,
            InvocationTargetException, IllegalAccessException {
        EnumTypeId mongoEnumType;

        try {
            var fromValueMethod = targetType.getDeclaredMethod("fromValue", String.class);
            mongoEnumType = (EnumTypeId) fromValueMethod.invoke(null, source);
        } catch (NoSuchMethodException e) {
            var methodValues = targetType.getDeclaredMethod("values");

            mongoEnumType = Arrays.stream((EnumTypeId[]) methodValues.invoke(null))
                    .filter(obj -> obj.getValue().equalsIgnoreCase(source))
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException(String.format("The value [%s] doesn't match any instance of enum [%s]", source,
                            targetType.getSimpleName())));
        }

        return mongoEnumType;
    }
}
