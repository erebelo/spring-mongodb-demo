package com.erebelo.springmongodbdemo.util;

import static com.erebelo.springmongodbdemo.constant.ProfileConstant.LOGGED_IN_USER_ID_HEADER;

import jakarta.servlet.http.HttpServletRequest;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Log4j2
@UtilityClass
public class AuthenticationUtil {

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

        var httpHeaders = Collections.list(requestHttpHeaders.getHeaderNames()).stream()
                .collect(Collectors.toMap(Function.identity(), h -> Collections.list(requestHttpHeaders.getHeaders(h)),
                        (oldValue, newValue) -> newValue, HttpHeaders::new));

        httpHeaders.set(HttpHeaders.ACCEPT_ENCODING, "*");
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        httpHeaders.setAcceptCharset(List.of(StandardCharsets.UTF_8));

        return httpHeaders;
    }

    private static HttpServletRequest getHttpServletRequest() {
        var requestAttributes = RequestContextHolder.getRequestAttributes();

        if (requestAttributes == null) {
            log.error("Error getting request attributes by RequestContextHolder");
            throw new IllegalStateException("No current request attributes found in RequestContextHolder");
        }

        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }
}
