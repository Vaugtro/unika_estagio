package com.desafio.estagio.validation.internal;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import java.io.Serial;

public class CNPJValidator implements IValidator<String> {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void validate(IValidatable<String> validatable) {
        String cnpj = validatable.getValue();
        if (cnpj == null || cnpj.isBlank()) {
            return;
        }

        String digits = cnpj.replaceAll("\\D", "");
        if (digits.length() != 14) {
            validatable.error(new ValidationError("CNPJ deve ter 14 dígitos"));
            return;
        }

        if (digits.matches("(\\d)\\1{13}")) {
            validatable.error(new ValidationError("CNPJ inválido"));
            return;
        }

        int[] weights1 = {5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        int sum = 0;
        for (int i = 0; i < 12; i++) {
            sum += (digits.charAt(i) - '0') * weights1[i];
        }
        int firstCheck = 11 - (sum % 11);
        if (firstCheck >= 10) {
            firstCheck = 0;
        }
        if (firstCheck != digits.charAt(12) - '0') {
            validatable.error(new ValidationError("CNPJ inválido"));
            return;
        }

        int[] weights2 = {6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2};
        sum = 0;
        for (int i = 0; i < 13; i++) {
            sum += (digits.charAt(i) - '0') * weights2[i];
        }
        int secondCheck = 11 - (sum % 11);
        if (secondCheck >= 10) {
            secondCheck = 0;
        }
        if (secondCheck != digits.charAt(13) - '0') {
            validatable.error(new ValidationError("CNPJ inválido"));
        }
    }
}
