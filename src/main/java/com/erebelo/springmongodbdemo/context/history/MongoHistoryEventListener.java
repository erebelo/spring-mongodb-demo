package com.erebelo.springmongodbdemo.context.history;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;
import org.springframework.stereotype.Component;

/* Mongo event listener handles the entity/document before/after persisting it */

@Component
@RequiredArgsConstructor
public class MongoHistoryEventListener extends AbstractMongoEventListener<Object> {

    private final DocumentHistoryService service;

    @Override
    public void onAfterSave(AfterSaveEvent<Object> event) {
        service.saveChangeHistory(event.getDocument(), event.getSource());
    }

    @Override
    public void onAfterDelete(AfterDeleteEvent<Object> event) {
        service.saveDeleteHistory(event.getDocument(), event.getType());
    }
}
