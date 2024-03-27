package com.erebelo.springmongodbdemo.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static com.erebelo.springmongodbdemo.constant.BusinessConstant.HEALTH_CHECK;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class HealthCheckControllerTest {

    private MockMvc mockMvc;

    @InjectMocks
    private HealthCheckController controller;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testGetHealthCheck() throws Exception {
        mockMvc.perform(get(HEALTH_CHECK))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(content().string(containsString("Spring MongoDB Demo application is up and running")));
    }
}
