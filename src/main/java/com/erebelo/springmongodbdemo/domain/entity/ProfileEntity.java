package com.erebelo.springmongodbdemo.domain.entity;

import com.erebelo.springmongodbdemo.context.history.DocumentHistory;
import com.erebelo.springmongodbdemo.domain.enumeration.EmploymentStatusEnum;
import com.erebelo.springmongodbdemo.domain.enumeration.GenderEnum;
import com.erebelo.springmongodbdemo.domain.enumeration.MaritalStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "profile")
@DocumentHistory(collection = "profile-history")
public class ProfileEntity extends BaseEntity implements Serializable {

    @Id
    private String id;

    @NotBlank(message = "fistName is mandatory")
    private String fistName;

    @NotBlank(message = "lastName is mandatory")
    private String lastName;

    @NotNull(message = "dateOfBirth is mandatory")
    private LocalDate dateOfBirth;

    @Min(value = 1, message = "numberOfDependents must be greater than or equal to 0")
    @Max(value = 20, message = "numberOfDependents must be less than or equal to 20")
    private Integer numberOfDependents;

    @DecimalMin(value = "0", message = "estimatedAnnualIncome must be greater than or equal to 0")
    @DecimalMax(value = "9999999.99", message = "estimatedAnnualIncome must be less than or equal to 9,999,999.99")
    private BigDecimal estimatedAnnualIncome;

    @DecimalMin(value = "-9999999.99", message = "estimatedNetWorth must be greater than or equal to -9,999,999.99")
    @DecimalMax(value = "9999999.99", message = "estimatedNetWorth must be less than or equal to 9,999,999.99")
    private BigDecimal estimatedNetWorth;

    private GenderEnum gender;
    private MaritalStatusEnum maritalStatus;
    private EmploymentStatusEnum employmentStatus;

    @Valid
    @NotNull
    private List<ProfileContact> contactNumbers;

    @Valid
    @NotNull
    private ProfileLocation currentLocation;

    @Valid
    private SpouseProfile spouseProfile;

}
