package com.erebelo.springmongodbdemo.domain.request;

import com.erebelo.springmongodbdemo.domain.enumeration.ContactTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProfileContactDTO {

    @NotNull(message = "contactNumbers.contactType is mandatory")
    private ContactTypeEnum contactType;

    @NotBlank(message = "contactNumbers.contactValue is mandatory")
    private String contactValue;

}
