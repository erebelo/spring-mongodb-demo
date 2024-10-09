package com.erebelo.springmongodbdemo.controller;

import static com.erebelo.springmongodbdemo.constant.BusinessConstant.WIKIMEDIA;
import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_404_004;
import static com.erebelo.springmongodbdemo.mock.WikimediaMock.getWikimediaResponse;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.erebelo.springmongodbdemo.domain.response.WikimediaResponse;
import com.erebelo.springmongodbdemo.exception.GlobalExceptionHandler;
import com.erebelo.springmongodbdemo.exception.model.CommonException;
import com.erebelo.springmongodbdemo.service.WikimediaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class WikimediaControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private WikimediaController controller;

    @Mock
    private WikimediaService service;

    @Mock
    private Environment env;

    private static final WikimediaResponse RESPONSE = getWikimediaResponse();

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).setControllerAdvice(new GlobalExceptionHandler(env))
                .build();
    }

    @Test
    void testGetWikimediaProjectPageviewsSuccessfully() throws Exception {
        given(service.getWikimediaProjectPageviews()).willReturn(RESPONSE);

        mockMvc.perform(get(WIKIMEDIA).accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk()) // TODO use
                                                                                                            // ResultMatcher
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items", hasSize(RESPONSE.getItems().size())))
                .andExpect(jsonPath("$.items[0].project").value(RESPONSE.getItems().get(0).getProject()))
                .andExpect(jsonPath("$.items[0].access").value(RESPONSE.getItems().get(0).getAccess()))
                .andExpect(jsonPath("$.items[0].agent").value(RESPONSE.getItems().get(0).getAgent()))
                .andExpect(jsonPath("$.items[0].granularity").value(RESPONSE.getItems().get(0).getGranularity()))
                .andExpect(jsonPath("$.items[0].timestamp").value(RESPONSE.getItems().get(0).getTimestamp()))
                .andExpect(jsonPath("$.items[0].views").value(RESPONSE.getItems().get(0).getViews())).andReturn();

        verify(service).getWikimediaProjectPageviews();
    }

    @Test
    void testGetWikimediaProjectPageviewsFailure() { // TODO fix it
        var exception = new CommonException(COMMON_ERROR_404_004);
        given(service.getWikimediaProjectPageviews()).willThrow(exception);

        assertThatThrownBy(() -> mockMvc.perform(get(WIKIMEDIA).accept(MediaType.APPLICATION_JSON_VALUE)))
                .hasCause(exception);

        verify(service).getWikimediaProjectPageviews();
    }
}
