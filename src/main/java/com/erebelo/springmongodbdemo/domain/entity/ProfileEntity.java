package com.erebelo.springmongodbdemo.domain.entity;

import com.erebelo.springmongodbdemo.context.history.DocumentHistory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
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
