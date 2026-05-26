package com.desafio.estagio.validation.internal;

import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.ValidationError;

import java.io.Serial;

public class CPFValidator implements IValidator<String> {

    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    public void validate(IValidatable<String> validatable) {
        String cpf = validatable.getValue();
        if (cpf == null || cpf.isBlank()) {
            return;
        }

        String digits = cpf.replaceAll("\\D", "");
        if (digits.length() != 11) {
            validatable.error(new ValidationError("CPF deve ter 11 dígitos"));
            return;
        }

        if (digits.matches("(\\d)\\1{10}")) {
            validatable.error(new ValidationError("CPF inválido"));
            return;
        }

        int sum = 0;
        for (int i = 0; i < 9; i++) {
            sum += (digits.charAt(i) - '0') * (10 - i);
        }
        int firstCheck = 11 - (sum % 11);
        if (firstCheck >= 10) {
            firstCheck = 0;
        }
        if (firstCheck != digits.charAt(9) - '0') {
            validatable.error(new ValidationError("CPF inválido"));
            return;
        }

        sum = 0;
        for (int i = 0; i < 10; i++) {
            sum += (digits.charAt(i) - '0') * (11 - i);
        }
        int secondCheck = 11 - (sum % 11);
        if (secondCheck >= 10) {
            secondCheck = 0;
        }
        if (secondCheck != digits.charAt(10) - '0') {
            validatable.error(new ValidationError("CPF inválido"));
        }
    }
}
