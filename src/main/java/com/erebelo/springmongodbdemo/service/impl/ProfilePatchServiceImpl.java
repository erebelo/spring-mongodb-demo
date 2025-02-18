package com.erebelo.springmongodbdemo.service.impl;

import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_400_001;
import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_404_002;
import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_422_000;
import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_422_001;
import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_422_002;
import static com.erebelo.springmongodbdemo.validation.ProfileConstraintValidator.validateContactNumbers;
import static com.erebelo.springmongodbdemo.validation.ProfileConstraintValidator.validateDateOfBirth;
import static com.erebelo.springmongodbdemo.validation.ProfileConstraintValidator.validateSpouseProfile;
import static java.util.Objects.isNull;

import com.erebelo.spring.common.utils.serialization.ObjectMapperProvider;
import com.erebelo.springmongodbdemo.domain.entity.ProfileEntity;
import com.erebelo.springmongodbdemo.domain.request.ProfileRequest;
import com.erebelo.springmongodbdemo.domain.response.ProfileResponse;
import com.erebelo.springmongodbdemo.exception.model.CommonException;
import com.erebelo.springmongodbdemo.mapper.ProfileMapper;
import com.erebelo.springmongodbdemo.repository.ProfileRepository;
import com.erebelo.springmongodbdemo.service.ProfilePatchService;
import com.erebelo.springmongodbdemo.service.ProfileService;
import com.erebelo.springmongodbdemo.validation.FieldMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@RequiredArgsConstructor
public class ProfilePatchServiceImpl implements ProfilePatchService {

    private final ProfileService profileService;
    private final ProfileMapper mapper;
    private final ProfileRepository repository;

    @Override
    @Transactional
    public ProfileResponse patchProfile(String userId, Map<String, Object> profileRequestMap) {
        log.info("Patching profile with userId: {}", userId);

        log.info("Validating map request attributes");
        if (isNull(profileRequestMap) || profileRequestMap.isEmpty()) {
            throw new CommonException(COMMON_ERROR_400_001,
                    Collections.singletonList("request body is mandatory and must contain some " + "attribute"));
        }

        log.info("Fetching profile from database");
        ProfileEntity profile = repository.findByUserId(userId)
                .orElseThrow(() -> new CommonException(COMMON_ERROR_404_002, userId));

        if (isNull(profile.getProfile())) {
            throw new CommonException(COMMON_ERROR_422_002);
        }

        int patchCounter;
        ProfileRequest dbProfileRequest = mapper.entityToRequest(profile.getProfile());
        try {
            log.info("Serializing/deserializing database profile object");
            Map<String, Object> dbProfileRequestMap = ObjectMapperProvider.INSTANCE.readValue(
                    ObjectMapperProvider.INSTANCE.writeValueAsString(dbProfileRequest), new TypeReference<>() {
                    });

            log.info("Recursively merging profile request with database profile object");
            patchCounter = recursiveMapMerge(profileRequestMap, dbProfileRequestMap);

            log.info("Serializing/deserializing profile object after merging objects");
            dbProfileRequest = ObjectMapperProvider.INSTANCE.readValue(
                    ObjectMapperProvider.INSTANCE.writeValueAsString(dbProfileRequestMap), ProfileRequest.class);
        } catch (JsonProcessingException e) {
            throw new CommonException(COMMON_ERROR_422_001, e, e.getMessage());
        }

        if (patchCounter > 0) {
            log.info("Validating merged request attributes");
            validateRequestAttributes(dbProfileRequest);

            log.info("Updating {} field(s) by patch request", patchCounter);
            return profileService.updateProfile(userId, dbProfileRequest);
        } else {
            log.info("No updates found for profile object by patch request");
            return mapper.entityToResponse(profile.getProfile());
        }
    }

    @SuppressWarnings("unchecked")
    private int recursiveMapMerge(Map<String, Object> source, Map<String, Object> target) {
        int counter = 0;
        for (Map.Entry<String, Object> entry : source.entrySet()) {
            String key = entry.getKey();
            Object sourceValue = entry.getValue();
            Object targetValue = target.get(key);

            if (targetValue instanceof Map && sourceValue instanceof Map && !((Map<?, ?>) sourceValue).isEmpty()) {
                counter += recursiveMapMerge((Map<String, Object>) sourceValue, (Map<String, Object>) targetValue);
            } else if (!Objects.equals(sourceValue, targetValue)) {
                target.put(key, sourceValue);
                counter++;
            }
        }
        return counter;
    }

    private void validateRequestAttributes(ProfileRequest dbProfileRequest) {
        List<FieldMessage> errorMessages = new ArrayList<>();

        validateDateOfBirth(dbProfileRequest, errorMessages);
        validateContactNumbers(dbProfileRequest, errorMessages);
        validateSpouseProfile(dbProfileRequest, errorMessages);

        if (!errorMessages.isEmpty()) {
            StringJoiner joiner = new StringJoiner(", ", "[", "]");

            for (FieldMessage field : errorMessages) {
                joiner.add(field.getMessage());
            }
            throw new CommonException(COMMON_ERROR_422_000, joiner.toString());
        }
    }
}
