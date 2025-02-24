package com.erebelo.springmongodbdemo.service.impl;

import com.erebelo.springmongodbdemo.context.history.DocumentHistoryService;
import com.erebelo.springmongodbdemo.domain.response.BulkOpsEngineResponse;
import com.mongodb.bulk.BulkWriteError;
import com.mongodb.bulk.BulkWriteResult;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.BulkOperationException;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

@Log4j2
@AllArgsConstructor
public class BulkOpsEngine {

    private MongoTemplate mongoTemplate;
    private DocumentHistoryService historyService;

    public <T> BulkOpsEngineResponse<T> bulkInsert(List<T> entityList, Class<T> entityClass,
            BiConsumer<T, String> idSetter, Function<T, String> idGetter, BiConsumer<T, String> errorMessageSetter) {
        log.info("Bulk insert by bulk operations engine");
        List<T> successList;
        List<T> failedList = new ArrayList<>();

        try {
            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, entityClass);
            bulkOps.insert(entityList);
            BulkWriteResult bulkWriteResult = bulkOps.execute();

            successList = extractSuccessfulBulkInserts(bulkWriteResult, entityList, idSetter);
        } catch (BulkOperationException e) {
            successList = extractSuccessfulBulkInserts(e.getResult(), entityList, idSetter);
            failedList = extractFailedBulkInserts(e.getErrors(), entityList, errorMessageSetter);

            if (!successList.isEmpty()) {
                /*
                 * Manually track the history of successfully inserted documents.
                 *
                 * This is only necessary for exception scenarios (in the catch block) because
                 * AbstractMongoEventListener lifecycle events are not automatically triggered
                 * when using BulkOperations.
                 */
                historyTrack(successList, idGetter);
            }
        }

        log.info("Bulk insert completed: {} record(s) inserted successfully, {} record(s) failed to insert",
                successList.size(), failedList.size());
        return new BulkOpsEngineResponse<>(successList, failedList);
    }

    private <T> List<T> extractSuccessfulBulkInserts(BulkWriteResult bulkWriteResult, List<T> entityList,
            BiConsumer<T, String> idSetter) {
        if (bulkWriteResult.getInsertedCount() > 0) {
            return bulkWriteResult.getInserts().parallelStream().map(insert -> {
                T entity = entityList.get(insert.getIndex());
                String objectId = insert.getId().asObjectId().getValue().toString();
                idSetter.accept(entity, objectId);
                return entity;
            }).toList();
        }
        return new ArrayList<>();
    }

    private <T> List<T> extractFailedBulkInserts(List<BulkWriteError> bulkWriteErrors, List<T> entityList,
            BiConsumer<T, String> errorMessageSetter) {
        if (!bulkWriteErrors.isEmpty()) {
            return bulkWriteErrors.parallelStream().map(error -> {
                T entity = entityList.get(error.getIndex());
                errorMessageSetter.accept(entity, error.getMessage());
                return entity;
            }).toList();
        }
        return new ArrayList<>();
    }

    private <T> void historyTrack(List<T> successList, Function<T, String> idGetter) {
        try {
            successList.parallelStream().forEach(entity -> {
                Document document = new Document("_id", new ObjectId(idGetter.apply(entity)));
                mongoTemplate.getConverter().write(entity, document);
                historyService.saveChangeHistory(document, entity);
            });
        } catch (Exception e) {
            log.error("Error creating history document for {} record(s)", successList.size(), e);
        }
    }
}
