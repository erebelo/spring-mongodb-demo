package com.erebelo.springmongodbdemo.config;

import com.erebelo.springmongodbdemo.context.resolver.UserIdArgumentResolver;
import com.erebelo.springmongodbdemo.context.resolver.RegistrationNameArgumentResolver;
import org.springframework.stereotype.Component;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Component
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new UserIdArgumentResolver());
        resolvers.add(new RegistrationNameArgumentResolver());
    }
}
