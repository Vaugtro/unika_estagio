package com.desafio.estagio.validation.annotation;

import com.desafio.estagio.validation.internal.TelefoneValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = TelefoneValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidTelefone {
    String message() default "Invalid telephone format (expected (XX) XXXXX-XXXX or (XX) XXXX-XXXX)";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}