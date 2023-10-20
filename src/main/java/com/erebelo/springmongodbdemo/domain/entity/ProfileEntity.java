package com.erebelo.springmongodbdemo.domain.entity;

import com.erebelo.springmongodbdemo.context.history.DocumentHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Document(collection = "profile")
@DocumentHistory(collection = "profile-history")
public class ProfileEntity extends BaseEntity implements Serializable {

    @Id
    private String id;

    @NotBlank(message = "hashObject is mandatory")
    private String hashObject;

    @NotBlank(message = "userId is mandatory")
    private String userId;

    @Valid
    @NotNull(message = "profile is mandatory")
    private UserProfile profile;

}
