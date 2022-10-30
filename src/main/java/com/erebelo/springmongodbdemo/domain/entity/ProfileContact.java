package com.erebelo.springmongodbdemo.domain.entity;

import com.erebelo.springmongodbdemo.domain.enumeration.ContactTypeEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileContact implements Serializable {

    @NotNull(message = "contactType is mandatory")
    private ContactTypeEnum contactType;

    @NotBlank(message = "contactValue is mandatory")
    private String contactValue;

}
