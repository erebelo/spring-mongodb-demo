package com.erebelo.springmongodbdemo.service;

import static com.erebelo.springmongodbdemo.mock.AddressMock.getAddressEntityList;
import static com.erebelo.springmongodbdemo.mock.AddressMock.getAddressRequestList;
import static com.erebelo.springmongodbdemo.mock.AddressMock.getAddressRequestListWithInvalidObjects;
import static com.erebelo.springmongodbdemo.mock.AddressMock.getBulkAddressResponse;
import static com.erebelo.springmongodbdemo.mock.AddressMock.getBulkAddressResponseWithInvalidObjects;
import static com.erebelo.springmongodbdemo.mock.AddressMock.getBulkOpsEngineResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.erebelo.springmongodbdemo.domain.entity.AddressEntity;
import com.erebelo.springmongodbdemo.domain.request.AddressRequest;
import com.erebelo.springmongodbdemo.domain.response.BulkAddressResponse;
import com.erebelo.springmongodbdemo.mapper.AddressMapper;
import com.erebelo.springmongodbdemo.service.impl.AddressServiceImpl;
import com.erebelo.springmongodbdemo.service.impl.BulkOpsEngine;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class AddressServiceTest {

    @InjectMocks
    private AddressServiceImpl service;

    @Mock
    private BulkOpsEngine bulkOpsEngine;

    @Spy
    private final AddressMapper mapper = Mappers.getMapper(AddressMapper.class);

    @Captor
    private ArgumentCaptor<List<AddressEntity>> entityArgumentCaptor;

    @BeforeEach
    void init() {
        ReflectionTestUtils.setField(service, "applicationName", "spring-mongodb-demo");
        ReflectionTestUtils.setField(service, "bulkOpsEngine", bulkOpsEngine);
    }

    @Test
    void testBulkInsertAddressesByBulkOpsEngineSuccessful() {
        given(bulkOpsEngine.bulkInsert(anyList(), any(), any(), any(), any())).willReturn(getBulkOpsEngineResponse());

        BulkAddressResponse response = service.bulkInsertAddresses(getAddressRequestList());

        assertThat(response).usingRecursiveComparison().isEqualTo(getBulkAddressResponse());

        verify(mapper, times(2)).requestToEntity(any(AddressRequest.class), anyString(), any(LocalDateTime.class));
        verify(mapper, times(2)).entityListToResponseList(anyList());

        verify(bulkOpsEngine).bulkInsert(entityArgumentCaptor.capture(), any(), any(), any(), any());
        assertThat(entityArgumentCaptor.getValue()).usingRecursiveComparison().ignoringCollectionOrder()
                .ignoringFields("createdBy", "modifiedBy", "createdDateTime", "modifiedDateTime", "version")
                .isEqualTo(getAddressEntityList());
    }

    @Test
    void testBulkInsertAddressesByBulkOpsEngineWithInvalidObjectsSuccessful() {
        given(bulkOpsEngine.bulkInsert(anyList(), any(), any(), any(), any())).willReturn(getBulkOpsEngineResponse());

        BulkAddressResponse response = service.bulkInsertAddresses(getAddressRequestListWithInvalidObjects());

        assertThat(response).usingRecursiveComparison().ignoringCollectionOrder()
                .isEqualTo(getBulkAddressResponseWithInvalidObjects());

        verify(mapper, times(4)).requestToEntity(any(AddressRequest.class), anyString(), any(LocalDateTime.class));
        verify(mapper, times(2)).entityListToResponseList(anyList());

        verify(bulkOpsEngine).bulkInsert(entityArgumentCaptor.capture(), any(), any(), any(), any());
        assertThat(entityArgumentCaptor.getValue()).usingRecursiveComparison().ignoringCollectionOrder()
                .ignoringFields("createdBy", "modifiedBy", "createdDateTime", "modifiedDateTime", "version")
                .isEqualTo(getAddressEntityList());
    }
}
