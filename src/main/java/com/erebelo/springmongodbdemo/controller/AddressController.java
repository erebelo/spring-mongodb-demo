package com.erebelo.springmongodbdemo.controller;

import static com.erebelo.springmongodbdemo.constant.BusinessConstant.ADDRESSES_BULK_PATH;
import static com.erebelo.springmongodbdemo.constant.BusinessConstant.ADDRESSES_PATH;

import com.erebelo.springmongodbdemo.context.interceptor.HeaderLoggedInUser;
import com.erebelo.springmongodbdemo.domain.request.AddressRequest;
import com.erebelo.springmongodbdemo.domain.response.BulkAddressResponse;
import com.erebelo.springmongodbdemo.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Log4j2
@RestController
@RequestMapping(ADDRESSES_PATH)
@HeaderLoggedInUser
@RequiredArgsConstructor
@Tag(name = "Addresses API")
public class AddressController {

    private final AddressService service;

    @Operation(summary = "POST Bulk Addresses")
    @PostMapping(value = ADDRESSES_BULK_PATH, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BulkAddressResponse> bulkInsertAddresses(
            @Valid @RequestBody List<AddressRequest> addressRequestList) {
        log.info("POST {}", ADDRESSES_PATH + ADDRESSES_BULK_PATH);
        return ResponseEntity.ok(service.bulkInsertAddresses(addressRequestList));
    }
}
