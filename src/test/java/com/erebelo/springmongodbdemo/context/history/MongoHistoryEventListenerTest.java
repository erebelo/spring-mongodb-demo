package com.erebelo.springmongodbdemo.context.history;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.mapping.event.AfterDeleteEvent;
import org.springframework.data.mongodb.core.mapping.event.AfterSaveEvent;

@ExtendWith(MockitoExtension.class)
class MongoHistoryEventListenerTest {

    @InjectMocks
    private MongoHistoryEventListener eventListener;

    @Mock
    private DocumentHistoryService service;

    @Test
    void testOnAfterSave() {
        var event = new AfterSaveEvent<>(new Object(), new Document(), "collection");

        eventListener.onAfterSave(event);

        verify(service).saveChangeHistory(eq(event.getDocument()), eq(event.getSource()));
    }

    @Test
    void testOnAfterDelete() {
        var event = new AfterDeleteEvent<>(new Document(), Object.class, "collection");

        eventListener.onAfterDelete(event);

        verify(service).saveDeleteHistory(eq(new Document()), eq(Object.class));
    }
}
