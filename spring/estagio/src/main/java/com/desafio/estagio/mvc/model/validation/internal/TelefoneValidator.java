package com.desafio.estagio.mvc.model.validation.internal;

import com.desafio.estagio.mvc.model.validation.annotation.ValidTelefone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TelefoneValidator implements ConstraintValidator<ValidTelefone, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }

        String digitsOnly = value.replaceAll("\\D", "");

        String phoneRegex = "^[1-9]{2}(?:[2-8]|9)[0-9]{7,8}$";

        return digitsOnly.matches(phoneRegex);
    }
}