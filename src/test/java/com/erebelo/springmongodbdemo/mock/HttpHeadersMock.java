package com.erebelo.springmongodbdemo.mock;

import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;

@UtilityClass
public class HttpHeadersMock {

    public static HttpHeaders getDownstreamApiHttpHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.ACCEPT_ENCODING, "*");
        httpHeaders.setAcceptCharset(List.of(StandardCharsets.UTF_8));

        return httpHeaders;
    }
}
