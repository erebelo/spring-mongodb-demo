package com.erebelo.springmongodbdemo.mock;

import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@UtilityClass
public class HttpHeadersMock {

    public static HttpHeaders getArticlesHttpHeaders() {
        var httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        return httpHeaders;
    }

    public static HttpHeaders getWikimediaHttpHeaders() {
        var httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.ACCEPT_ENCODING, "*");
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAcceptCharset(List.of(StandardCharsets.UTF_8));

        return httpHeaders;
    }
}
