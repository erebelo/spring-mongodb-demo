package com.erebelo.springmongodbdemo.controller;

import static com.erebelo.springmongodbdemo.mock.AddressMock.ADDRESSES_BULK_PATH;
import static com.erebelo.springmongodbdemo.mock.AddressMock.getAddressRequestList;
import static com.erebelo.springmongodbdemo.mock.AddressMock.getBulkAddressResponse;
import static com.erebelo.springmongodbdemo.mock.AddressMock.getBulkAddressResponseResultMatcher;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.erebelo.springmongodbdemo.domain.request.AddressRequest;
import com.erebelo.springmongodbdemo.service.AddressService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = AddressController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
class AddressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AddressService addressService;

    @Captor
    private ArgumentCaptor<List<AddressRequest>> addressRequestArgumentCaptor;

    @Test
    void testBulkInsertAddressesSuccessful() throws Exception {
        given(addressService.bulkInsertAddresses(anyList())).willReturn(getBulkAddressResponse());

        mockMvc.perform(post(ADDRESSES_BULK_PATH).contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(getAddressRequestList()))
                .accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isOk())
                .andExpectAll(getBulkAddressResponseResultMatcher());

        verify(addressService).bulkInsertAddresses(addressRequestArgumentCaptor.capture());

        assertThat(addressRequestArgumentCaptor.getValue()).usingRecursiveComparison()
                .isEqualTo(getAddressRequestList());
    }

    @Test
    void testBulkInsertAddressesFailure() throws Exception {
        given(addressService.bulkInsertAddresses(anyList()))
                .willThrow(new RuntimeException("An unexpected error occurred"));

        mockMvc.perform(post(ADDRESSES_BULK_PATH).contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(getAddressRequestList()))
                .accept(MediaType.APPLICATION_JSON_VALUE)).andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.status").value("INTERNAL_SERVER_ERROR"))
                .andExpect(jsonPath("$.code").doesNotExist())
                .andExpect(jsonPath("$.message").value("An unexpected error occurred"))
                .andExpect(jsonPath("$.timestamp").isNotEmpty());

        verify(addressService).bulkInsertAddresses(addressRequestArgumentCaptor.capture());

        assertThat(addressRequestArgumentCaptor.getValue()).usingRecursiveComparison()
                .isEqualTo(getAddressRequestList());
    }
}
