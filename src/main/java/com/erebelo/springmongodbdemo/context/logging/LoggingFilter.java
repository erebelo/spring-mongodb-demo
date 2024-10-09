package com.erebelo.springmongodbdemo.context.logging;

import static com.erebelo.springmongodbdemo.constant.BusinessConstant.REQUEST_ID_HEADER;
import static com.erebelo.springmongodbdemo.constant.BusinessConstant.REQUEST_ID_HEADER_PREFIX;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.split;
import static org.apache.commons.lang3.StringUtils.trimToEmpty;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.lang.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;

public class LoggingFilter extends OncePerRequestFilter {

    private static final List<String> REQUEST_HEADER_LIST;

    static {
        REQUEST_HEADER_LIST = Collections.singletonList(REQUEST_ID_HEADER);
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        try {
            Map<String, String> httpHeaders = sanitizeHeader(request);

            if (!httpHeaders.containsKey(REQUEST_ID_HEADER)) {
                httpHeaders.put(REQUEST_ID_HEADER, REQUEST_ID_HEADER_PREFIX + UUID.randomUUID());
            }

            httpHeaders.forEach(ThreadContext::put);

            filterChain.doFilter(request, response);
        } finally {
            ThreadContext.clearMap();
        }
    }

    private Map<String, String> sanitizeHeader(HttpServletRequest request) {
        return REQUEST_HEADER_LIST.stream()
                .filter(e -> isNotBlank(request.getHeader(e)) && request.getHeader(e).charAt(0) != ',')
                .collect(Collectors.toMap(e -> e, e -> trimToEmpty(split(request.getHeader(e), ',')[0])));
    }
}
