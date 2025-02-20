package com.erebelo.springmongodbdemo.domain.entity;

import com.erebelo.springmongodbdemo.context.history.DocumentHistory;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "addresses")
@DocumentHistory(collection = "addresses_history")
public class AddressEntity {

    @Id
    private String id;

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

    private String createdBy;
    private String modifiedBy;
    private LocalDateTime createdDateTime;
    private LocalDateTime modifiedDateTime;
    private Long version;

}
