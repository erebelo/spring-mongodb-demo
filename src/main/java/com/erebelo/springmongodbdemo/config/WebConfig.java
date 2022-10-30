package com.erebelo.springmongodbdemo.config;

import com.erebelo.springmongodbdemo.context.RegistrationIdArgumentResolver;
import com.erebelo.springmongodbdemo.context.RegistrationNameArgumentResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Component
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new RegistrationIdArgumentResolver());
        resolvers.add(new RegistrationNameArgumentResolver());
    }
}
