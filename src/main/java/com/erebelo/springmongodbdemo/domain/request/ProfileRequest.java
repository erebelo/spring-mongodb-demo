package com.erebelo.springmongodbdemo.domain.request;

import com.erebelo.springmongodbdemo.domain.enumeration.EmploymentStatusEnum;
import com.erebelo.springmongodbdemo.domain.enumeration.GenderEnum;
import com.erebelo.springmongodbdemo.domain.enumeration.HealthLevelEnum;
import com.erebelo.springmongodbdemo.domain.enumeration.MaritalStatusEnum;
import com.erebelo.springmongodbdemo.service.validation.ProfileValidator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

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

    private HealthLevelEnum healthLevel;

    @Valid
    @NotEmpty(message = "contactNumbers is mandatory")
    private List<ProfileContactDTO> contactNumbers;

    @Valid
    @NotNull(message = "currentLocation is mandatory")
    private ProfileLocationDTO currentLocation;

    @Valid
    private SpouseProfileDTO spouseProfile;

    @ToString.Include(name = "dateOfBirth", rank = 1)
    public String maskDateOfBirth() {
        if (Objects.nonNull(dateOfBirth)) {
            return "****-**-" + dateOfBirth.getDayOfMonth();
        }
        return null;
    }
}
