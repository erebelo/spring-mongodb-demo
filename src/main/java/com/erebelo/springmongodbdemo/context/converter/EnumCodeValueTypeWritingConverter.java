package com.erebelo.springmongodbdemo.context.converter;

import com.erebelo.springmongodbdemo.domain.enumeration.type.EnumCodeValueType;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class EnumCodeValueTypeWritingConverter implements Converter<EnumCodeValueType, Document> {

    @Override
    public Document convert(EnumCodeValueType source) {
        Document document = new Document();
        document.put("code", source.getCode());
        document.put("value", source.getValue());

        return document;
    }
}
