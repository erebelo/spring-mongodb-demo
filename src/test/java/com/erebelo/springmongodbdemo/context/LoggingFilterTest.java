package com.erebelo.springmongodbdemo.context;

import com.erebelo.springmongodbdemo.context.logging.LoggingFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.apache.logging.log4j.ThreadContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.UUID;

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LoggingFilterTest {

    @InjectMocks
    private LoggingFilter loggingFilter;

    @Mock
    private FilterChain filterChain;

    private static final String REQUEST_ID_HEADER = "RequestID";
    private static final String REQUEST_ID_HEADER_PREFIX = "GEN-";

    @Test
    void testDoFilterInternalWithRequestIdHeader() throws ServletException, IOException {
        var servletRequestMock = new MockHttpServletRequest();
        var servletResponseMock = new MockHttpServletResponse();
        var requestId = UUID.randomUUID().toString();

        servletRequestMock.addHeader(REQUEST_ID_HEADER, requestId);

        try (MockedStatic<ThreadContext> threadContextMockedStatic = mockStatic(ThreadContext.class)) {
            threadContextMockedStatic.when(() -> ThreadContext.put(REQUEST_ID_HEADER, requestId)).thenAnswer(invocation -> null);

            loggingFilter.doFilter(servletRequestMock, servletResponseMock, filterChain);

            threadContextMockedStatic.verify(() -> ThreadContext.put(REQUEST_ID_HEADER, requestId));
            verify(filterChain).doFilter(servletRequestMock, servletResponseMock);
            threadContextMockedStatic.verify(ThreadContext::clearMap);
        }
    }

    @Test
    void testDoFilterInternalWithoutRequestIdHeader() throws ServletException, IOException {
        var servletRequestMock = new MockHttpServletRequest();
        var servletResponseMock = new MockHttpServletResponse();
        var requestId = UUID.randomUUID();

        try (MockedStatic<UUID> uuidMockedStatic = mockStatic(UUID.class);
             MockedStatic<ThreadContext> threadContextMockedStatic = mockStatic(ThreadContext.class)) {

            uuidMockedStatic.when(UUID::randomUUID).thenReturn(requestId);
            threadContextMockedStatic.when(() -> ThreadContext.put(REQUEST_ID_HEADER, REQUEST_ID_HEADER_PREFIX + requestId)).thenAnswer(invocation -> null);

            loggingFilter.doFilter(servletRequestMock, servletResponseMock, filterChain);

            threadContextMockedStatic.verify(() -> ThreadContext.put(REQUEST_ID_HEADER, REQUEST_ID_HEADER_PREFIX + requestId));
            verify(filterChain).doFilter(servletRequestMock, servletResponseMock);
            threadContextMockedStatic.verify(ThreadContext::clearMap);
        }
    }
}
