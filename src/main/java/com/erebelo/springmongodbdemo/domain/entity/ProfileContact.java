package com.erebelo.springmongodbdemo.domain.entity;

import com.erebelo.springmongodbdemo.domain.enumeration.ContactTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileContact implements Serializable {

    @NotNull(message = "contactNumbers.contactType is mandatory")
    private ContactTypeEnum contactType;

    @NotBlank(message = "contactNumbers.contactValue is mandatory")
    private String contactValue;

}
