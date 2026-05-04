package com.desafio.estagio.mvc.model.validation.internal;

import com.desafio.estagio.mvc.model.validation.annotation.ValidRG;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RGValidator implements ConstraintValidator<ValidRG, String> {

    @Override
    public boolean isValid(String rg, ConstraintValidatorContext context) {
        if (rg == null || rg.isEmpty()) return true;

        // Remove dots and dashes
        rg = rg.replaceAll("[^0-9Xx]", "");

        // Basic check for common 9-digit format (SP)
        if (rg.length() != 9) return false;

        return isRGValid(rg);
    }

    private boolean isRGValid(String rg) {
        // Calculation for SP Standard:
        // Weights: 2, 3, 4, 5, 6, 7, 8, 9 (for the first 8 digits)
        int sum = 0;
        for (int i = 0; i < 8; i++) {
            sum += (rg.charAt(i) - '0') * (i + 2);
        }

        int remainder = sum % 11;
        char expectedDigit;

        if (remainder == 0) {
            expectedDigit = '0';
        } else if (remainder == 1) {
            expectedDigit = 'X';
        } else {
            expectedDigit = (char) ((11 - remainder) + '0');
        }

        char actualDigit = Character.toUpperCase(rg.charAt(8));
        return actualDigit == expectedDigit;
    }
}