package com.erebelo.springmongodbdemo.domain.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AddressRequest {

    private String recordId;

    @NotBlank(message = "addressLine1 is mandatory")
    private String addressLine1;

    @NotBlank(message = "addressLine2 is mandatory")
    private String addressLine2;

    @NotBlank(message = "city is mandatory")
    private String city;

    @NotBlank(message = "state is mandatory")
    private String state;

    @NotBlank(message = "country is mandatory")
    private String country;

    @NotBlank(message = "zipCode is mandatory")
    private String zipCode;

}
