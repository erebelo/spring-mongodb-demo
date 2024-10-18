package com.erebelo.springmongodbdemo.mock;

import com.erebelo.springmongodbdemo.domain.response.ArticlesDataResponse;
import com.erebelo.springmongodbdemo.domain.response.ArticlesDataResponseDTO;
import com.erebelo.springmongodbdemo.domain.response.ArticlesResponse;
import java.util.Collections;
import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ArticlesMock {

    public static final String ARTICLES_URL = "https://jsonmock.hackerrank.com/api/articles";
    private static final String TITLE = "A Message to Our Customers";
    private static final String NEW_TITLE = "Chrome";
    private static final String URL = "http://www.apple.com/customer-letter";
    private static final String NEW_URL = "http://www.google.com/customer-letter";
    private static final String AUTHOR = "epaga";
    private static final Integer NUM_COMMENTS = 967;
    private static final Integer STORY_ID = null;
    private static final String STORY_TITLE = "Guacamole – A clientless remote desktop gateway";
    private static final String STORY_URL = "https://guacamole.incubator.apache.org";
    private static final Integer PARENT_ID = 6547669;
    private static final Integer CREATED_AT = 1455698317;
    private static final Integer PAGE = 1;
    private static final Integer NEXT_PAGE = 2;
    private static final Integer PER_PAGE = 1;
    private static final Integer TOTAL = 2;
    public static final Integer TOTAL_PAGES = 2;

    public static List<ArticlesDataResponseDTO> getArticlesDataResponseDTO() {
        return Collections.singletonList(ArticlesDataResponseDTO.builder().title(TITLE).url(URL).author(AUTHOR)
                .numComments(NUM_COMMENTS).storyId(STORY_ID).storyTitle(STORY_TITLE).storyUrl(STORY_URL)
                .parentId(PARENT_ID).createdAt(CREATED_AT).build());
    }

    public static List<ArticlesDataResponseDTO> getArticlesDataResponseDTONextPage() {
        var responseDTO = getArticlesDataResponseDTO();
        return Collections.singletonList(ArticlesDataResponseDTO.builder().title(NEW_TITLE).url(NEW_URL)
                .author(responseDTO.get(0).getAuthor()).numComments(responseDTO.get(0).getNumComments())
                .storyId(responseDTO.get(0).getStoryId()).storyTitle(responseDTO.get(0).getStoryTitle())
                .storyUrl(responseDTO.get(0).getStoryUrl()).parentId(responseDTO.get(0).getParentId())
                .createdAt(responseDTO.get(0).getCreatedAt()).build());
    }

    public static ArticlesResponse getArticlesResponse() {
        return ArticlesResponse.builder().page(PAGE).perPage(PER_PAGE).total(TOTAL).totalPages(TOTAL_PAGES)
                .data(Collections.singletonList(ArticlesDataResponse.builder().title(TITLE).url(URL).author(AUTHOR)
                        .numComments(NUM_COMMENTS).storyId(STORY_ID).storyTitle(STORY_TITLE).storyUrl(STORY_URL)
                        .parentId(PARENT_ID).createdAt(CREATED_AT).build()))
                .build();
    }

    public static ArticlesResponse getArticlesResponseNextPage() {
        var response = getArticlesResponse();
        return ArticlesResponse.builder().page(NEXT_PAGE).perPage(response.getPerPage()).total(response.getTotal())
                .totalPages(response.getTotalPages())
                .data(Collections.singletonList(ArticlesDataResponse.builder().title(NEW_TITLE).url(NEW_URL)
                        .author(response.getData().get(0).getAuthor())
                        .numComments(response.getData().get(0).getNumComments())
                        .storyId(response.getData().get(0).getStoryId())
                        .storyTitle(response.getData().get(0).getStoryTitle())
                        .storyUrl(response.getData().get(0).getStoryUrl())
                        .parentId(response.getData().get(0).getParentId())
                        .createdAt(response.getData().get(0).getCreatedAt()).build()))
                .build();
    }
}
