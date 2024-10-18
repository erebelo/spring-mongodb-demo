package com.erebelo.springmongodbdemo.mock;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletRequestAttributes;

@UtilityClass
public class HttpHeadersMock {

    public static ServletRequestAttributes getServletRequestAttributes() {
        return new ServletRequestAttributes(buildHttpServletRequest());
    }

    private static HttpServletRequest buildHttpServletRequest() {
        var request = new MockHttpServletRequest();
        request.addHeader(HttpHeaders.ACCEPT, "*/*");

        return request;
    }

    public static HttpHeaders getHttpHeaders() {
        var httpHeaders = new HttpHeaders();
        httpHeaders.set(HttpHeaders.ACCEPT, "*/*");
        httpHeaders.set(HttpHeaders.ACCEPT_ENCODING, "*");
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAcceptCharset(List.of(StandardCharsets.UTF_8));

        return httpHeaders;
    }

    public static HttpHeaders getBasicHttpHeaders() {
        var httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        return httpHeaders;
    }
}
