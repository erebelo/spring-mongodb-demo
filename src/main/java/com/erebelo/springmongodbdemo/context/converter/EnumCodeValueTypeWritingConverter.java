package com.erebelo.springmongodbdemo.context.converter;

import com.erebelo.springmongodbdemo.domain.enumeration.types.EnumCodeValueType;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public enum EnumCodeValueTypeWritingConverter implements Converter<EnumCodeValueType, Document> {

    INSTANCE;

    @Override
    public Document convert(EnumCodeValueType source) {
        var document = new Document();
        document.put("code", source.getCode());
        document.put("value", source.getValue());

        return document;
    }
}
