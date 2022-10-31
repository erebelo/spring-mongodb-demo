package com.erebelo.springmongodbdemo.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.erebelo.springmongodbdemo.constants.ProfileConstants.LOGGED_IN_USER_ID_HEADER;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AuthenticationUtils {

    public static String getLoggedInUser() {
        return getHeaderByName(LOGGED_IN_USER_ID_HEADER);
    }

    private static String getHeaderByName(String headerName) {
        String headerValue = getHttpServletRequest().getHeader(headerName);
        if (headerValue == null) {
            throw new IllegalStateException(String.format("Missing %s attribute in HttpHeaders", headerName));
        }
        return headerValue;
    }

    private static HttpHeaders getHttpHeaders() {
        HttpServletRequest request = getHttpServletRequest();
        return Collections.list(request.getHeaderNames())
                .stream()
                .collect(Collectors.toMap(
                        Function.identity(), h -> Collections.list(request.getHeaders(h)),
                        (oldValue, newValue) -> newValue,
                        HttpHeaders::new));
    }

    private static HttpServletRequest getHttpServletRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes == null) {
            throw new IllegalStateException("No current request attributes found in RequestContextHolder");
        }
        return ((ServletRequestAttributes) requestAttributes).getRequest();
    }
}
