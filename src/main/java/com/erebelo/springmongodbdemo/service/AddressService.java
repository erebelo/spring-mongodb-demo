package com.erebelo.springmongodbdemo.service;

import com.erebelo.springmongodbdemo.domain.request.AddressRequest;
import com.erebelo.springmongodbdemo.domain.response.BulkAddressResponse;
import java.util.List;

public interface AddressService {

    BulkAddressResponse bulkInsertAddresses(List<AddressRequest> addressRequestList);

}
