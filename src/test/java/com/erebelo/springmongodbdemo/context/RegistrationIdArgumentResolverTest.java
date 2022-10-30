package com.erebelo.springmongodbdemo.context;

import com.erebelo.springmongodbdemo.utils.RegistrationUtils;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.core.MethodParameter;

import static org.assertj.core.api.BDDAssertions.then;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RegistrationIdArgumentResolverTest {

    private UserIdArgumentResolver registrationIdArgumentResolver = new UserIdArgumentResolver();
    private MethodParameter methodParameter = mock(MethodParameter.class);

    private static final String stringClass = "java.lang.String";
    private static final String booleanClass = "java.lang.Boolean";
    private static final String registrationId = "12345";

    @Test
    void whenTestSupportsParameterThenReturnTrue() throws ClassNotFoundException {
        Class myClass = Class.forName(stringClass);

        when(methodParameter.getParameterType()).thenReturn(myClass);
        when(methodParameter.hasParameterAnnotation(any())).thenReturn(true);

        Boolean result = registrationIdArgumentResolver.supportsParameter(methodParameter);

        then(result).isTrue();
    }

    @Test
    void whenTestSupportsParameterByParameterTypeThenReturnFalse() throws ClassNotFoundException {
        Class myClass = Class.forName(booleanClass);

        when(methodParameter.getParameterType()).thenReturn(myClass);
        when(methodParameter.hasParameterAnnotation(any())).thenReturn(true);

        Boolean result = registrationIdArgumentResolver.supportsParameter(methodParameter);

        then(result).isFalse();
    }

    @Test
    void whenTestSupportsParameterByHasParameterAnnotationThenReturnFalse() throws ClassNotFoundException {
        Class myClass = Class.forName(stringClass);

        when(methodParameter.getParameterType()).thenReturn(myClass);
        when(methodParameter.hasParameterAnnotation(any())).thenReturn(false);

        Boolean result = registrationIdArgumentResolver.supportsParameter(methodParameter);

        then(result).isFalse();
    }

    @Test
    void whenTestResolveArgumentThenReturnObject() {
        try (MockedStatic<RegistrationUtils> registrationUtils = Mockito.mockStatic(RegistrationUtils.class)) {
            registrationUtils.when(() -> RegistrationUtils.getRegistrationId()).thenReturn(registrationId);
            assertEquals(registrationId, RegistrationUtils.getRegistrationId());

            Object result = registrationIdArgumentResolver.resolveArgument(null, null, null, null);

            then(result).isEqualTo(registrationId);
        }
    }
}
