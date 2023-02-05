package com.erebelo.springmongodbdemo.domain.request;

import com.erebelo.springmongodbdemo.domain.enumeration.EmploymentStatusEnum;
import com.erebelo.springmongodbdemo.domain.enumeration.GenderEnum;
import com.erebelo.springmongodbdemo.domain.enumeration.MaritalStatusEnum;
import com.erebelo.springmongodbdemo.service.validation.ProfileValidator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@ProfileValidator
@Schema(name = "ProfileRequest")
public class ProfileRequest {

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

    @Valid
    @NotEmpty(message = "contactNumbers is mandatory")
    private List<ProfileContactDTO> contactNumbers;

    @Valid
    @NotNull(message = "currentLocation is mandatory")
    private ProfileLocationDTO currentLocation;

    @Valid
    private SpouseProfileDTO spouseProfile;

}