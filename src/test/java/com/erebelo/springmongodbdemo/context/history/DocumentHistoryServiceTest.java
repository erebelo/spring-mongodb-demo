package com.erebelo.springmongodbdemo.context.history;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.erebelo.springmongodbdemo.domain.entity.BaseEntity;
import com.erebelo.springmongodbdemo.util.HttpHeadersUtil;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;

@ExtendWith(MockitoExtension.class)
class DocumentHistoryServiceTest {

    @InjectMocks
    private DocumentHistoryService service;

    @Mock
    private MongoTemplate mongoTemplate;

    @Captor
    private ArgumentCaptor<Document> documentArgumentCaptor;

    private static final String USER_ID = "12345";

    @Test
    void testCreateHistory() {
        given(mongoTemplate.insert(any(Document.class), anyString())).willReturn(new Document());

        var document = new Document().append("_id", new ObjectId()).append("name", "test-create").append("version", 0L);

        service.saveChangeHistory(document, new TestEntity(null, "test-create"));

        verify(mongoTemplate).insert(documentArgumentCaptor.capture(), eq("test-history"));

        var capturedValue = documentArgumentCaptor.getValue();
        assertEquals("INSERT", capturedValue.get("action"));
        assertEquals(capturedValue.get("documentId"), document.getObjectId("_id").toString());
        assertNotNull(capturedValue.get("document"));

        var capturedDocument = (Document) capturedValue.get("document");
        assertNull(capturedDocument.getObjectId("_id"));
        assertEquals("test-create", capturedDocument.get("name"));
        assertEquals(0L, capturedDocument.get("version"));
    }

    @Test
    void testUpdateHistory() {
        given(mongoTemplate.insert(any(Document.class), anyString())).willReturn(new Document());

        var document = new Document().append("_id", new ObjectId()).append("name", "test-update").append("version", 1L);

        service.saveChangeHistory(document, new TestEntity(null, "test-update"));

        verify(mongoTemplate).insert(documentArgumentCaptor.capture(), eq("test-history"));

        var capturedValue = documentArgumentCaptor.getValue();
        assertEquals("UPDATE", capturedValue.get("action"));
        assertEquals(capturedValue.get("documentId"), document.getObjectId("_id").toString());
        assertNotNull(capturedValue.get("document"));

        var capturedDocument = (Document) capturedValue.get("document");
        assertNull(capturedDocument.getObjectId("_id"));
        assertEquals("test-update", capturedDocument.get("name"));
        assertEquals(1L, capturedDocument.get("version"));
    }

    @Test
    void testDeleteHistory() {
        given(mongoTemplate.insert(any(Document.class), anyString())).willReturn(new Document());

        try (MockedStatic<HttpHeadersUtil> mockedStatic = Mockito.mockStatic(HttpHeadersUtil.class)) {
            mockedStatic.when(HttpHeadersUtil::getLoggedInUser).thenReturn(USER_ID);

            var document = new Document().append("_id", new ObjectId());

            service.saveDeleteHistory(document, TestEntity.class);

            verify(mongoTemplate).insert(documentArgumentCaptor.capture(), eq("test-history"));

            var capturedValue = documentArgumentCaptor.getValue();
            assertEquals("DELETE", capturedValue.get("action"));
            assertEquals(document.getObjectId("_id").toString(), capturedValue.get("documentId"));
            assertEquals(USER_ID, capturedValue.get("historyCreatedBy"));
            assertNotNull(capturedValue.get("historyCreatedDateTime"));
            assertNull(capturedValue.get("document"));
        }
    }

    @Test
    void testIsToSaveHistoryThrowsNullPointerException() {
        var document = new Document().append("_id", new ObjectId());

        assertThrows(NullPointerException.class, () -> service.saveDeleteHistory(document, null));
    }

    @Getter
    @AllArgsConstructor
    @DocumentHistory(collection = "test-history")
    static class TestEntity extends BaseEntity {
        private String id;
        private String name;
    }
}
