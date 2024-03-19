package com.erebelo.springmongodbdemo.config;

import com.erebelo.springmongodbdemo.context.logging.MdcLoggingFilter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.servlet.FilterRegistrationBean;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MdcLoggingConfigurationTest {

    @Test
    void testMdcFilterRegistration() {
        var mdcLoggingConfiguration = new MdcLoggingConfiguration();

        FilterRegistrationBean<MdcLoggingFilter> registrationBean = mdcLoggingConfiguration.mdcFilter();

        assertEquals(MdcLoggingFilter.class, registrationBean.getFilter().getClass());
        assertEquals("/*", registrationBean.getUrlPatterns().iterator().next());
    }
}
