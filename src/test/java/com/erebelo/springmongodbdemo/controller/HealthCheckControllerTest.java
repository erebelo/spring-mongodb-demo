package com.erebelo.springmongodbdemo.controller;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static com.erebelo.springmongodbdemo.constants.BusinessConstants.HEALTH_CHECK;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class HealthCheckControllerTest extends BaseControllerTest {

    @InjectMocks
    private HealthCheckController controller;

    @Override
    protected Object getControllerClass() {
        return controller;
    }

    @Test
    void testGetHealthCheck() throws Exception {
        mockMvc.perform(get(HEALTH_CHECK))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Spring MongoDB Demo application is up and running")));
    }
}
