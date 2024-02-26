package com.erebelo.springmongodbdemo.mock;

import com.erebelo.springmongodbdemo.domain.response.WikimediaItemResponse;
import com.erebelo.springmongodbdemo.domain.response.WikimediaResponse;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WikimediaMock {

    public static final String WIKIMEDIA_URL = "https://wikimedia.org";
    private static final String PROJECT = "all-projects";
    private static final String ACCESS = "all-access";
    private static final String AGENT = "all-agents";
    private static final String GRANULARITY = "daily";
    private static final String TIMESTAMP = "2015100100";
    private static final Long VIEWS = 614236484L;

    public static WikimediaResponse getWikimediaResponse() {
        return WikimediaResponse.builder()
                .items(Collections.singletonList(WikimediaItemResponse.builder()
                        .project(PROJECT)
                        .access(ACCESS)
                        .agent(AGENT)
                        .granularity(GRANULARITY)
                        .timestamp(TIMESTAMP)
                        .views(VIEWS)
                        .build()))
                .build();
    }
}
