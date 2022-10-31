package com.erebelo.springmongodbdemo.domain.response;

import com.erebelo.springmongodbdemo.domain.enumeration.EmploymentStatusEnum;
import com.erebelo.springmongodbdemo.domain.enumeration.GenderEnum;
import com.erebelo.springmongodbdemo.domain.enumeration.MaritalStatusEnum;
import com.erebelo.springmongodbdemo.domain.request.ProfileContactDTO;
import com.erebelo.springmongodbdemo.domain.request.ProfileLocationDTO;
import com.erebelo.springmongodbdemo.domain.request.SpouseProfileDTO;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(name = "ProfileResponse")
public class ProfileResponse {

    private String firstName;
    private String lastName;
    private LocalDate dateOfBirth;
    private Integer numberOfDependents;
    private BigDecimal estimatedAnnualIncome;
    private BigDecimal estimatedNetWorth;
    private GenderEnum gender;
    private MaritalStatusEnum maritalStatus;
    private EmploymentStatusEnum employmentStatus;
    private List<ProfileContactDTO> contactNumbers;
    private ProfileLocationDTO currentLocation;
    private SpouseProfileDTO spouseProfile;

}
