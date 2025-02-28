package com.erebelo.springmongodbdemo.service.impl;

import static com.erebelo.springmongodbdemo.validation.AddressValidator.getRequestValidationErrors;

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
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Value;
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
    private final String applicationName;
    private final BulkOpsEngine bulkOpsEngine;

    public AddressServiceImpl(MongoTemplate mongoTemplate, DocumentHistoryService historyService, AddressMapper mapper,
            @Value("${spring.application.name}") String applicationName) {
        this.mongoTemplate = mongoTemplate;
        this.historyService = historyService;
        this.mapper = mapper;
        this.applicationName = applicationName;
        this.bulkOpsEngine = new BulkOpsEngine(this.mongoTemplate, this.historyService);
    }

    @Override
    public BulkAddressResponse bulkInsertAddresses(List<AddressRequest> addressRequestList) {
        log.info("Bulk insert addresses");
        List<AddressEntity> validAddresses = Collections.synchronizedList(new ArrayList<>());
        List<AddressEntity> failedAddresses = Collections.synchronizedList(new ArrayList<>());

        log.info("Validating addresses");
        validateAndParseRequest(addressRequestList, validAddresses, failedAddresses);

        List<AddressEntity> successList = new ArrayList<>();
        List<AddressEntity> failedList = new ArrayList<>();

        if (!validAddresses.isEmpty()) {
            try {
                BulkOperations bulkOps = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, AddressEntity.class);
                bulkOps.insert(validAddresses);
                BulkWriteResult bulkWriteResult = bulkOps.execute();

                successList = extractSuccessfulBulkAddressInserts(bulkWriteResult, validAddresses);
            } catch (BulkOperationException e) {
                successList = extractSuccessfulBulkAddressInserts(e.getResult(), validAddresses);
                failedList = extractFailedBulkAddressInserts(e.getErrors(), validAddresses);

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
        }

        // Combining failedAddresses and failedList
        List<AddressEntity> combinedFailedList = Stream.concat(failedAddresses.stream(), failedList.stream()).toList();

        log.info("Bulk insert completed: {} record(s) inserted successfully, {} record(s) failed to insert",
                successList.size(), combinedFailedList.size());
        return BulkAddressResponse.builder().success(mapper.entityListToResponseList(successList))
                .failed(mapper.entityListToResponseList(combinedFailedList)).build();
    }

    @Override
    public BulkAddressResponse bulkInsertAddressesByBulkOpsEngine(List<AddressRequest> addressRequestList) {
        log.info("Bulk insert addresses by bulk operations engine");
        List<AddressEntity> validAddresses = Collections.synchronizedList(new ArrayList<>());
        List<AddressEntity> failedAddresses = Collections.synchronizedList(new ArrayList<>());

        log.info("Validating addresses");
        validateAndParseRequest(addressRequestList, validAddresses, failedAddresses);

        BulkOpsEngineResponse<AddressEntity> bulkOpsEngineResponse = bulkOpsEngine.bulkInsert(validAddresses,
                AddressEntity.class, AddressEntity::setId, AddressEntity::getId, AddressEntity::setErrorMessage);

        // Combining failedAddresses and BulkOpsEngineResponse failed object
        List<AddressEntity> combinedFailedList = Stream
                .concat(failedAddresses.stream(), bulkOpsEngineResponse.getFailed().stream()).toList();

        log.info("Bulk insert completed: {} record(s) inserted successfully, {} record(s) failed to insert",
                bulkOpsEngineResponse.getSuccess().size(), combinedFailedList.size());
        return BulkAddressResponse.builder()
                .success(mapper.entityListToResponseList(bulkOpsEngineResponse.getSuccess()))
                .failed(mapper.entityListToResponseList(combinedFailedList)).build();
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

    private void validateAndParseRequest(List<AddressRequest> addressRequestList, List<AddressEntity> validAddresses,
            List<AddressEntity> failedAddresses) {
        LocalDateTime dateTime = LocalDateTime.now();

        addressRequestList.parallelStream().forEach(request -> {
            Optional<String> errorMessageOpt = getRequestValidationErrors(request);

            if (errorMessageOpt.isEmpty()) {
                validAddresses.add(mapper.requestToEntity(request, applicationName, dateTime));
            } else {
                AddressEntity failedEntry = mapper.requestToEntity(request, applicationName, dateTime);
                failedEntry.setErrorMessage(errorMessageOpt.get());
                failedAddresses.add(failedEntry);
            }
        });
    }
}
