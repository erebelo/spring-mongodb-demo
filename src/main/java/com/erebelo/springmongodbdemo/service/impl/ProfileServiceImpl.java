package com.erebelo.springmongodbdemo.service.impl;

import com.erebelo.springmongodbdemo.domain.entity.ProfileEntity;
import com.erebelo.springmongodbdemo.domain.request.ProfileRequest;
import com.erebelo.springmongodbdemo.domain.response.ProfileResponse;
import com.erebelo.springmongodbdemo.exception.StandardException;
import com.erebelo.springmongodbdemo.mapper.ProfileMapper;
import com.erebelo.springmongodbdemo.repository.ProfileRepository;
import com.erebelo.springmongodbdemo.service.ProfileService;
import com.erebelo.springmongodbdemo.utils.ByteWrapperObject;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.erebelo.springmongodbdemo.exception.CommonErrorCodesEnum.COMMON_ERROR_404_001;
import static com.erebelo.springmongodbdemo.exception.CommonErrorCodesEnum.COMMON_ERROR_404_002;
import static com.erebelo.springmongodbdemo.exception.CommonErrorCodesEnum.COMMON_ERROR_404_003;
import static com.erebelo.springmongodbdemo.exception.CommonErrorCodesEnum.COMMON_ERROR_409_001;
import static com.erebelo.springmongodbdemo.utils.ByteHandlerUtils.byteArrayComparison;
import static com.erebelo.springmongodbdemo.utils.ByteHandlerUtils.byteGenerator;
import static com.erebelo.springmongodbdemo.utils.HashAlgorithmUtils.generateSHAHashObject;
import static com.erebelo.springmongodbdemo.utils.RegistrationUtils.getRegistrationName;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final ProfileMapper mapper;
    private final ProfileRepository repository;

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileServiceImpl.class);
    private static final String CHECK_OBJ_LOGGER = "Checking whether profile object exists by userId: {}";
    private static final String RESPONSE_BODY_LOGGER = "Response body: {}";

    @Override
    public ProfileResponse getProfileByUserId(String userId) {
        LOGGER.info("Getting profile by userId: {}", userId);
        var profileEntity = repository.findByUserId(userId).orElseThrow(() ->
                new StandardException(COMMON_ERROR_404_001, userId));

        LOGGER.info(RESPONSE_BODY_LOGGER, profileEntity);
        return mapper.entityToResponse(profileEntity);
    }

    @Transactional
    @Override
    public ProfileResponse insertProfile(String userId, ProfileRequest profileRequest) {
        LOGGER.info(CHECK_OBJ_LOGGER, userId);
        repository.findByUserId(userId).ifPresent(o -> {
            throw new StandardException(COMMON_ERROR_409_001);
        });

        var profileEntity = mapper.requestToEntity(profileRequest);
        setInsertAttributes(profileEntity, userId);

        LOGGER.info("Inserting profile: {}", profileEntity);
        profileEntity = repository.insert(profileEntity);

        LOGGER.info(RESPONSE_BODY_LOGGER, profileEntity);
        return mapper.entityToResponse(profileEntity);
    }

    @Transactional
    @Override
    public ProfileResponse updateProfile(String userId, ProfileRequest profileRequest) {
        LOGGER.info(CHECK_OBJ_LOGGER, userId);
        var profileEntity = repository.findByUserId(userId).orElseThrow(() ->
                new StandardException(COMMON_ERROR_404_002, userId));

        LOGGER.info("Generating byte arrays for the objects");
        ByteWrapperObject profileBytes = byteGenerator(profileEntity);
        setUpdateAttributes(profileEntity, mapper.requestToEntity(profileRequest));
        ByteWrapperObject newProfileBytes = byteGenerator(profileEntity);

        LOGGER.info("Checking whether the generated byte arrays are equals");
        if (!byteArrayComparison(profileBytes, newProfileBytes)) {
            LOGGER.info("Updating profile: {}", profileEntity);
            profileEntity = repository.save(profileEntity);
        }

        LOGGER.info(RESPONSE_BODY_LOGGER, profileEntity);
        return mapper.entityToResponse(profileEntity);
    }

    @Transactional
    @Override
    public void deleteProfile(String userId) {
        LOGGER.info(CHECK_OBJ_LOGGER, userId);
        var profileEntity = repository.findByUserId(userId).orElseThrow(() ->
                new StandardException(COMMON_ERROR_404_003, userId));

        LOGGER.info("Deleting profile: {}", profileEntity);
        repository.delete(profileEntity);
    }

    private void setInsertAttributes(ProfileEntity profileEntity, String userId) {
        profileEntity.setUserId(userId);
        profileEntity.setRegistrationName(getRegistrationName());
        profileEntity.setHashObject(generateSHAHashObject(profileEntity.toString()));
    }

    private void setUpdateAttributes(ProfileEntity profileEntity, ProfileEntity requestToEntity) {
        profileEntity.setFirstName(requestToEntity.getFirstName());
        profileEntity.setLastName(requestToEntity.getLastName());
        profileEntity.setDateOfBirth(requestToEntity.getDateOfBirth());
        profileEntity.setNumberOfDependents(requestToEntity.getNumberOfDependents());
        profileEntity.setEstimatedAnnualIncome(requestToEntity.getEstimatedAnnualIncome());
        profileEntity.setEstimatedNetWorth(requestToEntity.getEstimatedNetWorth());
        profileEntity.setGender(requestToEntity.getGender());
        profileEntity.setMaritalStatus(requestToEntity.getMaritalStatus());
        profileEntity.setEmploymentStatus(requestToEntity.getEmploymentStatus());
        profileEntity.setContactNumbers(requestToEntity.getContactNumbers());
        profileEntity.setCurrentLocation(requestToEntity.getCurrentLocation());
        profileEntity.setSpouseProfile(requestToEntity.getSpouseProfile());
    }
}
