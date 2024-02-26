package com.erebelo.springmongodbdemo.mock;

import com.erebelo.springmongodbdemo.domain.response.ArticlesDataResponse;
import com.erebelo.springmongodbdemo.domain.response.ArticlesDataResponseDTO;
import com.erebelo.springmongodbdemo.domain.response.ArticlesResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ArticlesMock {

    private static final String TITLE = "A Message to Our Customers";
    private static final String URL = "http://www.apple.com/customer-letter";
    private static final String AUTHOR = "epaga";
    private static final Integer NUM_COMMENTS = 967;
    private static final Integer STORY_ID = null;
    private static final String STORY_TITLE = "Guacamole – A clientless remote desktop gateway";
    private static final String STORY_URL = "https://guacamole.incubator.apache.org";
    private static final Integer PARENT_ID = 6547669;
    private static final Integer CREATED_AT = 1455698317;
    private static final Integer PAGE = 1;
    private static final Integer PER_PAGE = 10;
    private static final Integer TOTAL = 41;
    private static final Integer TOTAL_PAGES = 5;

    public static List<ArticlesDataResponseDTO> getArticlesDataResponseDTO() {
        return Collections.singletonList(ArticlesDataResponseDTO.builder()
                .title(TITLE)
                .url(URL)
                .author(AUTHOR)
                .numComments(NUM_COMMENTS)
                .storyId(STORY_ID)
                .storyTitle(STORY_TITLE)
                .storyUrl(STORY_URL)
                .parentId(PARENT_ID)
                .createdAt(CREATED_AT)
                .build()
        );
    }

    public static ArticlesResponse getArticlesResponse() {
        return ArticlesResponse.builder()
                .page(PAGE)
                .perPage(PER_PAGE)
                .total(TOTAL)
                .totalPages(TOTAL_PAGES)
                .data(Collections.singletonList(ArticlesDataResponse.builder()
                        .title(TITLE)
                        .url(URL)
                        .author(AUTHOR)
                        .numComments(NUM_COMMENTS)
                        .storyId(STORY_ID)
                        .storyTitle(STORY_TITLE)
                        .storyUrl(STORY_URL)
                        .parentId(PARENT_ID)
                        .createdAt(CREATED_AT)
                        .build()))
                .build();
    }
}
