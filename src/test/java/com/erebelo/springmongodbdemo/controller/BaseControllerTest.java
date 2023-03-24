package com.erebelo.springmongodbdemo.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
abstract class BaseControllerTest {

    protected MockMvc mockMvc;

    @SuppressWarnings("all")
    protected JacksonTester<Object> jacksonTester;

    @BeforeEach
    void setUp() {
        ObjectMapper objMapper = new ObjectMapper();
        objMapper.registerModule(new JavaTimeModule());
        JacksonTester.initFields(this, objMapper);

        mockMvc = MockMvcBuilders.standaloneSetup(getControllerClass())
                .setMessageConverters(new MappingJackson2HttpMessageConverter(objMapper))
                .build();
    }

    protected abstract Object getControllerClass();

}
