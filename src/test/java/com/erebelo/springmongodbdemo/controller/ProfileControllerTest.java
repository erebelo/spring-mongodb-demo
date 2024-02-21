package com.erebelo.springmongodbdemo.controller;

import com.erebelo.springmongodbdemo.domain.request.ProfileRequest;
import com.erebelo.springmongodbdemo.domain.response.ProfileResponse;
import com.erebelo.springmongodbdemo.exception.StandardException;
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

import static com.erebelo.springmongodbdemo.constants.BusinessConstants.PROFILE;
import static com.erebelo.springmongodbdemo.exception.CommonErrorCodesEnum.COMMON_ERROR_409_001;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.USER_ID;
import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileResponse;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    private ArgumentCaptor<ProfileRequest> requestArgumentCaptor;

    @SuppressWarnings("all")
    private JacksonTester<ProfileRequest> requestJacksonTester;

    private static final String USER_ID_PARAM = "userId";
    private static final ProfileResponse RESPONSE = getProfileResponse();

    @BeforeEach
    void init() {
        var objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        JacksonTester.initFields(this, objectMapper);
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
        var exception = new StandardException(COMMON_ERROR_409_001);
        given(service.getProfile(anyString())).willThrow(exception);

        assertThatThrownBy(() -> mockMvc.perform(get(PROFILE)
                .param("userId", USER_ID)
                .accept(MediaType.APPLICATION_JSON_VALUE)))
                .hasCause(exception);
    }
}
