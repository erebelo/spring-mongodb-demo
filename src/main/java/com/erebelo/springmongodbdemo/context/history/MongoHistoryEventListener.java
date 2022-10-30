package com.erebelo.springmongodbdemo.context.history;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;

public class MongoHistoryEventListener extends AbstractMongoEventListener<Object> {

    @Autowired
    private DocumentHistoryService service;

    @Override
    public void onAfterSave(AfterSaveEvent<Object> event) {
        service.saveChangeHistory(event.getDocument(), event.getSource());
    }

    @Override
    public void onAfterDelete(AfterDeleteEvent<Object> event) {
        service.saveDeleteHistory(event.getDocument(), event.getType());
    }
}
