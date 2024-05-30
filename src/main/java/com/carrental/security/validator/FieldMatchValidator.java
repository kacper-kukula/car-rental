package com.carrental.security.validator;

import com.carrental.dto.user.UserRegistrationRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class FieldMatchValidator
        implements ConstraintValidator<FieldMatch, UserRegistrationRequestDto> {

    private String first;
    private String second;

    @Override
    public void initialize(final FieldMatch constraintAnnotation) {
        first = constraintAnnotation.first();
        second = constraintAnnotation.second();
    }

    @Override
    public boolean isValid(final UserRegistrationRequestDto dto,
                           final ConstraintValidatorContext context) {
        return dto.password().equals(dto.repeatPassword());
    }
}
