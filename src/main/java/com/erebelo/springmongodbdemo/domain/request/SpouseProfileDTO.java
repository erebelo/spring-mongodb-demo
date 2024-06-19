package com.erebelo.springmongodbdemo.domain.request;

import com.erebelo.springmongodbdemo.domain.enumeration.EmploymentStatusEnum;
import com.erebelo.springmongodbdemo.domain.enumeration.GenderEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Objects;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class SpouseProfileDTO {

    @NotBlank(message = "spouseProfile.firstName is mandatory")
    private String firstName;

    @NotBlank(message = "spouseProfile.lastName is mandatory")
    private String lastName;

    @NotNull(message = "spouseProfile.dateOfBirth is mandatory")
    private LocalDate dateOfBirth;

    private GenderEnum gender;
    private EmploymentStatusEnum employmentStatus;

    @ToString.Include(name = "dateOfBirth", rank = 1)
    public String maskDateOfBirth() {
        if (Objects.nonNull(dateOfBirth)) {
            return "****-**-" + dateOfBirth.getDayOfMonth();
        }
        return null;
    }
}
