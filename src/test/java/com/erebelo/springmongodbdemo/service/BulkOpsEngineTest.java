package com.erebelo.springmongodbdemo.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.erebelo.springmongodbdemo.context.history.DocumentHistoryService;
import com.erebelo.springmongodbdemo.domain.response.BulkOpsEngineResponse;
import com.erebelo.springmongodbdemo.service.impl.BulkOpsEngine;
import com.mongodb.MongoBulkWriteException;
import com.mongodb.bulk.BulkWriteError;
import com.mongodb.bulk.BulkWriteInsert;
import com.mongodb.bulk.BulkWriteResult;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bson.BsonObjectId;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.BulkOperationException;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;

@ExtendWith(MockitoExtension.class)
class BulkOpsEngineTest {

    @InjectMocks
    private BulkOpsEngine bulkOpsEngine;

    @Mock
    private MongoTemplate mongoTemplate;

    @Mock
    private DocumentHistoryService historyService;

    private static final String ID_1 = "67bfd1e9bba32978e8c5d359";
    private static final String ID_2 = "67bfd1e9bba32978e8c5d4ff";
    private static final String ID_3 = "67bfd1e9bba32978e8c5d35f";
    private static final String ADDRESS_1 = "123 Main St";
    private static final String ADDRESS_2 = "456 Elm St";
    private static final String ADDRESS_3 = "789 Oak St";

    @Test
    void testBulkInsertWithSuccessAndNoFailedRecordsSuccessful() {
        List<TestEntity> entityList = List.of(new TestEntity(null, null, ADDRESS_1),
                new TestEntity(null, null, ADDRESS_2), new TestEntity(null, null, ADDRESS_3));
        List<TestEntity> successList = List.of(new TestEntity(ID_1, null, ADDRESS_1),
                new TestEntity(ID_2, null, ADDRESS_2), new TestEntity(ID_3, null, ADDRESS_3));

        BulkOperations bulkOps = mock(BulkOperations.class);
        BulkWriteResult bulkWriteResult = mock(BulkWriteResult.class);

        given(mongoTemplate.bulkOps(any(BulkOperations.BulkMode.class), any(Class.class))).willReturn(bulkOps);
        given(bulkOps.insert(anyList())).willReturn(bulkOps);
        given(bulkOps.execute()).willReturn(bulkWriteResult);
        given(bulkWriteResult.getInsertedCount()).willReturn(3);
        given(bulkWriteResult.getInserts())
                .willReturn(List.of(new BulkWriteInsert(0, new BsonObjectId(new ObjectId(ID_1))),
                        new BulkWriteInsert(1, new BsonObjectId(new ObjectId(ID_2))),
                        new BulkWriteInsert(2, new BsonObjectId(new ObjectId(ID_3)))));

        BulkOpsEngineResponse<TestEntity> response = bulkOpsEngine.bulkInsert(entityList, TestEntity.class,
                TestEntity::setId, TestEntity::getId, TestEntity::setErrorMessage);

        assertNotNull(response);
        assertEquals(3, response.getSuccess().size());
        assertThat(response.getSuccess()).usingRecursiveComparison().isEqualTo(successList);
        assertTrue(response.getFailed().isEmpty());

        verify(mongoTemplate).bulkOps(any(BulkOperations.BulkMode.class), any(Class.class));
        verify(historyService, never()).saveChangeHistory(any(Document.class), any(TestEntity.class));
    }

    @Test
    void testBulkInsertWithSuccessAndFailedRecordsSuccessful() {
        List<TestEntity> entityList = List.of(new TestEntity(null, null, ADDRESS_1),
                new TestEntity(null, null, ADDRESS_2), new TestEntity(null, null, ADDRESS_3));
        List<TestEntity> successList = List.of(new TestEntity(ID_1, null, ADDRESS_1),
                new TestEntity(ID_3, null, ADDRESS_3));
        List<TestEntity> failedList = Collections
                .singletonList(new TestEntity(null, "E11000 Duplicated key error", ADDRESS_2));

        BulkOperations bulkOps = mock(BulkOperations.class);
        BulkWriteResult bulkWriteResult = mock(BulkWriteResult.class);
        BulkWriteError bulkWriteError = mock(BulkWriteError.class);
        MongoBulkWriteException mongoBulkWriteException = mock(MongoBulkWriteException.class);
        MongoConverter mockConverter = mock(MongoConverter.class);

        given(mongoTemplate.bulkOps(any(BulkOperations.BulkMode.class), any(Class.class))).willReturn(bulkOps);
        given(bulkOps.insert(anyList())).willReturn(bulkOps);

        given(bulkWriteResult.getInsertedCount()).willReturn(2);
        given(bulkWriteResult.getInserts())
                .willReturn(List.of(new BulkWriteInsert(0, new BsonObjectId(new ObjectId(ID_1))),
                        new BulkWriteInsert(2, new BsonObjectId(new ObjectId(ID_3)))));
        given(bulkWriteError.getIndex()).willReturn(1);
        given(bulkWriteError.getMessage()).willReturn("E11000 Duplicated key error");

        given(mongoBulkWriteException.getWriteResult()).willReturn(bulkWriteResult);
        given(mongoBulkWriteException.getWriteErrors()).willReturn(Collections.singletonList(bulkWriteError));
        willThrow(new BulkOperationException("Bulk write operation error", mongoBulkWriteException)).given(bulkOps)
                .execute();

        given(mongoTemplate.getConverter()).willReturn(mockConverter);
        willDoNothing().given(mockConverter).write(any(), any(Document.class));
        willDoNothing().willThrow(new RuntimeException("Mocked exception on second iteration")).given(historyService)
                .saveChangeHistory(any(Document.class), any(TestEntity.class));

        BulkOpsEngineResponse<TestEntity> response = bulkOpsEngine.bulkInsert(entityList, TestEntity.class,
                TestEntity::setId, TestEntity::getId, TestEntity::setErrorMessage);

        assertNotNull(response);
        assertEquals(2, response.getSuccess().size());
        assertEquals(1, response.getFailed().size());
        assertThat(response.getSuccess()).usingRecursiveComparison().isEqualTo(successList);
        assertThat(response.getFailed()).usingRecursiveComparison().isEqualTo(failedList);

        verify(mongoTemplate).bulkOps(any(BulkOperations.BulkMode.class), any(Class.class));
        verify(mongoTemplate.getConverter(), times(2)).write(any(), any(Document.class));
        verify(historyService, times(2)).saveChangeHistory(any(Document.class), any(TestEntity.class));
    }

