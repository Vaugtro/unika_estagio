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

        // RG can be 8 or 9 digits (sometimes 10 in some states)
        if (rawRG.length() < 8 || rawRG.length() > 10) {
            return false;
        }

        // Add any additional RG validation logic here
        // Example: check if not all digits are the same
        return !rawRG.matches("(\\d)\\1{" + (rawRG.length() - 1) + "}");
    }
}