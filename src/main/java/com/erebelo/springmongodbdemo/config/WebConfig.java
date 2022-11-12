package com.erebelo.springmongodbdemo.config;

import com.erebelo.springmongodbdemo.context.interceptor.HeaderInterceptor;
import com.erebelo.springmongodbdemo.context.resolver.UserIdArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Component
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final HeaderInterceptor headerInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(headerInterceptor);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new UserIdArgumentResolver());
    }
}