    @Test
    void testBulkInsertWithNoSuccessAndNoFailedRecordsSuccessful() {
        List<TestEntity> entityList = List.of(new TestEntity(null, null, ADDRESS_1),
                new TestEntity(null, null, ADDRESS_2), new TestEntity(null, null, ADDRESS_3));

        BulkOperations bulkOps = mock(BulkOperations.class);
        BulkWriteResult bulkWriteResult = mock(BulkWriteResult.class);
        MongoBulkWriteException mongoBulkWriteException = mock(MongoBulkWriteException.class);

        given(mongoTemplate.bulkOps(any(BulkOperations.BulkMode.class), any(Class.class))).willReturn(bulkOps);
        given(bulkOps.insert(anyList())).willReturn(bulkOps);

        given(bulkWriteResult.getInsertedCount()).willReturn(0);
        given(mongoBulkWriteException.getWriteResult()).willReturn(bulkWriteResult);
        given(mongoBulkWriteException.getWriteErrors()).willReturn(Collections.emptyList());
        willThrow(new BulkOperationException("Bulk write operation error", mongoBulkWriteException)).given(bulkOps)
                .execute();

        BulkOpsEngineResponse<TestEntity> response = bulkOpsEngine.bulkInsert(entityList, TestEntity.class,
                TestEntity::setId, TestEntity::getId, TestEntity::setErrorMessage);

        assertNotNull(response);
        assertEquals(0, response.getSuccess().size());
        assertEquals(0, response.getFailed().size());
        assertTrue(response.getSuccess().isEmpty());
        assertTrue(response.getFailed().isEmpty());

        verify(mongoTemplate).bulkOps(any(BulkOperations.BulkMode.class), any(Class.class));
        verify(historyService, never()).saveChangeHistory(any(Document.class), any(TestEntity.class));
    }

    @Test
    void testBulkInsertWithNullEntityListSuccessful() {
        BulkOpsEngineResponse<TestEntity> response = bulkOpsEngine.bulkInsert(null, TestEntity.class, TestEntity::setId,
                TestEntity::getId, TestEntity::setErrorMessage);

        assertNotNull(response);
        assertEquals(0, response.getSuccess().size());
        assertEquals(0, response.getFailed().size());
        assertTrue(response.getSuccess().isEmpty());
        assertTrue(response.getFailed().isEmpty());

        verify(mongoTemplate, never()).bulkOps(any(BulkOperations.BulkMode.class), any(Class.class));
        verify(historyService, never()).saveChangeHistory(any(Document.class), any(TestEntity.class));
    }

    @Test
    void testBulkInsertWithEmptyEntityListSuccessful() {
        BulkOpsEngineResponse<TestEntity> response = bulkOpsEngine.bulkInsert(Collections.emptyList(), TestEntity.class,
                TestEntity::setId, TestEntity::getId, TestEntity::setErrorMessage);

        assertNotNull(response);
        assertEquals(0, response.getSuccess().size());
        assertEquals(0, response.getFailed().size());
        assertTrue(response.getSuccess().isEmpty());
        assertTrue(response.getFailed().isEmpty());

        verify(mongoTemplate, never()).bulkOps(any(BulkOperations.BulkMode.class), any(Class.class));
        verify(historyService, never()).saveChangeHistory(any(Document.class), any(TestEntity.class));
    }

    @Getter
    @Setter
    @AllArgsConstructor
    static class TestEntity {
        private String id;
        private String errorMessage;
        private String address;
    }
}
