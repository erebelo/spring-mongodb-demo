package com.erebelo.springmongodbdemo.context;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import com.erebelo.springmongodbdemo.context.resolver.UserIdArgumentResolver;
import com.erebelo.springmongodbdemo.util.HttpHeadersUtil;
import java.util.Objects;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.core.MethodParameter;

class UserIdArgumentResolverTest {

    private final UserIdArgumentResolver userIdArgumentResolver = new UserIdArgumentResolver();
    private final MethodParameter methodParameter = mock(MethodParameter.class);
    private final MockedStatic<HttpHeadersUtil> mockedStatic = mockStatic(HttpHeadersUtil.class);

    private static final String STRING_CLASS = "java.lang.String";
    private static final String BOOLEAN_CLASS = "java.lang.Boolean";
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
    void testSupportsParameterThenReturnTrue() throws ClassNotFoundException {
        Class myClass = Class.forName(STRING_CLASS);

        given(methodParameter.getParameterType()).willReturn(myClass);
        given(methodParameter.hasParameterAnnotation(any())).willReturn(true);

        var result = userIdArgumentResolver.supportsParameter(methodParameter);

        assertTrue(result);
    }

    @Test
    void testSupportsParameterThenReturnFalse() throws ClassNotFoundException {
        Class myClass = Class.forName(BOOLEAN_CLASS);

        given(methodParameter.getParameterType()).willReturn(myClass);
        given(methodParameter.hasParameterAnnotation(any())).willReturn(true);

        var result = userIdArgumentResolver.supportsParameter(methodParameter);

        assertFalse(result);
    }

    @Test
    void testSupportsParameterByHasParameterAnnotationThenReturnFalse() throws ClassNotFoundException {
        Class myClass = Class.forName(STRING_CLASS);

        given(methodParameter.getParameterType()).willReturn(myClass);
        given(methodParameter.hasParameterAnnotation(any())).willReturn(false);

        boolean result = userIdArgumentResolver.supportsParameter(methodParameter);

        assertFalse(result);
    }

    @Test
    void testResolveArgumentThenReturnObject() {
        var result = userIdArgumentResolver.resolveArgument(null, null, null, null);

        assertThat(result).isEqualTo(USER_ID);
        assertThat(HttpHeadersUtil.getLoggedInUser()).isEqualTo(USER_ID);
    }
}
