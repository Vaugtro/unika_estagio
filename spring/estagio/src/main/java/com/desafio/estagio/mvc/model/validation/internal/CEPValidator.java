package com.desafio.estagio.mvc.model.validation.internal;

import com.desafio.estagio.mvc.model.validation.annotation.ValidCEP;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class CEPValidator implements ConstraintValidator<ValidCEP, String> {

    @Override
    public boolean isValid(String cep, ConstraintValidatorContext context) {
        if (cep == null || cep.isEmpty()) return true;

        // Pattern: 8 digits optionally separated by a dash (e.g., 12345-678 or 12345678)
        String cepPattern = "^\\d{5}-?\\d{3}$";

        return cep.matches(cepPattern);
    }
}