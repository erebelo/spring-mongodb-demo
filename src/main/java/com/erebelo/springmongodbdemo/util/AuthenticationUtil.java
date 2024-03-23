package com.erebelo.springmongodbdemo.util;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.erebelo.springmongodbdemo.constants.ProfileConstants.LOGGED_IN_USER_ID_HEADER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthenticationUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationUtil.class);

    public static String getLoggedInUser() {
        return getHeaderByName(LOGGED_IN_USER_ID_HEADER);
    }

    private static String getHeaderByName(String headerName) {
        return getHttpServletRequest().getHeader(headerName);
    }

    public static HttpHeaders getBasicHttpHeaders() {
        var httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        return httpHeaders;
    }

    public static HttpHeaders getHttpHeaders() {
        var requestHttpHeaders = getHttpServletRequest();

        var httpHeaders = Collections.list(requestHttpHeaders.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(
                        Function.identity(), h -> Collections.list(requestHttpHeaders.getHeaders(h)),
                        (oldValue, newValue) -> newValue,
                        HttpHeaders::new));

        httpHeaders.set(HttpHeaders.ACCEPT_ENCODING, "*");
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAcceptCharset(List.of(StandardCharsets.UTF_8));

        return httpHeaders;
    }

    private static HttpServletRequest getHttpServletRequest() {
        var requestAttributes = RequestContextHolder.getRequestAttributes();

        if (requestAttributes == null) {
            LOGGER.error("Error getting request attributes by RequestContextHolder");
            throw new IllegalStateException("No current request attributes found in RequestContextHolder");
        }

        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }
}
