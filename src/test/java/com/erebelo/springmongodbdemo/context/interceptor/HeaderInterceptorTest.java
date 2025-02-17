package com.erebelo.springmongodbdemo.context.interceptor;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.method.HandlerMethod;

@ExtendWith(MockitoExtension.class)
class HeaderInterceptorTest {

    @InjectMocks
    private HeaderInterceptor headerInterceptor;

    @Mock
    private HttpServletRequest httpServletRequest;

    @Mock
    private HttpServletResponse httpServletResponse;

    @Mock
    private HandlerMethod handlerMethod;

    @Test
    void testPreHandleWithLoggedInUserAnnotation() {
        given(handlerMethod.getMethodAnnotation(HeaderLoggedInUser.class)).willReturn(createLoggedInUserAnnotation());
        given(httpServletRequest.getHeader(anyString())).willReturn("someValue");

        boolean response = headerInterceptor.preHandle(httpServletRequest, this.httpServletResponse, handlerMethod);

        assertTrue(response);
    }

    @Test
    void testPreHandleWithoutLoggedInUserAnnotation() throws NoSuchMethodException {
        given(handlerMethod.getMethodAnnotation(HeaderLoggedInUser.class)).willReturn(null);
        given(handlerMethod.getMethod()).willReturn(TestController.class.getDeclaredMethod("testMethod"));

        boolean response = headerInterceptor.preHandle(httpServletRequest, httpServletResponse, handlerMethod);

        assertTrue(response);
    }

    @Test
    void testPreHandleMissingHeader() throws Exception {
        given(handlerMethod.getMethodAnnotation(HeaderLoggedInUser.class)).willReturn(createLoggedInUserAnnotation());
        given(httpServletRequest.getHeader(anyString())).willReturn(null);
        given(httpServletResponse.getWriter()).willReturn(Mockito.mock(PrintWriter.class));

        boolean response = headerInterceptor.preHandle(httpServletRequest, httpServletResponse, handlerMethod);

        assertFalse(response);
    }

    @Test
    void testPreHandleMissingHeaderThrowsException() throws IOException {
        given(handlerMethod.getMethodAnnotation(HeaderLoggedInUser.class)).willReturn(createLoggedInUserAnnotation());
        given(httpServletRequest.getHeader(anyString())).willReturn(null);
        given(httpServletResponse.getWriter()).willThrow(new IOException("Error serializing object"));

        assertThrows(IllegalStateException.class,
                () -> headerInterceptor.preHandle(httpServletRequest, httpServletResponse, handlerMethod));
    }

    private HeaderLoggedInUser createLoggedInUserAnnotation() {
        return Mockito.mock(HeaderLoggedInUser.class);
    }

    static class TestController {
        public void testMethod() {
            // Method is empty for testing
        }
    }
}
