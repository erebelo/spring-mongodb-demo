package com.erebelo.springmongodbdemo.mock;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

import com.erebelo.springmongodbdemo.domain.request.AddressRequest;
import com.erebelo.springmongodbdemo.domain.response.AddressResponse;
import com.erebelo.springmongodbdemo.domain.response.BulkAddressResponse;
import java.util.Collections;
import java.util.List;
import lombok.experimental.UtilityClass;
import org.springframework.test.web.servlet.ResultMatcher;

@UtilityClass
public class AddressMock {

    public static final String ADDRESSES_BULK_PATH = "/addresses/bulk";
    public static final String ADDRESSES_BULK_OPS_ENGINE_PATH = "/addresses/bulk-ops-engine";
    private static final String ID = "67bfd1e9bba32978e8c5d35f";
    private static final String RECORD_ID_ONE = "1";
    private static final String RECORD_ID_TWO = "2";
    private static final String ADDRESS_LINE_1_ONE = "85157 Moore Plain";
    private static final String ADDRESS_LINE_1_TWO = "654 Pine Rd";
    private static final String ADDRESS_LINE_2_ONE = "Suite 577";
    private static final String ADDRESS_LINE_2_TWO = "Building B";
    private static final String CITY = "Miami";
    private static final String STATE = "FL";
    private static final String COUNTRY = "USA";
    private static final String ZIP_CODE_ONE = "33101";
    private static final String ZIP_CODE_TWO = "45121";
    private static final String ERROR_MESSAGE = "E11000 Duplicated key error";

    private AddressResponse getAddressResponseOne() {
        return AddressResponse.builder().recordId(RECORD_ID_ONE).addressLine1(ADDRESS_LINE_1_ONE)
                .addressLine2(ADDRESS_LINE_2_ONE).city(CITY).state(STATE).country(COUNTRY).zipCode(ZIP_CODE_ONE)
                .build();
    }

    private AddressResponse getAddressResponseTwo() {
        return AddressResponse.builder().recordId(RECORD_ID_TWO).addressLine1(ADDRESS_LINE_1_TWO)
                .addressLine2(ADDRESS_LINE_2_TWO).city(CITY).state(STATE).country(COUNTRY).zipCode(ZIP_CODE_TWO)
                .build();
    }

    public static BulkAddressResponse getBulkAddressResponse() {
        AddressResponse success = getAddressResponseOne();
        success.setId(ID);

        AddressResponse failed = getAddressResponseTwo();
        failed.setErrorMessage(ERROR_MESSAGE);

        return BulkAddressResponse.builder().success(Collections.singletonList(success))
                .failed(Collections.singletonList(failed)).build();
    }

    private static AddressRequest getAddressRequestOne() {
        return AddressRequest.builder().recordId(RECORD_ID_ONE).addressLine1(ADDRESS_LINE_1_ONE)
                .addressLine2(ADDRESS_LINE_2_ONE).city(CITY).state(STATE).country(COUNTRY).zipCode(ZIP_CODE_ONE)
                .build();
    }

    private static AddressRequest getAddressRequestTwo() {
        return AddressRequest.builder().recordId(RECORD_ID_TWO).addressLine1(ADDRESS_LINE_1_TWO)
                .addressLine2(ADDRESS_LINE_2_TWO).city(CITY).state(STATE).country(COUNTRY).zipCode(ZIP_CODE_TWO)
                .build();
    }

    public static List<AddressRequest> getAddressRequestList() {
        return List.of(getAddressRequestOne(), getAddressRequestTwo());
    }

    public static ResultMatcher[] getBulkAddressResponseResultMatcher() {
        AddressResponse success = getBulkAddressResponse().getSuccess().get(0);
        AddressResponse failed = getBulkAddressResponse().getFailed().get(0);

        return new ResultMatcher[]{jsonPath("$.success").isArray(), jsonPath("$.success", hasSize(1)),
                jsonPath("$.success[0].id").value(success.getId()),
                jsonPath("$.success[0].recordId").value(success.getRecordId()),
                jsonPath("$.success[0].addressLine1").value(success.getAddressLine1()),
                jsonPath("$.success[0].addressLine2").value(success.getAddressLine2()),
                jsonPath("$.success[0].city").value(success.getCity()),
                jsonPath("$.success[0].state").value(success.getState()),
                jsonPath("$.success[0].country").value(success.getCountry()),
                jsonPath("$.success[0].zipCode").value(success.getZipCode()),
                jsonPath("$.success[0].errorMessage").doesNotExist(), jsonPath("$.failed").isArray(),
                jsonPath("$.failed", hasSize(1)), jsonPath("$.failed[0].id").doesNotExist(),
                jsonPath("$.failed[0].recordId").value(failed.getRecordId()),
                jsonPath("$.failed[0].addressLine1").value(failed.getAddressLine1()),
                jsonPath("$.failed[0].addressLine2").value(failed.getAddressLine2()),
                jsonPath("$.failed[0].city").value(failed.getCity()),
                jsonPath("$.failed[0].state").value(failed.getState()),
                jsonPath("$.failed[0].country").value(failed.getCountry()),
                jsonPath("$.failed[0].zipCode").value(failed.getZipCode()),
                jsonPath("$.failed[0].errorMessage").value(failed.getErrorMessage())};
    }
}
