package com.erebelo.springmongodbdemo.context.resolver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import com.erebelo.springmongodbdemo.util.HttpHeadersUtil;
import java.util.Objects;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;

class UserIdArgumentResolverTest {

    private final UserIdArgumentResolver userIdArgumentResolver = new UserIdArgumentResolver();
    private final MethodParameter methodParameter = mock(MethodParameter.class);
    private final MockedStatic<HttpHeadersUtil> mockedStatic = mockStatic(HttpHeadersUtil.class);

    private static final String USER_ID = "12345";

    @BeforeEach
    void init() {
        mockedStatic.when(HttpHeadersUtil::getLoggedInUser).thenReturn(USER_ID);
    }

    @AfterEach
    void clear() {
        if (Objects.nonNull(mockedStatic)) {
            mockedStatic.close();
        }
    }

    @Test
    void testSupportsParameterThenReturnTrue() {
        given(methodParameter.getParameterType()).willAnswer(invocation -> String.class);
        given(methodParameter.hasParameterAnnotation(any())).willReturn(true);

        var result = userIdArgumentResolver.supportsParameter(methodParameter);

        assertTrue(result);
    }

    @Test
    void testSupportsParameterThenReturnFalse() {
        given(methodParameter.getParameterType()).willAnswer(invocation -> Boolean.class);
        given(methodParameter.hasParameterAnnotation(any())).willReturn(true);

        var result = userIdArgumentResolver.supportsParameter(methodParameter);

        assertFalse(result);
    }

    @Test
    void testSupportsParameterByHasParameterAnnotationThenReturnFalse() {
        given(methodParameter.getParameterType()).willAnswer(invocation -> String.class);
        given(methodParameter.hasParameterAnnotation(any())).willReturn(false);

        boolean result = userIdArgumentResolver.supportsParameter(methodParameter);

        assertFalse(result);
    }

    @Test
    void testResolveArgumentThenReturnObject() {
        var result = userIdArgumentResolver.resolveArgument(mock(MethodParameter.class),
                mock(ModelAndViewContainer.class), mock(NativeWebRequest.class), mock(WebDataBinderFactory.class));

        assertEquals(USER_ID, result);
        assertEquals(USER_ID, HttpHeadersUtil.getLoggedInUser());
    }
}
