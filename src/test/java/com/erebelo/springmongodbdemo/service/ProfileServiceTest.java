package com.erebelo.springmongodbdemo.service;

import com.erebelo.springmongodbdemo.domain.entity.ProfileEntity;
import com.erebelo.springmongodbdemo.domain.entity.UserProfile;
import com.erebelo.springmongodbdemo.domain.enumeration.EmploymentStatusEnum;
import com.erebelo.springmongodbdemo.domain.enumeration.MaritalStatusEnum;
import com.erebelo.springmongodbdemo.domain.request.ProfileRequest;
import com.erebelo.springmongodbdemo.domain.response.ProfileResponse;
import com.erebelo.springmongodbdemo.exception.StandardException;
import com.erebelo.springmongodbdemo.mapper.ProfileMapper;
import com.erebelo.springmongodbdemo.repository.ProfileRepository;
import com.erebelo.springmongodbdemo.service.impl.ProfileServiceImpl;
import com.erebelo.springmongodbdemo.utils.ByteHandlerUtils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static com.erebelo.springmongodbdemo.exception.CommonErrorCodesEnum.COMMON_ERROR_400_001;
import static com.erebelo.springmongodbdemo.exception.CommonErrorCodesEnum.COMMON_ERROR_404_001;
import static com.erebelo.springmongodbdemo.exception.CommonErrorCodesEnum.COMMON_ERROR_404_002;
import static com.erebelo.springmongodbdemo.exception.CommonErrorCodesEnum.COMMON_ERROR_404_003;
import static com.erebelo.springmongodbdemo.exception.CommonErrorCodesEnum.COMMON_ERROR_409_001;
import static com.erebelo.springmongodbdemo.exception.CommonErrorCodesEnum.COMMON_ERROR_422_000;
import static com.erebelo.springmongodbdemo.exception.CommonErrorCodesEnum.COMMON_ERROR_422_001;
import static com.erebelo.springmongodbdemo.exception.CommonErrorCodesEnum.COMMON_ERROR_422_002;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.FIRST_NAME;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.NEW_ESTIMATED_ANNUAL_INCOME;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.USER_ID;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getOptionalProfileEntity;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileEntity;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileEntityPatch;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileRequest;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileRequestMapPatch;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileResponse;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileResponsePatch;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @InjectMocks
    private ProfileServiceImpl service;

    @Mock
    private ProfileRepository repository;

    @Spy
    private final ProfileMapper mapper = Mappers.getMapper(ProfileMapper.class);

    @Spy
    private ObjectMapper objectMapper;

    @Captor
    private ArgumentCaptor<ProfileEntity> entityArgumentCaptor;

    @BeforeEach
    void init() {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
    }

    @Test
    void testGetProfileSuccessfully() {
        given(repository.findByUserId(anyString())).willReturn(getOptionalProfileEntity());

        var result = service.getProfile(USER_ID);

        assertThat(result).usingRecursiveComparison().isEqualTo(getProfileResponse());

        verify(repository).findByUserId(USER_ID);
        verify(mapper).entityToResponse(any(UserProfile.class));
    }

    @Test
    void testGetProfileThrowNotFoundException() {
        given(repository.findByUserId(anyString())).willReturn(Optional.empty());

        assertThatExceptionOfType(StandardException.class).isThrownBy(() -> service.getProfile(USER_ID))
                .hasFieldOrPropertyWithValue("errorCode", COMMON_ERROR_404_001)
                .hasFieldOrPropertyWithValue("args", new Object[]{USER_ID});

        verify(repository).findByUserId(USER_ID);
        verify(mapper, never()).entityToResponse(any(UserProfile.class));
    }

    @Test
    void testInsertProfileSuccessfully() {
        given(repository.findByUserId(anyString())).willReturn(Optional.empty());
        given(repository.insert(any(ProfileEntity.class))).willReturn(getProfileEntity());

        var result = service.insertProfile(USER_ID, getProfileRequest());

        assertThat(result).usingRecursiveComparison().isEqualTo(getProfileResponse());

        verify(repository).findByUserId(USER_ID);
        verify(mapper).requestToEntity(any(ProfileRequest.class));
        verify(repository).insert(entityArgumentCaptor.capture());
        verify(mapper).entityToResponse(any(UserProfile.class));

        assertThat(entityArgumentCaptor.getValue())
                .usingRecursiveComparison()
                .ignoringFields("id", "hashObject", "createdBy", "modifiedBy", "createdDateTime", "modifiedDateTime", "version")
                .isEqualTo(getProfileEntity());
    }

    @Test
    void testInsertProfileThrowConflictException() {
        given(repository.findByUserId(anyString())).willReturn(getOptionalProfileEntity());

        assertThatExceptionOfType(StandardException.class)
                .isThrownBy(() -> service.insertProfile(USER_ID, getProfileRequest()))
                .hasFieldOrPropertyWithValue("errorCode", COMMON_ERROR_409_001);

        verify(repository).findByUserId(USER_ID);
        verify(mapper, never()).requestToEntity(any(ProfileRequest.class));
        verify(repository, never()).insert(any(ProfileEntity.class));
        verify(mapper, never()).entityToResponse(any(UserProfile.class));
    }

    @Test
    void testUpdateProfileSuccessfully() {
        given(repository.findByUserId(anyString())).willReturn(getOptionalProfileEntity());

        var profileEntity = getProfileEntity();
        profileEntity.getProfile().setEstimatedAnnualIncome(NEW_ESTIMATED_ANNUAL_INCOME);
        profileEntity.getProfile().setEmploymentStatus(EmploymentStatusEnum.RETIRED);

        given(repository.save(any(ProfileEntity.class))).willReturn(profileEntity);

        var profileRequest = getProfileRequest();
        profileRequest.setEstimatedAnnualIncome(NEW_ESTIMATED_ANNUAL_INCOME);
        profileRequest.setEmploymentStatus(EmploymentStatusEnum.RETIRED);

        var result = service.updateProfile(USER_ID, profileRequest);

        assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("estimatedAnnualIncome", "employmentStatus")
                .isEqualTo(getProfileResponse());
        assertThat(result.getEstimatedAnnualIncome()).isEqualTo(NEW_ESTIMATED_ANNUAL_INCOME);
        assertThat(result.getEmploymentStatus()).isEqualTo(EmploymentStatusEnum.RETIRED);

        verify(repository).findByUserId(USER_ID);
        verify(mapper).requestToEntity(any(ProfileRequest.class));
        verify(repository).save(entityArgumentCaptor.capture());
        verify(mapper).entityToResponse(any(UserProfile.class));

        assertThat(entityArgumentCaptor.getValue())
                .usingRecursiveComparison()
                .ignoringFields("hashObject")
                .isEqualTo(profileEntity);
    }

    @Test
    void testUpdateProfileWhenMatchingProfileIsFound() {
        given(repository.findByUserId(anyString())).willReturn(getOptionalProfileEntity());

        var byteHandlerUtilsMockedStatic = Mockito.mockStatic(ByteHandlerUtils.class);
        byteHandlerUtilsMockedStatic.when(() -> ByteHandlerUtils.byteArrayComparison(any(), any())).thenReturn(true);

        var result = service.updateProfile(USER_ID, getProfileRequest());

        assertThat(result).usingRecursiveComparison().isEqualTo(getProfileResponse());

        verify(repository).findByUserId(USER_ID);
        verify(mapper).requestToEntity(any(ProfileRequest.class));
        verify(repository, never()).save(any(ProfileEntity.class));
        verify(mapper).entityToResponse(any(UserProfile.class));

        byteHandlerUtilsMockedStatic.close();
    }

    @Test
    void testUpdateProfileThrowNotFoundException() {
        given(repository.findByUserId(anyString())).willReturn(Optional.empty());

        assertThatExceptionOfType(StandardException.class)
                .isThrownBy(() -> service.updateProfile(USER_ID, getProfileRequest()))
                .hasFieldOrPropertyWithValue("errorCode", COMMON_ERROR_404_002)
                .hasFieldOrPropertyWithValue("args", new Object[]{USER_ID});

        verify(repository).findByUserId(USER_ID);
        verify(mapper, never()).requestToEntity(any(ProfileRequest.class));
        verify(repository, never()).save(any(ProfileEntity.class));
        verify(mapper, never()).entityToResponse(any(UserProfile.class));
    }

    @Test
    void testPatchProfileSuccessfully() throws JsonProcessingException {
        given(repository.findByUserId(anyString())).willReturn(getOptionalProfileEntity());
        given(repository.save(any(ProfileEntity.class))).willReturn(getProfileEntityPatch());

        var result = service.patchProfile(USER_ID, getProfileRequestMapPatch());

        assertThat(result).usingRecursiveComparison().isEqualTo(getProfileResponsePatch());

        verify(repository).findByUserId(USER_ID);
        verify(mapper).entityToRequest(any(UserProfile.class));
        verify(objectMapper, times(2)).writeValueAsString(any());
        verify(objectMapper, times(2)).readValue(anyString(), any(Class.class));
        verify(mapper).requestToEntity(any(ProfileRequest.class));
        verify(repository).save(entityArgumentCaptor.capture());
        verify(mapper).entityToResponse(any(UserProfile.class));

        assertThat(entityArgumentCaptor.getValue())
                .usingRecursiveComparison()
                .ignoringFields("hashObject")
                .isEqualTo(getProfileEntityPatch());
    }

    @Test
    void testPatchProfileWhenMatchingProfileIsFound() throws JsonProcessingException {
        given(repository.findByUserId(anyString())).willReturn(Optional.ofNullable(
                ProfileEntity.builder().profile(UserProfile.builder().firstName(FIRST_NAME).build()).build()));

        var byteHandlerUtilsMockedStatic = Mockito.mockStatic(ByteHandlerUtils.class);
        byteHandlerUtilsMockedStatic.when(() -> ByteHandlerUtils.byteArrayComparison(any(), any())).thenReturn(true);

        Map<String, Object> profileRequestMap = new LinkedHashMap<>() {{
            put("firstName", FIRST_NAME);
        }};

        var result = service.patchProfile(USER_ID, profileRequestMap);

        assertThat(result).usingRecursiveComparison().isEqualTo(
                ProfileResponse.builder().firstName(FIRST_NAME).employmentStatus(EmploymentStatusEnum.NOT_EMPLOYED).build());

        verify(repository).findByUserId(USER_ID);
        verify(mapper).entityToRequest(any(UserProfile.class));
        verify(objectMapper, times(2)).writeValueAsString(any());
        verify(objectMapper, times(2)).readValue(anyString(), any(Class.class));
        verify(mapper).requestToEntity(any(ProfileRequest.class));
        verify(repository, never()).save(any(ProfileEntity.class));
        verify(mapper).entityToResponse(any(UserProfile.class));

        byteHandlerUtilsMockedStatic.close();
    }

    @Test
    void testPatchProfileThrowBadRequestException() throws JsonProcessingException {
        assertThatExceptionOfType(StandardException.class)
                .isThrownBy(() -> service.patchProfile(USER_ID, new HashMap<>()))
                .hasFieldOrPropertyWithValue("errorCode", COMMON_ERROR_400_001)
                .hasFieldOrPropertyWithValue("args", new Object[]{
                        Collections.singletonList("request body is mandatory and must contain some attribute")});

        verify(repository, never()).findByUserId(USER_ID);
        verify(mapper, never()).entityToRequest(any(UserProfile.class));
        verify(objectMapper, never()).writeValueAsString(any());
        verify(objectMapper, never()).readValue(anyString(), any(Class.class));
        verify(mapper, never()).requestToEntity(any(ProfileRequest.class));
        verify(repository, never()).save(any(ProfileEntity.class));
        verify(mapper, never()).entityToResponse(any(UserProfile.class));
    }

    @Test
    void testPatchProfileThrowNotFoundException() throws JsonProcessingException {
        given(repository.findByUserId(anyString())).willReturn(Optional.empty());

        assertThatExceptionOfType(StandardException.class)
                .isThrownBy(() -> service.patchProfile(USER_ID, getProfileRequestMapPatch()))
                .hasFieldOrPropertyWithValue("errorCode", COMMON_ERROR_404_002)
                .hasFieldOrPropertyWithValue("args", new Object[]{USER_ID});

        verify(repository).findByUserId(USER_ID);
        verify(mapper, never()).entityToRequest(any(UserProfile.class));
        verify(objectMapper, never()).writeValueAsString(any());
        verify(objectMapper, never()).readValue(anyString(), any(Class.class));
        verify(mapper, never()).requestToEntity(any(ProfileRequest.class));
        verify(repository, never()).save(any(ProfileEntity.class));
        verify(mapper, never()).entityToResponse(any(UserProfile.class));
    }

    @Test
    void testPatchProfileWithEmptyObjectThrowUnprocessableEntityException() throws JsonProcessingException {
        given(repository.findByUserId(anyString())).willReturn(Optional.ofNullable(new ProfileEntity()));

        assertThatExceptionOfType(StandardException.class)
                .isThrownBy(() -> service.patchProfile(USER_ID, getProfileRequestMapPatch()))
                .hasFieldOrPropertyWithValue("errorCode", COMMON_ERROR_422_002);

        verify(repository).findByUserId(USER_ID);
        verify(mapper, never()).entityToRequest(any(UserProfile.class));
        verify(objectMapper, never()).writeValueAsString(any());
        verify(objectMapper, never()).readValue(anyString(), any(Class.class));
        verify(mapper, never()).requestToEntity(any(ProfileRequest.class));
        verify(repository, never()).save(any(ProfileEntity.class));
        verify(mapper, never()).entityToResponse(any(UserProfile.class));
    }

    @Test
    void testPatchProfileWithNonMapAttributesThrowUnprocessableEntityException() throws JsonProcessingException {
        given(repository.findByUserId(anyString())).willReturn(getOptionalProfileEntity());

        var profileRequestMap = getProfileRequestMapPatch();
        profileRequestMap.put("firstName", new LinkedHashMap<>());

        assertThatExceptionOfType(StandardException.class)
                .isThrownBy(() -> service.patchProfile(USER_ID, profileRequestMap))
                .withCauseExactlyInstanceOf(MismatchedInputException.class)
                .hasFieldOrPropertyWithValue("errorCode", COMMON_ERROR_422_001)
                .withMessageContaining("Cannot deserialize value of type");

        verify(repository).findByUserId(USER_ID);
        verify(mapper).entityToRequest(any(UserProfile.class));
        verify(objectMapper, times(2)).writeValueAsString(any());
        verify(objectMapper, times(2)).readValue(anyString(), any(Class.class));
        verify(mapper, never()).requestToEntity(any(ProfileRequest.class));
        verify(repository, never()).save(any(ProfileEntity.class));
        verify(mapper, never()).entityToResponse(any(UserProfile.class));
    }

    @Test
    void testPatchProfileWithInvalidRequestAttributesThrowUnprocessableEntityException() throws JsonProcessingException {
        given(repository.findByUserId(anyString())).willReturn(getOptionalProfileEntity());

        var profileRequestMap = getProfileRequestMapPatch();
        profileRequestMap.put("maritalStatus", MaritalStatusEnum.SINGLE.getValue());

        assertThatExceptionOfType(StandardException.class)
                .isThrownBy(() -> service.patchProfile(USER_ID, profileRequestMap))
                .hasFieldOrPropertyWithValue("errorCode", COMMON_ERROR_422_000)
                .hasFieldOrPropertyWithValue("args", new Object[]{
                        "[spouseProfile should not be filled in when marital status equals SINGLE, DIVORCE OR WIDOWED]"});

        verify(repository).findByUserId(USER_ID);
        verify(mapper).entityToRequest(any(UserProfile.class));
        verify(objectMapper, times(2)).writeValueAsString(any());
        verify(objectMapper, times(2)).readValue(anyString(), any(Class.class));
        verify(mapper, never()).requestToEntity(any(ProfileRequest.class));
        verify(repository, never()).save(any(ProfileEntity.class));
        verify(mapper, never()).entityToResponse(any(UserProfile.class));
    }

    @Test
    void testDeleteProfileSuccessfully() {
        given(repository.findByUserId(anyString())).willReturn(getOptionalProfileEntity());
        willDoNothing().given(repository).delete(any(ProfileEntity.class));

        service.deleteProfile(USER_ID);

        verify(repository).findByUserId(USER_ID);
        verify(repository).delete(any(ProfileEntity.class));
    }

    @Test
    void testDeleteProfileThrowNotFoundException() {
        given(repository.findByUserId(anyString())).willReturn(Optional.empty());

        assertThatExceptionOfType(StandardException.class)
                .isThrownBy(() -> service.deleteProfile(USER_ID))
                .hasFieldOrPropertyWithValue("errorCode", COMMON_ERROR_404_003)
                .hasFieldOrPropertyWithValue("args", new Object[]{USER_ID});

        verify(repository).findByUserId(USER_ID);
        verify(repository, never()).delete(any(ProfileEntity.class));
    }
}
