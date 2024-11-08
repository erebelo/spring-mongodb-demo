package com.erebelo.springmongodbdemo.context.converter;

import com.erebelo.springmongodbdemo.domain.enumeration.types.EnumCodeValueType;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.converter.ConverterFactory;
import org.springframework.data.convert.ReadingConverter;

@ReadingConverter
public class EnumCodeValueTypeReadingConverter implements ConverterFactory<Document, EnumCodeValueType> {

    @Override
    @SuppressWarnings("unchecked")
    public <T extends EnumCodeValueType> Converter<Document, T> getConverter(final Class<T> targetType) {

        return source -> {
            if (!targetType.isEnum()) {
                throw new IllegalStateException(
                        String.format("The targetType [%s] have to be an enum.", targetType.getSimpleName()));
            }

            try {
                String code = source.get("code", String.class);
                return (T) getMongoEnumType(targetType, code);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException(e.getMessage(), e);
            }
        };
    }

    private <T extends EnumCodeValueType> EnumCodeValueType getMongoEnumType(Class<T> targetType, String source)
            throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
        EnumCodeValueType mongoEnumType;

        try {
            var fromValueMethod = targetType.getDeclaredMethod("fromCode", String.class);
            mongoEnumType = (EnumCodeValueType) fromValueMethod.invoke(null, source);
        } catch (NoSuchMethodException e) {
            var valuesMethod = targetType.getDeclaredMethod("values");

            mongoEnumType = Arrays.stream((EnumCodeValueType[]) valuesMethod.invoke(null))
                    .filter(obj -> obj.getValue().equalsIgnoreCase(source)).findFirst()
                    .orElseThrow(() -> new IllegalStateException(
                            String.format("The value [%s] doesn't match with any instance of the enum [%s]", source,
                                    targetType.getSimpleName())));
        }

        return mongoEnumType;
    }
}
