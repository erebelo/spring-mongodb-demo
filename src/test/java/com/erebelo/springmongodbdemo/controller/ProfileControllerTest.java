package com.erebelo.springmongodbdemo.controller;

import static com.erebelo.springmongodbdemo.constant.BusinessConstant.MERGE_PATCH_MEDIA_TYPE;
import static com.erebelo.springmongodbdemo.constant.BusinessConstant.PROFILE;
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
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileNotFoundFailureResultMatcher;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileRequest;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileRequestMapPatch;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileResponse;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileResponsePatch;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileResponsePatchResultMatcher;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileResponseResultMatcher;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.erebelo.springmongodbdemo.domain.request.ProfileRequest;
import com.erebelo.springmongodbdemo.exception.GlobalExceptionHandler;
import com.erebelo.springmongodbdemo.exception.model.CommonException;
import com.erebelo.springmongodbdemo.service.ProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private ProfileController controller;

    @Mock
    private ProfileService service;

    @Mock
    private Environment env;

    @Captor
    private ArgumentCaptor<?> argumentCaptor;

    @SuppressWarnings("all")
    private JacksonTester<ProfileRequest> requestJacksonTester;

    @SuppressWarnings("all")
    private JacksonTester<Map> mapJacksonTester;

    private static final String USER_ID_PARAM = "userId";

    @BeforeEach
    void init() {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        JacksonTester.initFields(this, objectMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler(env))
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper)).build();
    }

    @Test
    void testGetProfileSuccessfully() throws Exception {
        given(service.getProfile(anyString())).willReturn(getProfileResponse());

        mockMvc.perform(get(PROFILE).param(USER_ID_PARAM, USER_ID).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk()).andExpectAll(getProfileResponseResultMatcher()).andReturn();

        verify(service).getProfile(USER_ID);
    }

    @Test
    void testGetProfileNotFoundFailure() throws Exception {
        given(service.getProfile(anyString())).willThrow(new CommonException(COMMON_ERROR_404_001, USER_ID));
        given(env.getProperty(anyString())).willReturn("404|Object not found by userId: %s");

        mockMvc.perform(get(PROFILE).param(USER_ID_PARAM, USER_ID).accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound()).andExpectAll(getProfileNotFoundFailureResultMatcher());

        verify(service).getProfile(USER_ID);
        verify(env).getProperty("COMMON-ERROR-404-001");
    }

    @Test
    void testInsertProfileSuccessfully() throws Exception {
        given(service.insertProfile(anyString(), any(ProfileRequest.class))).willReturn(getProfileResponse());

        mockMvc.perform(post(PROFILE).param(USER_ID_PARAM, USER_ID).contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestJacksonTester.write(getProfileRequest()).getJson())
                .accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isCreated())
                .andExpectAll(getProfileResponseResultMatcher());

        verify(service).insertProfile(eq(USER_ID), (ProfileRequest) argumentCaptor.capture());

        assertThat(argumentCaptor.getValue()).usingRecursiveComparison().isEqualTo(getProfileRequest());
    }

    @Test
    void testInsertProfileFailure() { // TODO fix it
        var exception = new CommonException(COMMON_ERROR_409_001);
        given(service.insertProfile(anyString(), any(ProfileRequest.class))).willThrow(exception);

        assertThatThrownBy(() -> mockMvc
                .perform(post(PROFILE).param(USER_ID_PARAM, USER_ID).contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJacksonTester.write(getProfileRequest()).getJson())
                        .accept(MediaType.APPLICATION_JSON_VALUE)))
                .hasCause(exception);

        verify(service).insertProfile(eq(USER_ID), (ProfileRequest) argumentCaptor.capture());

        assertThat(argumentCaptor.getValue()).usingRecursiveComparison().isEqualTo(getProfileRequest());
    }

    @Test
    void testUpdateProfileSuccessfully() throws Exception {
        given(service.updateProfile(anyString(), any(ProfileRequest.class))).willReturn(getProfileResponse());

        mockMvc.perform(put(PROFILE).param(USER_ID_PARAM, USER_ID).contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestJacksonTester.write(getProfileRequest()).getJson())
                .accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk())
                .andExpectAll(getProfileResponseResultMatcher());

        verify(service).updateProfile(eq(USER_ID), (ProfileRequest) argumentCaptor.capture());

        assertThat(argumentCaptor.getValue()).usingRecursiveComparison().isEqualTo(getProfileRequest());
    }

    @Test
    void testUpdateProfileFailure() { // TODO fix it
        var exception = new CommonException(COMMON_ERROR_404_002, USER_ID);
        given(service.updateProfile(anyString(), any(ProfileRequest.class))).willThrow(exception);

        assertThatThrownBy(() -> mockMvc
                .perform(put(PROFILE).param(USER_ID_PARAM, USER_ID).contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJacksonTester.write(getProfileRequest()).getJson())
                        .accept(MediaType.APPLICATION_JSON_VALUE)))
                .hasCause(exception);

        verify(service).updateProfile(eq(USER_ID), (ProfileRequest) argumentCaptor.capture());

        assertThat(argumentCaptor.getValue()).usingRecursiveComparison().isEqualTo(getProfileRequest());
    }

    @Test
    void testPatchProfileSuccessfully() throws Exception {
        given(service.patchProfile(anyString(), any(Map.class))).willReturn(getProfileResponsePatch());

        mockMvc.perform(patch(PROFILE).param(USER_ID_PARAM, USER_ID).contentType(MERGE_PATCH_MEDIA_TYPE)
                .content(mapJacksonTester.write(getProfileRequestMapPatch()).getJson())
                .accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk())
                .andExpectAll(getProfileResponsePatchResultMatcher());

        verify(service).patchProfile(eq(USER_ID), (Map<String, Object>) argumentCaptor.capture());

        assertPatchArgumentCaptorValues((Map<String, Object>) argumentCaptor.getValue());
    }

    @Test
    void testPatchProfileFailure() { // TODO fix it
        var exception = new CommonException(COMMON_ERROR_400_001,
                Collections.singletonList("request body is mandatory and must contain some attribute"));
        given(service.patchProfile(anyString(), any(Map.class))).willThrow(exception);

        assertThatThrownBy(
                () -> mockMvc.perform(patch(PROFILE).param(USER_ID_PARAM, USER_ID).contentType(MERGE_PATCH_MEDIA_TYPE)
                        .content(mapJacksonTester.write(getProfileRequestMapPatch()).getJson())
                        .accept(MediaType.APPLICATION_JSON_VALUE)))
                .hasCause(exception);

        verify(service).patchProfile(eq(USER_ID), (Map<String, Object>) argumentCaptor.capture());

        assertPatchArgumentCaptorValues((Map<String, Object>) argumentCaptor.getValue());
    }

    @Test
    void testDeleteProfileSuccessfully() throws Exception {
        willDoNothing().given(service).deleteProfile(anyString());

        mockMvc.perform(delete(PROFILE).param(USER_ID_PARAM, USER_ID).contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent());

        verify(service).deleteProfile(USER_ID);
    }

    @Test
    void testDeleteProfileFailure() { // TODO fix it
        var exception = new CommonException(COMMON_ERROR_404_003, USER_ID);
        willThrow(exception).given(service).deleteProfile(anyString());

        assertThatThrownBy(() -> mockMvc
                .perform(delete(PROFILE).param(USER_ID_PARAM, USER_ID).contentType(MediaType.APPLICATION_JSON_VALUE)))
                .hasCause(exception);

        verify(service).deleteProfile(USER_ID);
    }

    private void assertPatchArgumentCaptorValues(Map<String, Object> profileRequestMap) {
        assertThat(profileRequestMap).usingRecursiveComparison()
                .ignoringFields("dateOfBirth", "healthLevel", "contactNumbers").isEqualTo(getProfileRequestMapPatch());
        assertThat(profileRequestMap.get("dateOfBirth")).isEqualTo(NEW_DATE_OF_BIRTH.toString());
        assertThat(profileRequestMap.get("healthLevel")).isEqualTo(NEW_HEALTH_LEVEL.getValue());

        var contactNumbers = (List<Map<String, Object>>) profileRequestMap.get("contactNumbers");
        assertThat(contactNumbers.get(0).get("contactType")).isEqualTo(CONTACT_TYPE.getValue());
        assertThat(contactNumbers.get(0).get("contactValue")).isEqualTo(NEW_CONTACT_VALUE);
    }
}
