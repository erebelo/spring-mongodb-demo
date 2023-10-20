package com.erebelo.springmongodbdemo.domain.entity;

import com.erebelo.springmongodbdemo.domain.enumeration.EmploymentStatusEnum;
import com.erebelo.springmongodbdemo.domain.enumeration.GenderEnum;
import com.erebelo.springmongodbdemo.domain.enumeration.HealthLevelEnum;
import com.erebelo.springmongodbdemo.domain.enumeration.MaritalStatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfile implements Serializable {

    @NotBlank(message = "firstName is mandatory")
    private String firstName;

    @NotBlank(message = "lastName is mandatory")
    private String lastName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "dateOfBirth is mandatory")
    private LocalDate dateOfBirth;

    @Min(value = 0, message = "numberOfDependents must be greater than or equal to 0")
    @Max(value = 20, message = "numberOfDependents must be less than or equal to 20")
    private Integer numberOfDependents;

    @DecimalMin(value = "0", message = "estimatedAnnualIncome must be greater than or equal to 0")
    @DecimalMax(value = "9999999.99", message = "estimatedAnnualIncome must be less than or equal to 9,999,999.99")
    private BigDecimal estimatedAnnualIncome;

    @DecimalMin(value = "-9999999.99", message = "estimatedNetWorth must be greater than or equal to -9,999,999.99")
    @DecimalMax(value = "9999999.99", message = "estimatedNetWorth must be less than or equal to 9,999,999.99")
    private BigDecimal estimatedNetWorth;

    private GenderEnum gender;

    @NotNull(message = "maritalStatus is mandatory")
    private MaritalStatusEnum maritalStatus;

    private EmploymentStatusEnum employmentStatus;

    private HealthLevelEnum healthLevel;

    @Valid
    @NotEmpty(message = "contactNumbers is mandatory")
    private List<ProfileContact> contactNumbers;

    @Valid
    @NotNull(message = "currentLocation is mandatory")
    private ProfileLocation currentLocation;

    @Valid
    private SpouseProfile spouseProfile;

}
