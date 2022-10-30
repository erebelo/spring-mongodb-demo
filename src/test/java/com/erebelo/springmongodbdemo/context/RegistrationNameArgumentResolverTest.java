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

class RegistrationNameArgumentResolverTest {

    private RegistrationNameArgumentResolver registrationNameArgumentResolver = new RegistrationNameArgumentResolver();
    private MethodParameter methodParameter = mock(MethodParameter.class);

    private static final String stringClass = "java.lang.String";
    private static final String booleanClass = "java.lang.Boolean";
    private static final String registrationName = "ED123ANQ45";

    @Test
    void whenTestSupportsParameterThenReturnTrue() throws ClassNotFoundException {
        Class myClass = Class.forName(stringClass);

        when(methodParameter.getParameterType()).thenReturn(myClass);
        when(methodParameter.hasParameterAnnotation(any())).thenReturn(true);

        Boolean result = registrationNameArgumentResolver.supportsParameter(methodParameter);

        then(result).isTrue();
    }

    @Test
    void whenTestSupportsParameterByParameterTypeThenReturnFalse() throws ClassNotFoundException {
        Class myClass = Class.forName(booleanClass);

        when(methodParameter.getParameterType()).thenReturn(myClass);
        when(methodParameter.hasParameterAnnotation(any())).thenReturn(true);

        Boolean result = registrationNameArgumentResolver.supportsParameter(methodParameter);

        then(result).isFalse();
    }

    @Test
    void whenTestSupportsParameterByHasParameterAnnotationThenReturnFalse() throws ClassNotFoundException {
        Class myClass = Class.forName(stringClass);

        when(methodParameter.getParameterType()).thenReturn(myClass);
        when(methodParameter.hasParameterAnnotation(any())).thenReturn(false);

        Boolean result = registrationNameArgumentResolver.supportsParameter(methodParameter);

        then(result).isFalse();
    }

    @Test
    void whenTestResolveArgumentThenReturnObject() {
        try (MockedStatic<RegistrationUtils> registrationUtils = Mockito.mockStatic(RegistrationUtils.class)) {
            registrationUtils.when(() -> RegistrationUtils.getRegistrationName()).thenReturn(registrationName);
            assertEquals(registrationName, RegistrationUtils.getRegistrationName());

            Object result = registrationNameArgumentResolver.resolveArgument(null, null, null, null);

            then(result).isEqualTo(registrationName);
        }
    }
}
