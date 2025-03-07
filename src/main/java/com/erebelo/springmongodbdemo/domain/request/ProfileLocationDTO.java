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
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileLocationDTO {

    @NotBlank(message = "currentLocation.address is mandatory")
    private String address;

    @NotBlank(message = "currentLocation.city is mandatory")
    private String city;

    @NotBlank(message = "currentLocation.state is mandatory")
    private String state;

    @NotBlank(message = "currentLocation.country is mandatory")
    private String country;

    private String postalCode;

}
