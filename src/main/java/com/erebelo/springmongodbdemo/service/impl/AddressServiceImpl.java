package com.erebelo.springmongodbdemo.service.impl;

import com.erebelo.springmongodbdemo.context.history.DocumentHistoryService;
import com.erebelo.springmongodbdemo.domain.entity.AddressEntity;
import com.erebelo.springmongodbdemo.domain.request.AddressRequest;
import com.erebelo.springmongodbdemo.domain.response.BulkAddressResponse;
import com.erebelo.springmongodbdemo.domain.response.BulkOpsEngineResponse;
import com.erebelo.springmongodbdemo.mapper.AddressMapper;
import com.erebelo.springmongodbdemo.service.AddressService;
import com.mongodb.bulk.BulkWriteError;
import com.mongodb.bulk.BulkWriteResult;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.BulkOperationException;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class AddressServiceImpl implements AddressService {

    private final MongoTemplate mongoTemplate;
    private final DocumentHistoryService historyService;
    private final AddressMapper mapper;
    private final BulkOpsEngine bulkOpsEngine;

    public AddressServiceImpl(MongoTemplate mongoTemplate, DocumentHistoryService historyService,
            AddressMapper mapper) {
        this.mongoTemplate = mongoTemplate;
        this.historyService = historyService;
        this.mapper = mapper;
        this.bulkOpsEngine = new BulkOpsEngine(this.mongoTemplate, this.historyService);
    }

    @Override
    public BulkAddressResponse bulkInsertAddresses(List<AddressRequest> addressRequestList) {
        log.info("Bulk insert addresses");
        List<AddressEntity> addresses = mapper.requestListToEntityList(addressRequestList, LocalDateTime.now());
        List<AddressEntity> successList;
        List<AddressEntity> failedList = new ArrayList<>();

        try {
            BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, AddressEntity.class);
            bulkOps.insert(addresses);
            BulkWriteResult bulkWriteResult = bulkOps.execute();

            successList = extractSuccessfulBulkAddressInserts(bulkWriteResult, addresses);
        } catch (BulkOperationException e) {
            successList = extractSuccessfulBulkAddressInserts(e.getResult(), addresses);
            failedList = extractFailedBulkAddressInserts(e.getErrors(), addresses);
            log.info("{} record(s) failed to insert", failedList.size());

            if (!successList.isEmpty()) {
                /*
                 * Manually track the history of successfully inserted documents.
                 *
                 * This is only necessary for exception scenarios (in the catch block) because
                 * AbstractMongoEventListener lifecycle events are not automatically triggered
                 * when using BulkOperations.
                 */
                historyTrack(successList);
            }
        }

        log.info("Bulk insert completed: {} record(s) inserted successfully, {} record(s) failed to insert",
                successList.size(), failedList.size());

        return BulkAddressResponse.builder().success(mapper.entityListToResponseList(successList))
                .failed(mapper.entityListToResponseList(failedList)).build();
    }

    @Override
    public BulkAddressResponse bulkInsertAddressesByBulkOpsEngine(List<AddressRequest> addressRequestList) {
        log.info("Bulk insert addresses");
        List<AddressEntity> addresses = mapper.requestListToEntityList(addressRequestList, LocalDateTime.now());

        BulkOpsEngineResponse<AddressEntity> bulkOpsEngineResponse = bulkOpsEngine.bulkInsert(addresses,
                AddressEntity.class, AddressEntity::setId, AddressEntity::getId, AddressEntity::setErrorMessage);

        return BulkAddressResponse.builder()
                .success(mapper.entityListToResponseList(bulkOpsEngineResponse.getSuccess()))
                .failed(mapper.entityListToResponseList(bulkOpsEngineResponse.getFailed())).build();
    }

    private List<AddressEntity> extractSuccessfulBulkAddressInserts(BulkWriteResult bulkWriteResult,
            List<AddressEntity> addresses) {
        if (bulkWriteResult.getInsertedCount() > 0) {
            return bulkWriteResult.getInserts().parallelStream().map(insert -> {
                AddressEntity entity = addresses.get(insert.getIndex());
                entity.setId(insert.getId().asObjectId().getValue().toString());
                return entity;
            }).toList();
        }
        return new ArrayList<>();
    }

    private List<AddressEntity> extractFailedBulkAddressInserts(List<BulkWriteError> bulkWriteErrors,
            List<AddressEntity> addresses) {
        if (!bulkWriteErrors.isEmpty()) {
            return bulkWriteErrors.parallelStream().map(error -> {
                AddressEntity entity = addresses.get(error.getIndex());
                entity.setErrorMessage(error.getMessage());
                return entity;
            }).toList();
        }
        return new ArrayList<>();
    }

    private void historyTrack(List<AddressEntity> successList) {
        try {
            successList.parallelStream().forEach(entity -> {
                Document document = new Document("_id", new ObjectId(entity.getId()));
                mongoTemplate.getConverter().write(entity, document);
                historyService.saveChangeHistory(document, entity);
            });
        } catch (Exception e) {
            log.error("Error creating history document for {} record(s)", successList.size());
        }
    }
}
