package com.erebelo.springmongodbdemo.validation;

import static com.erebelo.springmongodbdemo.mock.ProfileMock.getProfileRequest;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

import com.erebelo.springmongodbdemo.domain.enumeration.MaritalStatusEnum;
import com.erebelo.springmongodbdemo.domain.request.ProfileContactDTO;
import com.erebelo.springmongodbdemo.domain.request.ProfileRequest;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProfileConstraintValidatorTest {

    @InjectMocks
    private ProfileConstraintValidator validator;

    @Mock
    private ProfileValidator profileValidator;

    @Mock
    private ConstraintValidatorContext context;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder builderContext;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder.NodeBuilderCustomizableContext nodeBuilderContext;

    @Test
    void testInitialize() {
        assertDoesNotThrow(() -> validator.initialize(profileValidator));
    }

    @Test
    void testValidProfileRequest() {
        boolean isValid = validator.isValid(getProfileRequest(), context);

        assertTrue(isValid);
    }

    @Test
    void testInvalidDateOfBirth() {
        given(context.buildConstraintViolationWithTemplate(anyString())).willReturn(builderContext);
        given(builderContext.addPropertyNode(anyString())).willReturn(nodeBuilderContext);

        ProfileRequest profileRequest = getProfileRequest();
        profileRequest.setDateOfBirth(LocalDate.now());
        profileRequest.getSpouseProfile().setDateOfBirth(LocalDate.now());

        boolean isValid = validator.isValid(profileRequest, context);

        assertFalse(isValid);
    }

    @Test
    void testInvalidContactNumbers() {
        given(context.buildConstraintViolationWithTemplate(anyString())).willReturn(builderContext);
        given(builderContext.addPropertyNode(anyString())).willReturn(nodeBuilderContext);

        List<ProfileContactDTO> contactNumbers = new ArrayList<>();
        contactNumbers.add(null);
        ProfileRequest profileRequest = getProfileRequest();
        profileRequest.setContactNumbers(contactNumbers);

        boolean isValid = validator.isValid(profileRequest, context);

        assertFalse(isValid);
    }

    @Test
    void testInvalidMaritalStatus() {
        given(context.buildConstraintViolationWithTemplate(anyString())).willReturn(builderContext);
        given(builderContext.addPropertyNode(anyString())).willReturn(nodeBuilderContext);

        ProfileRequest profileRequest = getProfileRequest();
        profileRequest.setMaritalStatus(MaritalStatusEnum.SINGLE);

        boolean isValid = validator.isValid(profileRequest, context);

        assertFalse(isValid);
    }
}
