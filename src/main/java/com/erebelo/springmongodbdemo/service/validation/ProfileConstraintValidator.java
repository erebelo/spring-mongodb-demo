package com.erebelo.springmongodbdemo.service.validation;

import com.erebelo.springmongodbdemo.domain.enumeration.MaritalStatusEnum;
import com.erebelo.springmongodbdemo.domain.request.ProfileRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
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
        LOGGER.info("Validating the profile request: {}", request);
        List<FieldMessage> errorMessages = new ArrayList<>();

        validateSpouseProfile(request, errorMessages);
        validateDateOfBirth(request, errorMessages);

        for (FieldMessage e : errorMessages) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(e.getMessage()).addPropertyNode(e.getFieldName()).addConstraintViolation();
        }

        return errorMessages.isEmpty();
    }

    public static void validateSpouseProfile(ProfileRequest request, List<FieldMessage> errorMessages) {
        LOGGER.info("Validating spouseProfile object");
        var maritalStatus = request.getMaritalStatus();

        if (Objects.nonNull(maritalStatus) && Objects.nonNull(request.getSpouseProfile()) &&
                (maritalStatus.equals(MaritalStatusEnum.SINGLE) || maritalStatus.equals(MaritalStatusEnum.DIVORCED) || maritalStatus.equals(MaritalStatusEnum.WIDOWED))) {
            errorMessages.add(new FieldMessage("spouseProfile",
                    "spouseProfile should not be filled in when marital status equals SINGLE, DIVORCE OR WIDOWED"));
        }
    }

    public static void validateDateOfBirth(ProfileRequest request, List<FieldMessage> errorMessages) {
        LOGGER.info("Validating dateOfBirth fields");
        var dob = request.getDateOfBirth();

        if (Objects.nonNull(dob) && dob.compareTo(LocalDate.now()) >= 0) {
            errorMessages.add(new FieldMessage("dateOfBirth", "dateOfBirth must be less than current date"));
        }

        dob = request.getSpouseProfile() != null ? request.getSpouseProfile().getDateOfBirth() : null;
        if (Objects.nonNull(dob) && dob.compareTo(LocalDate.now()) >= 0) {
            errorMessages.add(new FieldMessage("spouseProfile.dateOfBirth", "spouseProfile.dateOfBirth must be less than current date"));
        }
    }
}
