package com.erebelo.springmongodbdemo.service.impl;

import com.erebelo.springmongodbdemo.domain.response.WikimediaResponse;
import com.erebelo.springmongodbdemo.exception.StandardException;
import com.erebelo.springmongodbdemo.rest.HttpClient;
import com.erebelo.springmongodbdemo.service.WikimediaService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Objects;

import static com.erebelo.springmongodbdemo.exception.CommonErrorCodesEnum.COMMON_ERROR_404_004;
import static com.erebelo.springmongodbdemo.exception.CommonErrorCodesEnum.COMMON_ERROR_422_001;
import static com.erebelo.springmongodbdemo.utils.AuthenticationUtils.getHttpHeaders;

@Service
@RequiredArgsConstructor
public class WikimediaServiceImpl implements WikimediaService {

    private final HttpClient httpClient;

    @Value("${wikimedia.public.api.url}")
    private String wikimediaPublicApiUrl;

    private static final Logger LOGGER = LoggerFactory.getLogger(WikimediaServiceImpl.class);
    private static final String RESPONSE_BODY_LOGGER = "Response body: {}";

    @Override
    public WikimediaResponse getWikimediaProjectPageviews() {
        LOGGER.info("Getting daily aggregated Wikimedia pageviews for all projects");
        WikimediaResponse wikimediaPageViews;

        try {
            ResponseEntity<WikimediaResponse> response = httpClient.invokeService(wikimediaPublicApiUrl, getHttpHeaders(),
                    new ParameterizedTypeReference<>() {
                    }, HttpMethod.GET);
            wikimediaPageViews = response.hasBody() ? response.getBody() : null;
        } catch (Exception e) {
            throw new StandardException(COMMON_ERROR_422_001, e);
        }

        if (Objects.isNull(wikimediaPageViews) || wikimediaPageViews.getItems().isEmpty()) {
            throw new StandardException(COMMON_ERROR_404_004);
        }

        LOGGER.info(RESPONSE_BODY_LOGGER, wikimediaPageViews);
        return wikimediaPageViews;
    }
}
