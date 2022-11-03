package com.erebelo.springmongodbdemo.context.converter;

import com.erebelo.springmongodbdemo.domain.enumeration.EnumTypeId;
import org.bson.Document;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public enum EnumTypeIdToDocumentConverter implements Converter<EnumTypeId, Document> {

    INSTANCE;

    @Override
    public Document convert(EnumTypeId source) {
        var document = new Document();
        document.put("id", source.getId());
        document.put("value", source.getValue());

        return document;
    }
}
