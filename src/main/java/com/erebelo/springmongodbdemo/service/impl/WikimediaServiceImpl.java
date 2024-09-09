package com.erebelo.springmongodbdemo.service.impl;

import com.erebelo.springmongodbdemo.domain.response.WikimediaResponse;
import com.erebelo.springmongodbdemo.exception.model.ClientException;
import com.erebelo.springmongodbdemo.exception.model.CommonException;
import com.erebelo.springmongodbdemo.rest.HttpClient;
import com.erebelo.springmongodbdemo.service.WikimediaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Objects;

import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_404_004;
import static com.erebelo.springmongodbdemo.util.AuthenticationUtil.getHttpHeaders;

@Log4j2
@Service
@RequiredArgsConstructor
public class WikimediaServiceImpl implements WikimediaService {

    private final HttpClient httpClient;

    @Value("${wikimedia.public.api.url}")
    private String wikimediaPublicApiUrl;

    private static final String WIKIMEDIA_CLIENT_ERROR_MESSAGE = "Error getting Wikimedia project pageviews";

    @Override
    public WikimediaResponse getWikimediaProjectPageviews() {
        log.info("Getting daily aggregated Wikimedia pageviews for all projects");
        WikimediaResponse wikimediaPageViews;

        try {
            ResponseEntity<WikimediaResponse> response = httpClient.getRestTemplate().exchange(wikimediaPublicApiUrl, HttpMethod.GET,
                    new HttpEntity<>(getHttpHeaders()), new ParameterizedTypeReference<>() {
                    });
            wikimediaPageViews = response.hasBody() ? response.getBody() : null;
        } catch (HttpClientErrorException e) {
            throw new ClientException(HttpStatus.valueOf(e.getStatusCode().value()), WIKIMEDIA_CLIENT_ERROR_MESSAGE, e);
        }

        if (Objects.isNull(wikimediaPageViews) || Objects.isNull(wikimediaPageViews.getItems()) || wikimediaPageViews.getItems().isEmpty()) {
            throw new CommonException(COMMON_ERROR_404_004);
        }

        log.info("{} wikimedia items found", wikimediaPageViews.getItems().size());
        return wikimediaPageViews;
    }
}
