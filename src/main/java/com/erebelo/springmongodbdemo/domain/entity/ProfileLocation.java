package com.erebelo.springmongodbdemo.domain.entity;

import jakarta.validation.constraints.NotBlank;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileLocation implements Serializable {

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
