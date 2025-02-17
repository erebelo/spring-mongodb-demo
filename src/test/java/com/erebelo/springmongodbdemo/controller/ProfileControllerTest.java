package com.erebelo.springmongodbdemo.controller;

import static com.erebelo.springmongodbdemo.constant.BusinessConstant.MERGE_PATCH_MEDIA_TYPE;
import static com.erebelo.springmongodbdemo.constant.BusinessConstant.PROFILES_PATH;
import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_400_001;
import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_404_001;
import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_404_002;
import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_404_003;
import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_409_001;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.CONTACT_TYPE;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.NEW_CONTACT_VALUE;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.NEW_DATE_OF_BIRTH;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.NEW_HEALTH_LEVEL;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.USER_ID;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileRequest;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileRequestMapPatch;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileResponse;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileResponsePatch;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileResponsePatchResultMatcher;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileResponseResultMatcher;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.LIST;
import static org.assertj.core.api.InstanceOfAssertFactories.MAP;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.erebelo.springmongodbdemo.domain.request.ProfileRequest;
import com.erebelo.springmongodbdemo.exception.model.CommonException;
import com.erebelo.springmongodbdemo.service.ProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ProfileController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class ProfileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProfileService service;

    @Captor
    private ArgumentCaptor<ProfileRequest> profileRequestArgumentCaptor;

    @Captor
    private ArgumentCaptor<Map<String, Object>> mapArgumentCaptor;

    private static final String USER_ID_HEADER = "X-UserId";

    @Test
    void testGetProfileSuccessful() throws Exception {
        given(service.getProfile(anyString())).willReturn(getProfileResponse());

        mockMvc.perform(get(PROFILES_PATH).header(USER_ID_HEADER, USER_ID).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk()).andExpectAll(getProfileResponseResultMatcher());

        verify(service).getProfile(USER_ID);
    }

    @Test
    void testGetProfileNotFoundFailure() throws Exception {
        given(service.getProfile(anyString())).willThrow(new CommonException(COMMON_ERROR_404_001, USER_ID));

        mockMvc.perform(get(PROFILES_PATH).header(USER_ID_HEADER, USER_ID).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.code").value("COMMON-ERROR-404-001"))
                .andExpect(jsonPath("$.message").value("Object not found by userId: 12345"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        verify(service).getProfile(USER_ID);
    }

    @Test
    void testInsertProfileSuccessful() throws Exception {
        given(service.insertProfile(anyString(), any(ProfileRequest.class))).willReturn(getProfileResponse());

        mockMvc.perform(post(PROFILES_PATH).header(USER_ID_HEADER, USER_ID)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(getProfileRequest())).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated()).andExpectAll(getProfileResponseResultMatcher());

        verify(service).insertProfile(eq(USER_ID), profileRequestArgumentCaptor.capture());

        assertThat(profileRequestArgumentCaptor.getValue()).usingRecursiveComparison().isEqualTo(getProfileRequest());
    }

    @Test
    void testInsertProfileFailure() throws Exception {
        given(service.insertProfile(anyString(), any(ProfileRequest.class)))
                .willThrow(new CommonException(COMMON_ERROR_409_001));

        mockMvc.perform(post(PROFILES_PATH).header(USER_ID_HEADER, USER_ID)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(getProfileRequest())).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isConflict()).andExpect(jsonPath("$.status").value("CONFLICT"))
                .andExpect(jsonPath("$.code").value("COMMON-ERROR-409-001"))
                .andExpect(jsonPath("$.message")
                        .value("The object already exists. Try updating it instead of entering it"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        verify(service).insertProfile(eq(USER_ID), profileRequestArgumentCaptor.capture());

        assertThat(profileRequestArgumentCaptor.getValue()).usingRecursiveComparison().isEqualTo(getProfileRequest());
    }

    @Test
    void testUpdateProfileSuccessful() throws Exception {
        given(service.updateProfile(anyString(), any(ProfileRequest.class))).willReturn(getProfileResponse());

        mockMvc.perform(put(PROFILES_PATH).header(USER_ID_HEADER, USER_ID).contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(getProfileRequest())).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk()).andExpectAll(getProfileResponseResultMatcher());

        verify(service).updateProfile(eq(USER_ID), profileRequestArgumentCaptor.capture());

        assertThat(profileRequestArgumentCaptor.getValue()).usingRecursiveComparison().isEqualTo(getProfileRequest());
    }

    @Test
    void testUpdateProfileFailure() throws Exception {
        given(service.updateProfile(anyString(), any(ProfileRequest.class)))
                .willThrow(new CommonException(COMMON_ERROR_404_002, USER_ID));

        mockMvc.perform(put(PROFILES_PATH).header(USER_ID_HEADER, USER_ID).contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(getProfileRequest())).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.code").value("COMMON-ERROR-404-002"))
                .andExpect(jsonPath("$.message").value(String
                        .format("Object not found by userId: %s. Try entering it instead of updating it", USER_ID)))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        verify(service).updateProfile(eq(USER_ID), profileRequestArgumentCaptor.capture());

        assertThat(profileRequestArgumentCaptor.getValue()).usingRecursiveComparison().isEqualTo(getProfileRequest());
    }

    @Test
    void testPatchProfileSuccessful() throws Exception {
        given(service.patchProfile(anyString(), anyMap())).willReturn(getProfileResponsePatch());

        mockMvc.perform(patch(PROFILES_PATH).header(USER_ID_HEADER, USER_ID).contentType(MERGE_PATCH_MEDIA_TYPE)
                .content(objectMapper.writeValueAsString(getProfileRequestMapPatch()))
                .accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk())
                .andExpectAll(getProfileResponsePatchResultMatcher());

        verify(service).patchProfile(eq(USER_ID), mapArgumentCaptor.capture());

        assertPatchArgumentCaptorValues(mapArgumentCaptor.getValue());
    }

    @Test
    void testPatchProfileFailure() throws Exception {
        String errorMsg = "request body is mandatory and must contain some attribute";
        given(service.patchProfile(anyString(), anyMap()))
                .willThrow(new CommonException(COMMON_ERROR_400_001, Collections.singletonList(errorMsg)));

        mockMvc.perform(patch(PROFILES_PATH).header(USER_ID_HEADER, USER_ID).contentType(MERGE_PATCH_MEDIA_TYPE)
                .content(objectMapper.writeValueAsString(getProfileRequestMapPatch()))
                .accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("BAD_REQUEST"))
                .andExpect(jsonPath("$.code").value("COMMON-ERROR-400-001"))
                .andExpect(jsonPath("$.message", containsString(errorMsg)))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        verify(service).patchProfile(eq(USER_ID), mapArgumentCaptor.capture());

        assertPatchArgumentCaptorValues(mapArgumentCaptor.getValue());
    }

    @Test
    void testDeleteProfileSuccessful() throws Exception {
        willDoNothing().given(service).deleteProfile(anyString());

        mockMvc.perform(
                delete(PROFILES_PATH).header(USER_ID_HEADER, USER_ID).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent());

        verify(service).deleteProfile(USER_ID);
    }

    @Test
    void testDeleteProfileFailure() throws Exception {
        willThrow(new CommonException(COMMON_ERROR_404_003, USER_ID)).given(service).deleteProfile(anyString());

        mockMvc.perform(
                delete(PROFILES_PATH).header(USER_ID_HEADER, USER_ID).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.code").value("COMMON-ERROR-404-003"))
                .andExpect(jsonPath("$.message")
                        .value("The delete operation has not been completed as the object was not found by userId: "
                                + USER_ID))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        verify(service).deleteProfile(USER_ID);
    }

    private void assertPatchArgumentCaptorValues(Map<String, Object> profileRequestMap) {
        assertThat(profileRequestMap).usingRecursiveComparison()
                .ignoringFields("dateOfBirth", "healthLevel", "contactNumbers").isEqualTo(getProfileRequestMapPatch());

        assertThat(profileRequestMap).extracting("dateOfBirth", "healthLevel")
                .containsExactly(NEW_DATE_OF_BIRTH.toString(), NEW_HEALTH_LEVEL.getValue());

        assertThat(profileRequestMap).extracting("contactNumbers").asInstanceOf(LIST).first().asInstanceOf(MAP)
                .containsEntry("contactType", CONTACT_TYPE.getValue()).containsEntry("contactValue", NEW_CONTACT_VALUE);
    }
}
