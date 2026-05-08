package com.desafio.estagio.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.hibernate.validator.internal.constraintvalidators.hv.br.CNPJValidator;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CNPJValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCNPJ {
    String message() default "Invalid CNPJ number";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}