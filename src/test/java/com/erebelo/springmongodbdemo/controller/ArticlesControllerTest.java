package com.erebelo.springmongodbdemo.controller;

import com.erebelo.springmongodbdemo.domain.response.ArticlesDataResponseDTO;
import com.erebelo.springmongodbdemo.exception.model.CommonException;
import com.erebelo.springmongodbdemo.service.ArticlesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static com.erebelo.springmongodbdemo.constant.BusinessConstant.ARTICLES;
import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_422_003;
import static com.erebelo.springmongodbdemo.mock.ArticlesMock.getArticlesDataResponseDTO;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ArticlesControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private ArticlesController controller;

    @Mock
    private ArticlesService service;

    private static final List<ArticlesDataResponseDTO> RESPONSE = getArticlesDataResponseDTO();

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testGetArticlesSuccessfully() throws Exception {
        given(service.getArticles()).willReturn(RESPONSE);

        mockMvc.perform(get(ARTICLES)
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(RESPONSE.size())))
                .andExpect(jsonPath("$.[0].title").value(RESPONSE.get(0).getTitle()))
                .andExpect(jsonPath("$.[0].url").value(RESPONSE.get(0).getUrl()))
                .andExpect(jsonPath("$.[0].author").value(RESPONSE.get(0).getAuthor()))
                .andExpect(jsonPath("$.[0].numComments").value(RESPONSE.get(0).getNumComments()))
                .andExpect(jsonPath("$.[0].storyId").doesNotExist())
                .andExpect(jsonPath("$.[0].storyTitle").value(RESPONSE.get(0).getStoryTitle()))
                .andExpect(jsonPath("$.[0].storyUrl").value(RESPONSE.get(0).getStoryUrl()))
                .andExpect(jsonPath("$.[0].parentId").value(RESPONSE.get(0).getParentId()))
                .andExpect(jsonPath("$.[0].createdAt").value(RESPONSE.get(0).getCreatedAt()))
                .andReturn();

        verify(service).getArticles();
    }

    @Test
    void testGetArticlesFailure() {
        var exception = new CommonException(COMMON_ERROR_422_003);
        given(service.getArticles()).willThrow(exception);

        assertThatThrownBy(() -> mockMvc.perform(get(ARTICLES)
                .accept(MediaType.APPLICATION_JSON_VALUE)))
                .hasCause(exception);

        verify(service).getArticles();
    }
}
