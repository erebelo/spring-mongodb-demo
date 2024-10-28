package com.erebelo.springmongodbdemo.controller;

import static com.erebelo.springmongodbdemo.constant.BusinessConstant.WIKIMEDIA;
import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_404_004;
import static com.erebelo.springmongodbdemo.mock.WikimediaMock.getWikimediaResponse;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.erebelo.springmongodbdemo.domain.response.WikimediaResponse;
import com.erebelo.springmongodbdemo.exception.model.CommonException;
import com.erebelo.springmongodbdemo.service.WikimediaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = WikimediaController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class WikimediaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WikimediaService service;

    private static final WikimediaResponse RESPONSE = getWikimediaResponse();

    @Test
    void testGetWikimediaProjectPageviewsSuccessfully() throws Exception {
        given(service.getWikimediaProjectPageviews()).willReturn(RESPONSE);

        mockMvc.perform(get(WIKIMEDIA).accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk())
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items", hasSize(RESPONSE.getItems().size())))
                .andExpect(jsonPath("$.items[0].project").value(RESPONSE.getItems().get(0).getProject()))
                .andExpect(jsonPath("$.items[0].access").value(RESPONSE.getItems().get(0).getAccess()))
                .andExpect(jsonPath("$.items[0].agent").value(RESPONSE.getItems().get(0).getAgent()))
                .andExpect(jsonPath("$.items[0].granularity").value(RESPONSE.getItems().get(0).getGranularity()))
                .andExpect(jsonPath("$.items[0].timestamp").value(RESPONSE.getItems().get(0).getTimestamp()))
                .andExpect(jsonPath("$.items[0].views").value(RESPONSE.getItems().get(0).getViews()));

        verify(service).getWikimediaProjectPageviews();
    }

    @Test
    void testGetWikimediaProjectPageviewsFailure() throws Exception {
        given(service.getWikimediaProjectPageviews()).willThrow(new CommonException(COMMON_ERROR_404_004));

        mockMvc.perform(get(WIKIMEDIA).accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.code").value("COMMON-ERROR-404-004"))
                .andExpect(jsonPath("$.message").value("Wikimedia pageviews for all projects not found"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        verify(service).getWikimediaProjectPageviews();
    }
}
