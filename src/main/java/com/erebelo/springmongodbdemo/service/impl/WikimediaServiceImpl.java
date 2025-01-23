package com.erebelo.springmongodbdemo.service.impl;

import static com.erebelo.springmongodbdemo.exception.model.CommonErrorCodesEnum.COMMON_ERROR_404_004;
import static com.erebelo.springmongodbdemo.util.HttpHeadersUtil.getDownstreamApiHttpHeaders;

import com.erebelo.springmongodbdemo.domain.response.WikimediaResponse;
import com.erebelo.springmongodbdemo.exception.model.ClientException;
import com.erebelo.springmongodbdemo.exception.model.CommonException;
import com.erebelo.springmongodbdemo.service.WikimediaService;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Log4j2
@Service
public class WikimediaServiceImpl implements WikimediaService {

    @Autowired
    @Qualifier("serviceTwoRestTemplate")
    private RestTemplate restTemplate;

    @Value("${wikimedia.public.api.url}")
    private String wikimediaPublicApiUrl;

    private static final String WIKIMEDIA_CLIENT_ERROR_MESSAGE = "Error getting Wikimedia project pageviews. Error "
            + "message: %s";

    @Override
    public WikimediaResponse getWikimediaProjectPageviews() {
        log.info("Fetching daily aggregated Wikimedia pageviews for all projects from downstream API: {}",
                wikimediaPublicApiUrl);
        WikimediaResponse wikimediaPageViews;

        try {
            ResponseEntity<WikimediaResponse> response = restTemplate.exchange(wikimediaPublicApiUrl, HttpMethod.GET,
                    new HttpEntity<>(getDownstreamApiHttpHeaders()), new ParameterizedTypeReference<>() {
                    });
            wikimediaPageViews = response.hasBody() ? response.getBody() : null;
        } catch (RestClientException e) {
            throw new ClientException(HttpStatus.UNPROCESSABLE_ENTITY,
                    String.format(WIKIMEDIA_CLIENT_ERROR_MESSAGE, e.getMessage()), e);
        }

        if (Objects.isNull(wikimediaPageViews) || Objects.isNull(wikimediaPageViews.getItems())
                || wikimediaPageViews.getItems().isEmpty()) {
            throw new CommonException(COMMON_ERROR_404_004);
        }

        log.info("{} wikimedia items found", wikimediaPageViews.getItems().size());
        return wikimediaPageViews;
    }
}
