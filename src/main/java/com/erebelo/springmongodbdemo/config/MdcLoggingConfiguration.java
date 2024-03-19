package com.erebelo.springmongodbdemo.config;

import com.erebelo.springmongodbdemo.context.logging.MdcLoggingFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MdcLoggingConfiguration {

    @Bean
    public FilterRegistrationBean<MdcLoggingFilter> mdcFilter() {
        FilterRegistrationBean<MdcLoggingFilter> registrationBean = new FilterRegistrationBean<>();

        registrationBean.setFilter(new MdcLoggingFilter());
        registrationBean.addUrlPatterns("/*");

        return registrationBean;
    }
}
