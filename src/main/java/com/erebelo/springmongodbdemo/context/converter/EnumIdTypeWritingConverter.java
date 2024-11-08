package com.erebelo.springmongodbdemo.context.converter;

import com.erebelo.springmongodbdemo.domain.enumeration.types.EnumIdType;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public enum EnumIdTypeWritingConverter implements Converter<EnumIdType, Document> {

    INSTANCE;

    @Override
    public Document convert(EnumIdType source) {
        var document = new Document();
        document.put("id", source.getId());
        document.put("value", source.getValue());

        return document;
    }
}
