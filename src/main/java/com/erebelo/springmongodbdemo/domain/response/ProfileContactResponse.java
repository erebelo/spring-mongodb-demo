package com.erebelo.springmongodbdemo.domain.response;

import com.erebelo.springmongodbdemo.domain.enumeration.ContactTypeEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(name = "ProfileContactResponse")
public class ProfileContactResponse {

    private ContactTypeEnum contactType;
    private String contactValue;

}
