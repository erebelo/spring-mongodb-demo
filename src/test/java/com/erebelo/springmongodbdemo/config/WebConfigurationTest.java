package com.erebelo.springmongodbdemo.config;

import com.erebelo.springmongodbdemo.context.interceptor.HeaderInterceptor;
import com.erebelo.springmongodbdemo.context.logging.LoggingFilter;
import com.erebelo.springmongodbdemo.context.resolver.UserIdArgumentResolver;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class WebConfigurationTest {

    @InjectMocks
    private WebConfiguration webConfiguration;

    @Mock
    private HeaderInterceptor headerInterceptor;

    @Mock
    private InterceptorRegistry interceptorRegistry;

    @Test
    void testAddInterceptors() {
        webConfiguration.addInterceptors(interceptorRegistry);

        verify(interceptorRegistry).addInterceptor(headerInterceptor);
    }

    @Test
    void testAddArgumentResolvers() {
        List<HandlerMethodArgumentResolver> resolvers = Mockito.mock(List.class);

        webConfiguration.addArgumentResolvers(resolvers);

        verify(resolvers).add(Mockito.any(UserIdArgumentResolver.class));
    }

    @Test
    void testLoggingFilterRegistration() {
        FilterRegistrationBean<LoggingFilter> result = webConfiguration.loggingFilterRegistration();

        assertEquals(LoggingFilter.class, result.getFilter().getClass(), "Filter should be set correctly");
        assertEquals(1, result.getOrder(), "Order should be set correctly");
        assertEquals(1, result.getUrlPatterns().size(), "URL Patterns size should be correct");
        assertEquals("/*", result.getUrlPatterns().iterator().next(), "URL Pattern should be set correctly");
    }
}
