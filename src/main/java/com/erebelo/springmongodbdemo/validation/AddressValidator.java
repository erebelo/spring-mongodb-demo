package com.erebelo.springmongodbdemo.validation;

import com.erebelo.springmongodbdemo.domain.request.AddressRequest;
import java.util.Optional;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AddressValidator {

    public static Optional<String> getRequestValidationErrors(AddressRequest addressRequest) {
        StringBuilder errors = new StringBuilder();

        String addressLine1 = addressRequest.getAddressLine1();
        if (addressLine1 == null || addressLine1.isBlank()) {
            errors.append("addressLine1 must not be blank, ");
        }

        String addressLine2 = addressRequest.getAddressLine2();
        if (addressLine2 == null || addressLine2.isBlank()) {
            errors.append("addressLine2 must not be blank, ");
        }

        String city = addressRequest.getCity();
        if (city == null || city.isBlank()) {
            errors.append("city must not be blank, ");
        }

        String state = addressRequest.getState();
        if (state == null || state.isBlank()) {
            errors.append("state must not be blank, ");
        } else if (state.length() != 2) {
            errors.append("invalid state, ");
        }

        String country = addressRequest.getCountry();
        if (country == null || country.isBlank()) {
            errors.append("country must not be blank, ");
        }

        String zipCode = addressRequest.getZipCode();
        if (zipCode == null || zipCode.isBlank()) {
            errors.append("zipCode must not be blank, ");
        } else if (zipCode.length() < 5) {
            errors.append("invalid zipCode, ");
        }

        if (!errors.isEmpty()) {
            errors.setLength(errors.length() - 2); // Remove trailing comma
            return Optional.of("[" + errors + "]");
        }

        return Optional.empty();
    }
}
