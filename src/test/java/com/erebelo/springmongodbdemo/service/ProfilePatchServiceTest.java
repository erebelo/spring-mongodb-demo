package com.erebelo.springmongodbdemo.service;

import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_400_001;
import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_404_002;
import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_422_000;
import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_422_001;
import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_422_002;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.FIRST_NAME;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.USER_ID;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getOptionalProfileEntity;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileRequestMapPatch;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileResponsePatch;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import com.erebelo.springmongodbdemo.domain.entity.ProfileEntity;
import com.erebelo.springmongodbdemo.domain.entity.UserProfile;
import com.erebelo.springmongodbdemo.domain.enumeration.EmploymentStatusEnum;
import com.erebelo.springmongodbdemo.domain.enumeration.MaritalStatusEnum;
import com.erebelo.springmongodbdemo.domain.request.ProfileRequest;
import com.erebelo.springmongodbdemo.domain.response.ProfileResponse;
import com.erebelo.springmongodbdemo.exception.model.CommonException;
import com.erebelo.springmongodbdemo.mapper.ProfileMapper;
import com.erebelo.springmongodbdemo.repository.ProfileRepository;
import com.erebelo.springmongodbdemo.service.impl.ProfilePatchServiceImpl;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProfilePatchServiceTest {

    @InjectMocks
    private ProfilePatchServiceImpl profilePatchService;

    @Mock
    private ProfileService profileService;

    @Mock
    private ProfileRepository repository;

    @Spy
    private final ProfileMapper mapper = Mappers.getMapper(ProfileMapper.class);

    @Test
    void testPatchProfileSuccessful() {
        given(repository.findByUserId(anyString())).willReturn(getOptionalProfileEntity());
        given(profileService.updateProfile(anyString(), any(ProfileRequest.class)))
                .willReturn(getProfileResponsePatch());

        ProfileResponse response = profilePatchService.patchProfile(USER_ID, getProfileRequestMapPatch());

        assertThat(response).usingRecursiveComparison().isEqualTo(getProfileResponsePatch());

        verify(repository).findByUserId(USER_ID);
        verify(mapper).entityToRequest(any(UserProfile.class));
        verify(profileService).updateProfile(eq(USER_ID), any(ProfileRequest.class));
        verify(mapper, never()).entityToResponse(any(UserProfile.class));
    }

    @Test
    void testPatchProfileWhenMatchingProfileIsFound() {
        given(repository.findByUserId(anyString())).willReturn(Optional.ofNullable(
                ProfileEntity.builder().profile(UserProfile.builder().firstName(FIRST_NAME).build()).build()));

        Map<String, Object> profileRequestMap = new LinkedHashMap<>() {
            {
                put("firstName", FIRST_NAME);
            }
        };

        ProfileResponse response = profilePatchService.patchProfile(USER_ID, profileRequestMap);

        assertThat(response).usingRecursiveComparison().isEqualTo(ProfileResponse.builder().firstName(FIRST_NAME)
                .employmentStatus(EmploymentStatusEnum.NOT_EMPLOYED).build());

        verify(repository).findByUserId(USER_ID);
        verify(mapper).entityToRequest(any(UserProfile.class));
        verify(profileService, never()).updateProfile(eq(USER_ID), any(ProfileRequest.class));
        verify(mapper).entityToResponse(any(UserProfile.class));
    }

    @Test
    void testPatchProfileThrowsBadRequestException() {
        Map<String, Object> map = new HashMap<>();

        CommonException exception = assertThrows(CommonException.class,
                () -> profilePatchService.patchProfile(USER_ID, map));

        assertEquals(COMMON_ERROR_400_001, exception.getErrorCode());
        assertArrayEquals(
                new Object[]{Collections.singletonList("request body is mandatory and must contain some attribute")},
                exception.getArgs());

        verify(repository, never()).findByUserId(USER_ID);
        verify(mapper, never()).entityToRequest(any(UserProfile.class));
        verify(profileService, never()).updateProfile(anyString(), any(ProfileRequest.class));
        verify(mapper, never()).entityToResponse(any(UserProfile.class));
    }

    @Test
    void testPatchProfileThrowsNotFoundException() {
        given(repository.findByUserId(anyString())).willReturn(Optional.empty());
        Map<String, Object> map = getProfileRequestMapPatch();

        CommonException exception = assertThrows(CommonException.class,
                () -> profilePatchService.patchProfile(USER_ID, map));

        assertEquals(COMMON_ERROR_404_002, exception.getErrorCode());
        assertArrayEquals(new Object[]{USER_ID}, exception.getArgs());

        verify(repository).findByUserId(USER_ID);
        verify(mapper, never()).entityToRequest(any(UserProfile.class));
        verify(profileService, never()).updateProfile(anyString(), any(ProfileRequest.class));
        verify(mapper, never()).entityToResponse(any(UserProfile.class));
    }

    @Test
    void testPatchProfileWithEmptyObjectThrowsUnprocessableEntityException() {
        given(repository.findByUserId(anyString())).willReturn(Optional.of(new ProfileEntity()));
        Map<String, Object> map = getProfileRequestMapPatch();

        CommonException exception = assertThrows(CommonException.class,
                () -> profilePatchService.patchProfile(USER_ID, map));

        assertEquals(COMMON_ERROR_422_002, exception.getErrorCode());

        verify(repository).findByUserId(USER_ID);
        verify(mapper, never()).entityToRequest(any(UserProfile.class));
        verify(profileService, never()).updateProfile(anyString(), any(ProfileRequest.class));
        verify(mapper, never()).entityToResponse(any(UserProfile.class));
    }

    @Test
    void testPatchProfileWithNonMapAttributesThrowsUnprocessableEntityException() {
        given(repository.findByUserId(anyString())).willReturn(getOptionalProfileEntity());

        Map<String, Object> profileRequestMap = getProfileRequestMapPatch();
        profileRequestMap.put("firstName", new LinkedHashMap<>());

        CommonException exception = assertThrows(CommonException.class,
                () -> profilePatchService.patchProfile(USER_ID, profileRequestMap));

        assertInstanceOf(MismatchedInputException.class, exception.getCause());
        assertEquals(COMMON_ERROR_422_001, exception.getErrorCode());
        assertTrue(exception.getMessage().contains("Cannot deserialize value of type"));

        verify(repository).findByUserId(USER_ID);
        verify(mapper).entityToRequest(any(UserProfile.class));
        verify(profileService, never()).updateProfile(anyString(), any(ProfileRequest.class));
        verify(mapper, never()).entityToResponse(any(UserProfile.class));
    }

    @Test
    void testPatchProfileWithInvalidRequestAttributesThrowsUnprocessableEntityException() {
        given(repository.findByUserId(anyString())).willReturn(getOptionalProfileEntity());

        Map<String, Object> profileRequestMap = getProfileRequestMapPatch();
        profileRequestMap.put("maritalStatus", MaritalStatusEnum.SINGLE.getValue());

        CommonException exception = assertThrows(CommonException.class,
                () -> profilePatchService.patchProfile(USER_ID, profileRequestMap));

        assertEquals(COMMON_ERROR_422_000, exception.getErrorCode());
        assertArrayEquals(new Object[]{
                "[spouseProfile should not be filled in when marital status equals SINGLE, DIVORCED, or " + "WIDOWED]"},
                exception.getArgs());

        verify(repository).findByUserId(USER_ID);
        verify(mapper).entityToRequest(any(UserProfile.class));
        verify(profileService, never()).updateProfile(anyString(), any(ProfileRequest.class));
        verify(mapper, never()).entityToResponse(any(UserProfile.class));
    }
}
