package com.erebelo.springmongodbdemo.controller;

import static com.erebelo.springmongodbdemo.constant.BusinessConstant.ARTICLES_PATH;
import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_404_005;
import static com.erebelo.springmongodbdemo.mock.ArticleMock.getArticleDataResponseDTO;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.erebelo.springmongodbdemo.domain.response.ArticleDataResponseDTO;
import com.erebelo.springmongodbdemo.exception.model.CommonException;
import com.erebelo.springmongodbdemo.service.ArticleService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ArticleController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class ArticleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ArticleService service;

    private static final List<ArticleDataResponseDTO> RESPONSE = getArticleDataResponseDTO();

    @Test
    void testGetArticlesSuccessfully() throws Exception {
        given(service.getArticles()).willReturn(RESPONSE);

        mockMvc.perform(get(ARTICLES_PATH).accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray()).andExpect(jsonPath("$", hasSize(RESPONSE.size())))
                .andExpect(jsonPath("$.[0].title").value(RESPONSE.get(0).getTitle()))
                .andExpect(jsonPath("$.[0].url").value(RESPONSE.get(0).getUrl()))
                .andExpect(jsonPath("$.[0].author").value(RESPONSE.get(0).getAuthor()))
                .andExpect(jsonPath("$.[0].numComments").value(RESPONSE.get(0).getNumComments()))
                .andExpect(jsonPath("$.[0].storyId").doesNotExist())
                .andExpect(jsonPath("$.[0].storyTitle").value(RESPONSE.get(0).getStoryTitle()))
                .andExpect(jsonPath("$.[0].storyUrl").value(RESPONSE.get(0).getStoryUrl()))
                .andExpect(jsonPath("$.[0].parentId").value(RESPONSE.get(0).getParentId()))
                .andExpect(jsonPath("$.[0].createdAt").value(RESPONSE.get(0).getCreatedAt()));

        verify(service).getArticles();
    }

    @Test
    void testGetArticlesFailure() throws Exception {
        given(service.getArticles()).willThrow(new CommonException(COMMON_ERROR_404_005));

        mockMvc.perform(get(ARTICLES_PATH).accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value("NOT_FOUND"))
                .andExpect(jsonPath("$.code").value("COMMON-ERROR-404-005"))
                .andExpect(jsonPath("$.message").value("Articles not found"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        verify(service).getArticles();
    }
}
