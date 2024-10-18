package com.erebelo.springmongodbdemo.context;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.erebelo.springmongodbdemo.context.history.DocumentHistory;
import com.erebelo.springmongodbdemo.context.history.DocumentHistoryService;
import com.erebelo.springmongodbdemo.domain.entity.BaseEntity;
import com.erebelo.springmongodbdemo.util.AuthenticationUtil;
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
        when(mongoTemplate.insert(any(Document.class), anyString())).thenReturn(new Document());

        var document = new Document()
                .append("_id", new ObjectId())
                .append("name", "test-create")
                .append("version", 0L);

        service.saveChangeHistory(document, new TestEntity(null, "test-create"));

        verify(mongoTemplate).insert(documentArgumentCaptor.capture(), eq("test-history"));

        var capturedValue = documentArgumentCaptor.getValue();
        assertEquals(capturedValue.get("action"), "INSERT");
        assertEquals(capturedValue.get("documentId"), document.getObjectId("_id").toString());
        assertNotNull(capturedValue.get("document"));

        var capturedDocument = (Document) capturedValue.get("document");
        assertNull(capturedDocument.getObjectId("_id"));
        assertEquals(capturedDocument.get("name"), "test-create");
        assertEquals(capturedDocument.get("version"), 0L);
    }

    @Test
    void testUpdateHistory() {
        when(mongoTemplate.insert(any(Document.class), anyString())).thenReturn(new Document());

        var document = new Document()
                .append("_id", new ObjectId())
                .append("name", "test-update")
                .append("version", 1L);

        service.saveChangeHistory(document, new TestEntity(null, "test-update"));

        verify(mongoTemplate).insert(documentArgumentCaptor.capture(), eq("test-history"));

        var capturedValue = documentArgumentCaptor.getValue();
        assertEquals(capturedValue.get("action"), "UPDATE");
        assertEquals(capturedValue.get("documentId"), document.getObjectId("_id").toString());
        assertNotNull(capturedValue.get("document"));

        var capturedDocument = (Document) capturedValue.get("document");
        assertNull(capturedDocument.getObjectId("_id"));
        assertEquals(capturedDocument.get("name"), "test-update");
        assertEquals(capturedDocument.get("version"), 1L);
    }

    @Test
    void testDeleteHistory() {
        when(mongoTemplate.insert(any(Document.class), anyString())).thenReturn(new Document());

        try (MockedStatic<AuthenticationUtil> mockedStatic = Mockito.mockStatic(AuthenticationUtil.class)) {
            mockedStatic.when(AuthenticationUtil::getLoggedInUser).thenReturn(USER_ID);

            var document = new Document().append("_id", new ObjectId());

            service.saveDeleteHistory(document, TestEntity.class);

            verify(mongoTemplate).insert(documentArgumentCaptor.capture(), eq("test-history"));

            var capturedValue = documentArgumentCaptor.getValue();
            assertEquals(capturedValue.get("action"), "DELETE");
            assertEquals(document.getObjectId("_id").toString(), capturedValue.get("documentId"));
            assertEquals(capturedValue.get("historyCreatedBy"), USER_ID);
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
