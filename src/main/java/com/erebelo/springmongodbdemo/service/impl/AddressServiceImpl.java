package com.erebelo.springmongodbdemo.service.impl;

import static com.erebelo.springmongodbdemo.validation.AddressValidator.getRequestValidationErrors;

import com.erebelo.springmongodbdemo.context.history.DocumentHistoryService;
import com.erebelo.springmongodbdemo.domain.entity.AddressEntity;
import com.erebelo.springmongodbdemo.domain.request.AddressRequest;
import com.erebelo.springmongodbdemo.domain.response.BulkAddressResponse;
import com.erebelo.springmongodbdemo.domain.response.BulkOpsEngineResponse;
import com.erebelo.springmongodbdemo.mapper.AddressMapper;
import com.erebelo.springmongodbdemo.service.AddressService;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Log4j2
@Service
public class AddressServiceImpl implements AddressService {

    private final AddressMapper mapper;
    private final String applicationName;
    private final BulkOpsEngine bulkOpsEngine;

    public AddressServiceImpl(AddressMapper mapper, @Value("${spring.application.name}") String applicationName,
            MongoTemplate mongoTemplate, DocumentHistoryService historyService) {
        this.mapper = mapper;
        this.applicationName = applicationName;
        this.bulkOpsEngine = new BulkOpsEngine(mongoTemplate, historyService);
    }

    @Override
    public BulkAddressResponse bulkInsertAddresses(List<AddressRequest> addressRequestList) {
        log.info("Bulk insert addresses");
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
