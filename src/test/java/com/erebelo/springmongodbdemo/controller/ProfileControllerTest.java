package com.erebelo.springmongodbdemo.controller;

import com.erebelo.springmongodbdemo.domain.request.ProfileRequest;
import com.erebelo.springmongodbdemo.domain.response.ProfileResponse;
import com.erebelo.springmongodbdemo.exception.model.CommonException;
import com.erebelo.springmongodbdemo.service.ProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Map;

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
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileRequest;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileRequestMapPatch;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileResponse;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileResponsePatch;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private ProfileController controller;

    @Mock
    private ProfileService service;

    @Captor
    private ArgumentCaptor<?> argumentCaptor;

    @SuppressWarnings("all")
    private JacksonTester<ProfileRequest> requestJacksonTester;

    @SuppressWarnings("all")
    private JacksonTester<Map> mapJacksonTester;

    private static final String USER_ID_PARAM = "userId";
    private static final ProfileResponse RESPONSE = getProfileResponse();
    private static final ProfileResponse PATCH_RESPONSE = getProfileResponsePatch();

    @BeforeEach
    void init() {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        JacksonTester.initFields(this, objectMapper);
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objectMapper))
                .build();
    }

    @Test
    void testGetProfileSuccessfully() throws Exception {
        given(service.getProfile(anyString())).willReturn(RESPONSE);

        mockMvc.perform(get(PROFILE)
                        .param(USER_ID_PARAM, USER_ID)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(RESPONSE.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(RESPONSE.getLastName()))
                .andExpect(jsonPath("$.dateOfBirth").value(RESPONSE.getDateOfBirth().toString()))
                .andExpect(jsonPath("$.numberOfDependents").value(RESPONSE.getNumberOfDependents()))
                .andExpect(jsonPath("$.estimatedAnnualIncome").value(RESPONSE.getEstimatedAnnualIncome()))
                .andExpect(jsonPath("$.estimatedNetWorth").value(RESPONSE.getEstimatedNetWorth()))
                .andExpect(jsonPath("$.gender").value(RESPONSE.getGender().getValue()))
                .andExpect(jsonPath("$.maritalStatus").value(RESPONSE.getMaritalStatus().getValue()))
                .andExpect(jsonPath("$.employmentStatus").value(RESPONSE.getEmploymentStatus().getValue()))
                .andExpect(jsonPath("$.healthLevel").value(RESPONSE.getHealthLevel().getValue()))
                .andExpect(jsonPath("$.contactNumbers[0].contactType").value(RESPONSE.getContactNumbers().get(0).getContactType().getValue()))
                .andExpect(jsonPath("$.contactNumbers[0].contactValue").value(RESPONSE.getContactNumbers().get(0).getContactValue()))
                .andExpect(jsonPath("$.currentLocation.address").value(RESPONSE.getCurrentLocation().getAddress()))
                .andExpect(jsonPath("$.currentLocation.city").value(RESPONSE.getCurrentLocation().getCity()))
                .andExpect(jsonPath("$.currentLocation.state").value(RESPONSE.getCurrentLocation().getState()))
                .andExpect(jsonPath("$.currentLocation.country").value(RESPONSE.getCurrentLocation().getCountry()))
                .andExpect(jsonPath("$.currentLocation.postalCode").value(RESPONSE.getCurrentLocation().getPostalCode()))
                .andExpect(jsonPath("$.spouseProfile.firstName").value(RESPONSE.getSpouseProfile().getFirstName()))
                .andExpect(jsonPath("$.spouseProfile.lastName").value(RESPONSE.getSpouseProfile().getLastName()))
                .andExpect(jsonPath("$.spouseProfile.dateOfBirth").value(RESPONSE.getSpouseProfile().getDateOfBirth().toString()))
                .andExpect(jsonPath("$.spouseProfile.gender").value(RESPONSE.getSpouseProfile().getGender().getValue()))
                .andExpect(jsonPath("$.spouseProfile.employmentStatus").value(RESPONSE.getSpouseProfile().getEmploymentStatus().getValue()))
                .andReturn();

        verify(service).getProfile(USER_ID);
    }

    @Test
    void testGetProfileFailure() {
        var exception = new CommonException(COMMON_ERROR_404_001, USER_ID);
        given(service.getProfile(anyString())).willThrow(exception);

        assertThatThrownBy(() -> mockMvc.perform(get(PROFILE)
                .param("userId", USER_ID)
                .accept(MediaType.APPLICATION_JSON_VALUE)))
                .hasCause(exception);

        verify(service).getProfile(USER_ID);
    }

    @Test
    void testInsertProfileSuccessfully() throws Exception {
        given(service.insertProfile(anyString(), any(ProfileRequest.class))).willReturn(RESPONSE);

        mockMvc.perform(post(PROFILE)
                        .param(USER_ID_PARAM, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJacksonTester.write(getProfileRequest()).getJson())
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value(RESPONSE.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(RESPONSE.getLastName()))
                .andExpect(jsonPath("$.dateOfBirth").value(RESPONSE.getDateOfBirth().toString()))
                .andExpect(jsonPath("$.numberOfDependents").value(RESPONSE.getNumberOfDependents()))
                .andExpect(jsonPath("$.estimatedAnnualIncome").value(RESPONSE.getEstimatedAnnualIncome()))
                .andExpect(jsonPath("$.estimatedNetWorth").value(RESPONSE.getEstimatedNetWorth()))
                .andExpect(jsonPath("$.gender").value(RESPONSE.getGender().getValue()))
                .andExpect(jsonPath("$.maritalStatus").value(RESPONSE.getMaritalStatus().getValue()))
                .andExpect(jsonPath("$.employmentStatus").value(RESPONSE.getEmploymentStatus().getValue()))
                .andExpect(jsonPath("$.healthLevel").value(RESPONSE.getHealthLevel().getValue()))
                .andExpect(jsonPath("$.contactNumbers[0].contactType").value(RESPONSE.getContactNumbers().get(0).getContactType().getValue()))
                .andExpect(jsonPath("$.contactNumbers[0].contactValue").value(RESPONSE.getContactNumbers().get(0).getContactValue()))
                .andExpect(jsonPath("$.currentLocation.address").value(RESPONSE.getCurrentLocation().getAddress()))
                .andExpect(jsonPath("$.currentLocation.city").value(RESPONSE.getCurrentLocation().getCity()))
                .andExpect(jsonPath("$.currentLocation.state").value(RESPONSE.getCurrentLocation().getState()))
                .andExpect(jsonPath("$.currentLocation.country").value(RESPONSE.getCurrentLocation().getCountry()))
                .andExpect(jsonPath("$.currentLocation.postalCode").value(RESPONSE.getCurrentLocation().getPostalCode()))
                .andExpect(jsonPath("$.spouseProfile.firstName").value(RESPONSE.getSpouseProfile().getFirstName()))
                .andExpect(jsonPath("$.spouseProfile.lastName").value(RESPONSE.getSpouseProfile().getLastName()))
                .andExpect(jsonPath("$.spouseProfile.dateOfBirth").value(RESPONSE.getSpouseProfile().getDateOfBirth().toString()))
                .andExpect(jsonPath("$.spouseProfile.gender").value(RESPONSE.getSpouseProfile().getGender().getValue()))
                .andExpect(jsonPath("$.spouseProfile.employmentStatus").value(RESPONSE.getSpouseProfile().getEmploymentStatus().getValue()))
                .andReturn();

        verify(service).insertProfile(eq(USER_ID), (ProfileRequest) argumentCaptor.capture());

        assertThat(argumentCaptor.getValue()).usingRecursiveComparison().isEqualTo(getProfileRequest());
    }

    @Test
    void testInsertProfileFailure() {
        var exception = new CommonException(COMMON_ERROR_409_001);
        given(service.insertProfile(anyString(), any(ProfileRequest.class))).willThrow(exception);

        assertThatThrownBy(() -> mockMvc.perform(post(PROFILE)
                .param(USER_ID_PARAM, USER_ID)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestJacksonTester.write(getProfileRequest()).getJson())
                .accept(MediaType.APPLICATION_JSON_VALUE)))
                .hasCause(exception);

        verify(service).insertProfile(eq(USER_ID), (ProfileRequest) argumentCaptor.capture());

        assertThat(argumentCaptor.getValue()).usingRecursiveComparison().isEqualTo(getProfileRequest());
    }

    @Test
    void testUpdateProfileSuccessfully() throws Exception {
        given(service.updateProfile(anyString(), any(ProfileRequest.class))).willReturn(RESPONSE);

        mockMvc.perform(put(PROFILE)
                        .param(USER_ID_PARAM, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(requestJacksonTester.write(getProfileRequest()).getJson())
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(RESPONSE.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(RESPONSE.getLastName()))
                .andExpect(jsonPath("$.dateOfBirth").value(RESPONSE.getDateOfBirth().toString()))
                .andExpect(jsonPath("$.numberOfDependents").value(RESPONSE.getNumberOfDependents()))
                .andExpect(jsonPath("$.estimatedAnnualIncome").value(RESPONSE.getEstimatedAnnualIncome()))
                .andExpect(jsonPath("$.estimatedNetWorth").value(RESPONSE.getEstimatedNetWorth()))
                .andExpect(jsonPath("$.gender").value(RESPONSE.getGender().getValue()))
                .andExpect(jsonPath("$.maritalStatus").value(RESPONSE.getMaritalStatus().getValue()))
                .andExpect(jsonPath("$.employmentStatus").value(RESPONSE.getEmploymentStatus().getValue()))
                .andExpect(jsonPath("$.healthLevel").value(RESPONSE.getHealthLevel().getValue()))
                .andExpect(jsonPath("$.contactNumbers[0].contactType").value(RESPONSE.getContactNumbers().get(0).getContactType().getValue()))
                .andExpect(jsonPath("$.contactNumbers[0].contactValue").value(RESPONSE.getContactNumbers().get(0).getContactValue()))
                .andExpect(jsonPath("$.currentLocation.address").value(RESPONSE.getCurrentLocation().getAddress()))
                .andExpect(jsonPath("$.currentLocation.city").value(RESPONSE.getCurrentLocation().getCity()))
                .andExpect(jsonPath("$.currentLocation.state").value(RESPONSE.getCurrentLocation().getState()))
                .andExpect(jsonPath("$.currentLocation.country").value(RESPONSE.getCurrentLocation().getCountry()))
                .andExpect(jsonPath("$.currentLocation.postalCode").value(RESPONSE.getCurrentLocation().getPostalCode()))
                .andExpect(jsonPath("$.spouseProfile.firstName").value(RESPONSE.getSpouseProfile().getFirstName()))
                .andExpect(jsonPath("$.spouseProfile.lastName").value(RESPONSE.getSpouseProfile().getLastName()))
                .andExpect(jsonPath("$.spouseProfile.dateOfBirth").value(RESPONSE.getSpouseProfile().getDateOfBirth().toString()))
                .andExpect(jsonPath("$.spouseProfile.gender").value(RESPONSE.getSpouseProfile().getGender().getValue()))
                .andExpect(jsonPath("$.spouseProfile.employmentStatus").value(RESPONSE.getSpouseProfile().getEmploymentStatus().getValue()))
                .andReturn();

        verify(service).updateProfile(eq(USER_ID), (ProfileRequest) argumentCaptor.capture());

        assertThat(argumentCaptor.getValue()).usingRecursiveComparison().isEqualTo(getProfileRequest());
    }

    @Test
    void testUpdateProfileFailure() {
        var exception = new CommonException(COMMON_ERROR_404_002, USER_ID);
        given(service.updateProfile(anyString(), any(ProfileRequest.class))).willThrow(exception);

        assertThatThrownBy(() -> mockMvc.perform(put(PROFILE)
                .param(USER_ID_PARAM, USER_ID)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestJacksonTester.write(getProfileRequest()).getJson())
                .accept(MediaType.APPLICATION_JSON_VALUE)))
                .hasCause(exception);

        verify(service).updateProfile(eq(USER_ID), (ProfileRequest) argumentCaptor.capture());

        assertThat(argumentCaptor.getValue()).usingRecursiveComparison().isEqualTo(getProfileRequest());
    }

    @Test
    void testPatchProfileSuccessfully() throws Exception {
        given(service.patchProfile(anyString(), any(Map.class))).willReturn(PATCH_RESPONSE);

        mockMvc.perform(patch(PROFILE)
                        .param(USER_ID_PARAM, USER_ID)
                        .contentType(MERGE_PATCH_MEDIA_TYPE)
                        .content(mapJacksonTester.write(getProfileRequestMapPatch()).getJson())
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value(PATCH_RESPONSE.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(PATCH_RESPONSE.getLastName()))
                .andExpect(jsonPath("$.dateOfBirth").value(PATCH_RESPONSE.getDateOfBirth().toString()))
                .andExpect(jsonPath("$.numberOfDependents").value(PATCH_RESPONSE.getNumberOfDependents()))
                .andExpect(jsonPath("$.estimatedAnnualIncome").value(PATCH_RESPONSE.getEstimatedAnnualIncome()))
                .andExpect(jsonPath("$.estimatedNetWorth").value(PATCH_RESPONSE.getEstimatedNetWorth()))
                .andExpect(jsonPath("$.gender").value(PATCH_RESPONSE.getGender().getValue()))
                .andExpect(jsonPath("$.maritalStatus").value(PATCH_RESPONSE.getMaritalStatus().getValue()))
                .andExpect(jsonPath("$.employmentStatus").value(PATCH_RESPONSE.getEmploymentStatus().getValue()))
                .andExpect(jsonPath("$.healthLevel").value(PATCH_RESPONSE.getHealthLevel().getValue()))
                .andExpect(jsonPath("$.contactNumbers[0].contactType").value(PATCH_RESPONSE.getContactNumbers().get(0).getContactType().getValue()))
                .andExpect(jsonPath("$.contactNumbers[0].contactValue").value(PATCH_RESPONSE.getContactNumbers().get(0).getContactValue()))
                .andExpect(jsonPath("$.currentLocation.address").value(PATCH_RESPONSE.getCurrentLocation().getAddress()))
                .andExpect(jsonPath("$.currentLocation.city").value(PATCH_RESPONSE.getCurrentLocation().getCity()))
                .andExpect(jsonPath("$.currentLocation.state").value(PATCH_RESPONSE.getCurrentLocation().getState()))
                .andExpect(jsonPath("$.currentLocation.country").value(PATCH_RESPONSE.getCurrentLocation().getCountry()))
                .andExpect(jsonPath("$.currentLocation.postalCode").doesNotExist())
                .andExpect(jsonPath("$.spouseProfile.firstName").value(PATCH_RESPONSE.getSpouseProfile().getFirstName()))
                .andExpect(jsonPath("$.spouseProfile.lastName").value(PATCH_RESPONSE.getSpouseProfile().getLastName()))
                .andExpect(jsonPath("$.spouseProfile.dateOfBirth").value(PATCH_RESPONSE.getSpouseProfile().getDateOfBirth().toString()))
                .andExpect(jsonPath("$.spouseProfile.gender").value(PATCH_RESPONSE.getSpouseProfile().getGender().getValue()))
                .andExpect(jsonPath("$.spouseProfile.employmentStatus").value(PATCH_RESPONSE.getSpouseProfile().getEmploymentStatus().getValue()))
                .andReturn();

        verify(service).patchProfile(eq(USER_ID), (Map<String, Object>) argumentCaptor.capture());

        assertPatchArgumentCaptorValues((Map<String, Object>) argumentCaptor.getValue());
    }

    @Test
    void testPatchProfileFailure() {
        var exception = new CommonException(COMMON_ERROR_400_001,
                Collections.singletonList("request body is mandatory and must contain some attribute"));
        given(service.patchProfile(anyString(), any(Map.class))).willThrow(exception);

        assertThatThrownBy(() -> mockMvc.perform(patch(PROFILE)
                .param(USER_ID_PARAM, USER_ID)
                .contentType(MERGE_PATCH_MEDIA_TYPE)
                .content(mapJacksonTester.write(getProfileRequestMapPatch()).getJson())
                .accept(MediaType.APPLICATION_JSON_VALUE)))
                .hasCause(exception);

        verify(service).patchProfile(eq(USER_ID), (Map<String, Object>) argumentCaptor.capture());

        assertPatchArgumentCaptorValues((Map<String, Object>) argumentCaptor.getValue());
    }

    @Test
    void testDeleteProfileSuccessfully() throws Exception {
        willDoNothing().given(service).deleteProfile(anyString());

        mockMvc.perform(delete(PROFILE)
                        .param(USER_ID_PARAM, USER_ID)
                        .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent())
                .andReturn();

        verify(service).deleteProfile(USER_ID);
    }

    @Test
    void testDeleteProfileFailure() {
        var exception = new CommonException(COMMON_ERROR_404_003, USER_ID);
        willThrow(exception).given(service).deleteProfile(anyString());

        assertThatThrownBy(() -> mockMvc.perform(delete(PROFILE)
                .param(USER_ID_PARAM, USER_ID)
                .contentType(MediaType.APPLICATION_JSON_VALUE)))
                .hasCause(exception);

        verify(service).deleteProfile(USER_ID);
    }

    private void assertPatchArgumentCaptorValues(Map<String, Object> profileRequestMap) {
        assertThat(profileRequestMap)
                .usingRecursiveComparison()
                .ignoringFields("dateOfBirth", "healthLevel", "contactNumbers")
                .isEqualTo(getProfileRequestMapPatch());
        assertThat(profileRequestMap.get("dateOfBirth")).isEqualTo(NEW_DATE_OF_BIRTH.toString());
        assertThat(profileRequestMap.get("healthLevel")).isEqualTo(NEW_HEALTH_LEVEL.getValue());

        var contactNumbers = (List<Map<String, Object>>) profileRequestMap.get("contactNumbers");
        assertThat(contactNumbers.get(0).get("contactType")).isEqualTo(CONTACT_TYPE.getValue());
        assertThat(contactNumbers.get(0).get("contactValue")).isEqualTo(NEW_CONTACT_VALUE);
    }
}
