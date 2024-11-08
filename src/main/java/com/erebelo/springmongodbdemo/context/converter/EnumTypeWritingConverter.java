package com.erebelo.springmongodbdemo.context.converter;

import com.erebelo.springmongodbdemo.domain.enumeration.types.EnumType;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public enum EnumTypeWritingConverter implements Converter<EnumType, String> {

    INSTANCE;

    @Override
    public String convert(EnumType source) {
        return source.getValue();
    }

}
