package com.erebelo.springmongodbdemo.domain.entity;

import com.erebelo.springmongodbdemo.domain.enumeration.EmploymentStatusEnum;
import com.erebelo.springmongodbdemo.domain.enumeration.GenderEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SpouseProfile implements Serializable {

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
