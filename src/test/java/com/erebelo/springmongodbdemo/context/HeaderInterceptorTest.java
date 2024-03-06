package com.erebelo.springmongodbdemo.context;

import com.erebelo.springmongodbdemo.context.interceptor.HeaderInterceptor;
import com.erebelo.springmongodbdemo.context.interceptor.HeaderLoggedInUser;
import com.erebelo.springmongodbdemo.exception.ExceptionResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.method.HandlerMethod;

import java.io.IOException;
import java.io.PrintWriter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class HeaderInterceptorTest {

    @InjectMocks
    private HeaderInterceptor headerInterceptor;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private HandlerMethod handlerMethod;

    @Test
    void testPreHandleWithLoggedInUserAnnotation() {
        given(handlerMethod.getMethodAnnotation(HeaderLoggedInUser.class)).willReturn(createLoggedInUserAnnotation());
        given(request.getHeader(anyString())).willReturn("someValue");

        boolean result = headerInterceptor.preHandle(request, response, handlerMethod);

        assertTrue(result);
    }

    @Test
    void testPreHandleWithoutLoggedInUserAnnotation() throws NoSuchMethodException {
        given(handlerMethod.getMethodAnnotation(HeaderLoggedInUser.class)).willReturn(null);
        given(handlerMethod.getMethod()).willReturn(TestController.class.getDeclaredMethod("testMethod"));

        boolean result = headerInterceptor.preHandle(request, response, handlerMethod);

        assertTrue(result);
    }

    @Test
    void testPreHandleMissingHeader() throws Exception {
        given(handlerMethod.getMethodAnnotation(HeaderLoggedInUser.class)).willReturn(createLoggedInUserAnnotation());
        given(request.getHeader(anyString())).willReturn(null);
        given(objectMapper.writeValueAsString(any(ExceptionResponse.class))).willReturn("jsonRepresentation");
        given(response.getWriter()).willReturn(Mockito.mock(PrintWriter.class));

        boolean result = headerInterceptor.preHandle(request, response, handlerMethod);

        assertFalse(result);
    }

    @Test
    void testPreHandleMissingHeaderThrowException() throws IOException {
        given(handlerMethod.getMethodAnnotation(HeaderLoggedInUser.class)).willReturn(createLoggedInUserAnnotation());
        given(request.getHeader(anyString())).willReturn(null);
        given(response.getWriter()).willThrow(new IOException("Error serializing object"));

        assertThrows(IllegalStateException.class, () -> headerInterceptor.preHandle(request, response, handlerMethod));
    }

    private HeaderLoggedInUser createLoggedInUserAnnotation() {
        return Mockito.mock(HeaderLoggedInUser.class);
    }

    static class TestController {
        public void testMethod() {
        }
    }
}
