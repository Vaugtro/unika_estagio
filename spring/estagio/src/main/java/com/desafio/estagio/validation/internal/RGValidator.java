package com.desafio.estagio.validation.internal;

import com.desafio.estagio.validation.annotation.ValidRG;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class RGValidator implements ConstraintValidator<ValidRG, String> {

    @Override
    public boolean isValid(String rg, ConstraintValidatorContext context) {
        if (rg == null || rg.isBlank()) {
            return false;
        }

        // Remove any non-digit characters (handles formatting like "12.345.678-9")
        String rawRG = rg.replaceAll("\\D", "");

        // RG can be 7-9 digits (varies by state, column is VARCHAR(9))
        if (rawRG.length() < 7 || rawRG.length() > 9) {
            return false;
        }

        // Add any additional RG validation logic here
        // Example: check if not all digits are the same
        return !rawRG.matches("(\\d)\\1{" + (rawRG.length() - 1) + "}");
    }
}