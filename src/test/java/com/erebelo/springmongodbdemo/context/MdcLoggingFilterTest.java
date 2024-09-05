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

import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class MdcLoggingFilterTest {

    @InjectMocks
    private MdcLoggingFilter mdcLoggingFilter;

    @Mock
    private FilterChain filterChain;

    private final MockedStatic<MDC> mdcMockedStatic = mockStatic(MDC.class);

    private static final String REQUEST_ID_HEADER = "requestId";

    @Test
    void testDoFilterInternalWithRequestIdHeader() throws ServletException, IOException {
        var servletRequestMock = new MockHttpServletRequest();
        var servletResponseMock = new MockHttpServletResponse();
        var requestId = UUID.randomUUID().toString();
        servletRequestMock.addHeader(REQUEST_ID_HEADER, requestId);

        mdcMockedStatic.when(() -> MDC.put(REQUEST_ID_HEADER.replaceAll("-", ""), requestId)).thenAnswer(invocation -> null);

        mdcLoggingFilter.doFilter(servletRequestMock, servletResponseMock, filterChain);

        mdcMockedStatic.verify(() -> MDC.put(REQUEST_ID_HEADER.replaceAll("-", ""), requestId));
        verify(filterChain).doFilter(servletRequestMock, servletResponseMock);
        mdcMockedStatic.verify(MDC::clear);

        mdcMockedStatic.close();
    }

    @Test
    void testDoFilterInternalWithoutRequestIdHeader() throws ServletException, IOException {
        var servletRequestMock = new MockHttpServletRequest();
        var servletResponseMock = new MockHttpServletResponse();
        var requestId = UUID.randomUUID();

        mdcMockedStatic.when(() -> MDC.put(REQUEST_ID_HEADER.replaceAll("-", ""), requestId.toString())).thenAnswer(invocation -> null);

        try (MockedStatic<UUID> uuidMockedStatic = mockStatic(UUID.class)) {
            uuidMockedStatic.when(UUID::randomUUID).thenReturn(requestId);

            mdcLoggingFilter.doFilter(servletRequestMock, servletResponseMock, filterChain);

            mdcMockedStatic.verify(() -> MDC.put(REQUEST_ID_HEADER.replaceAll("-", ""), requestId.toString()));
            verify(filterChain).doFilter(servletRequestMock, servletResponseMock);
            mdcMockedStatic.verify(MDC::clear);

            mdcMockedStatic.close();
        }
    }
}
