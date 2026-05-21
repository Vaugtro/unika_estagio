package com.desafio.estagio.validation.internal;

import com.desafio.estagio.validation.annotation.ValidTelefone;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class TelefoneValidator implements ConstraintValidator<ValidTelefone, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }

        String digitsOnly = value.replaceAll("\\D", "");

        // 10-digit landline: area code [1-9]{2}, number starts with [2-8], 7 remaining digits
        // 11-digit mobile: area code [1-9]{2}, number starts with 9, 8 remaining digits
        if (digitsOnly.length() == 10) {
            return digitsOnly.matches("^[1-9]{2}[2-8][0-9]{7}$");
        } else if (digitsOnly.length() == 11) {
            return digitsOnly.matches("^[1-9]{2}9[0-9]{8}$");
        }
        return false;
    }
}