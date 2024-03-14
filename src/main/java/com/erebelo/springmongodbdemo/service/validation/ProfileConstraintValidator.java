package com.erebelo.springmongodbdemo.service.validation;

import com.erebelo.springmongodbdemo.domain.enumeration.MaritalStatusEnum;
import com.erebelo.springmongodbdemo.domain.request.ProfileRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProfileConstraintValidator implements ConstraintValidator<ProfileValidator, ProfileRequest> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProfileConstraintValidator.class);

    @Override
    public void initialize(ProfileValidator constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(ProfileRequest request, ConstraintValidatorContext context) {
        LOGGER.info("Validating the profile request object");
        List<FieldMessage> errorMessages = new ArrayList<>();

        validateDateOfBirth(request, errorMessages);
        validateContactNumbers(request, errorMessages);
        validateSpouseProfile(request, errorMessages);

        for (FieldMessage e : errorMessages) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName()).addConstraintViolation();
        }

        return errorMessages.isEmpty();
    }

    public static void validateDateOfBirth(ProfileRequest request, List<FieldMessage> errorMessages) {
        LOGGER.info("Validating dateOfBirth field");
        var dob = request.getDateOfBirth();

        if (Objects.nonNull(dob) && !dob.isBefore(LocalDate.now())) {
            errorMessages.add(new FieldMessage("dateOfBirth", "dateOfBirth must be less than current date"));
        }

        dob = request.getSpouseProfile() != null ? request.getSpouseProfile().getDateOfBirth() : null;
        if (Objects.nonNull(dob) && !dob.isBefore(LocalDate.now())) {
            errorMessages.add(new FieldMessage("spouseProfile.dateOfBirth", "spouseProfile.dateOfBirth must be less than current date"));
        }
    }

    public static void validateContactNumbers(ProfileRequest request, List<FieldMessage> errorMessages) {
        LOGGER.info("Validating contactNumbers object");
        var contactNumbers = request.getContactNumbers();

        if (Objects.nonNull(contactNumbers) && contactNumbers.stream().anyMatch(Objects::isNull)) {
            errorMessages.add(new FieldMessage("contactNumbers", "contactNumbers list cannot contain null elements"));
        }
    }

    public static void validateSpouseProfile(ProfileRequest request, List<FieldMessage> errorMessages) {
        LOGGER.info("Validating spouseProfile object");
        var maritalStatus = request.getMaritalStatus();

        if (Objects.nonNull(maritalStatus) && Objects.nonNull(request.getSpouseProfile()) &&
                (maritalStatus.equals(MaritalStatusEnum.SINGLE) || maritalStatus.equals(MaritalStatusEnum.DIVORCED) || maritalStatus.equals(MaritalStatusEnum.WIDOWED))) {
            errorMessages.add(new FieldMessage("spouseProfile",
                    "spouseProfile should not be filled in when marital status equals SINGLE, DIVORCED, or WIDOWED"));
        }
    }
}
