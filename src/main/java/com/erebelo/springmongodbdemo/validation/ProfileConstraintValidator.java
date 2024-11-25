package com.erebelo.springmongodbdemo.validation;

import com.erebelo.springmongodbdemo.domain.enumeration.MaritalStatusEnum;
import com.erebelo.springmongodbdemo.domain.request.ProfileRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class ProfileConstraintValidator implements ConstraintValidator<ProfileValidator, ProfileRequest> {

    @Override
    public void initialize(ProfileValidator constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(ProfileRequest request, ConstraintValidatorContext context) {
        log.info("Validating the profile request object");
        List<FieldMessage> errorMessages = new ArrayList<>();

        validateDateOfBirth(request, errorMessages);
        validateContactNumbers(request, errorMessages);
        validateSpouseProfile(request, errorMessages);

        for (FieldMessage e : errorMessages) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName())
                    .addConstraintViolation();
        }

        return errorMessages.isEmpty();
    }

    public static void validateDateOfBirth(ProfileRequest request, List<FieldMessage> errorMessages) {
        log.info("Validating dateOfBirth field");
        var dob = request.getDateOfBirth();

        if (Objects.nonNull(dob) && !dob.isBefore(LocalDate.now())) {
            errorMessages.add(new FieldMessage("dateOfBirth", "dateOfBirth must be less than current date"));
        }

        dob = request.getSpouseProfile() != null ? request.getSpouseProfile().getDateOfBirth() : null;
        if (Objects.nonNull(dob) && !dob.isBefore(LocalDate.now())) {
            errorMessages.add(new FieldMessage("spouseProfile.dateOfBirth",
                    "spouseProfile.dateOfBirth must be less than current date"));
        }
    }

    public static void validateContactNumbers(ProfileRequest request, List<FieldMessage> errorMessages) {
        log.info("Validating contactNumbers object");
        var contactNumbers = request.getContactNumbers();

        if (Objects.nonNull(contactNumbers) && contactNumbers.stream().anyMatch(Objects::isNull)) {
            errorMessages.add(new FieldMessage("contactNumbers", "contactNumbers list cannot contain null elements"));
        }
    }

    public static void validateSpouseProfile(ProfileRequest request, List<FieldMessage> errorMessages) {
        log.info("Validating spouseProfile object");
        var maritalStatus = request.getMaritalStatus();

        if (Objects.nonNull(maritalStatus) && Objects.nonNull(request.getSpouseProfile())
                && (maritalStatus.equals(MaritalStatusEnum.SINGLE) || maritalStatus.equals(MaritalStatusEnum.DIVORCED)
                        || maritalStatus.equals(MaritalStatusEnum.WIDOWED))) {
            errorMessages.add(new FieldMessage("spouseProfile",
                    "spouseProfile should not be filled in when marital status equals SINGLE, DIVORCED, or WIDOWED"));
        }
    }
}
