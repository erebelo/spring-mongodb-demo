package com.erebelo.springmongodbdemo.context;

import com.erebelo.springmongodbdemo.context.logging.MdcLoggingFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import java.io.IOException;
import java.util.UUID;

import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MdcLoggingFilterTest {

    @InjectMocks
    private MdcLoggingFilter mdcLoggingFilter;

    @Mock
    private FilterChain filterChain;

    private final MockedStatic<MDC> mdcMockedStatic = mockStatic(MDC.class);

    private static final String MDC_KEY_REQUEST_ID = "requestId";

    @Test
    void testDoFilterInternal() throws ServletException, IOException {
        var servletRequestMock = new MockHttpServletRequest();
        var servletResponseMock = new MockHttpServletResponse();
        var requestId = UUID.randomUUID().toString();

        doAnswer(invocation -> {
            MDC.put(MDC_KEY_REQUEST_ID, requestId);
            return null;
        }).when(MDC.class);

        MDC.put(MDC_KEY_REQUEST_ID, requestId);

        mdcLoggingFilter.doFilter(servletRequestMock, servletResponseMock, filterChain);

        verify(filterChain).doFilter(servletRequestMock, servletResponseMock);

        mdcMockedStatic.close();
    }
}
