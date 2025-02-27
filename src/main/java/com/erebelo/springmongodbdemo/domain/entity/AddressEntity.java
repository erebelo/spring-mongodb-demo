package com.erebelo.springmongodbdemo.domain.entity;

import com.erebelo.springmongodbdemo.context.history.DocumentHistory;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
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

    @Transient
    private String recordId;

    @Transient
    private String errorMessage;

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String zipCode;

    /*
     * The createdBy, modifiedBy, createdDateTime, modifiedDateTime, and version
     * fields are declared here directly instead of using the BaseEntity class
     * because BulkOperations does not handle these fields effectively.
     */
    private String createdBy;
    private String modifiedBy;
    private LocalDateTime createdDateTime;
    private LocalDateTime modifiedDateTime;
    private Long version;

}
