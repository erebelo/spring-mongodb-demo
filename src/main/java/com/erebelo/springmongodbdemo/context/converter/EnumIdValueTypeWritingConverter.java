package com.erebelo.springmongodbdemo.context.converter;

import com.erebelo.springmongodbdemo.domain.enumeration.type.EnumIdValueType;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class EnumIdValueTypeWritingConverter implements Converter<EnumIdValueType, Document> {

    @Override
    public Document convert(EnumIdValueType source) {
        Document document = new Document();
        document.put("id", source.getId());
        document.put("value", source.getValue());
        return document;
    }
}
