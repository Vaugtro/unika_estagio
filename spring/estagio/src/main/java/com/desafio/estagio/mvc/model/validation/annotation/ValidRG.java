package com.desafio.estagio.mvc.model.validation.annotation;

import com.desafio.estagio.mvc.model.validation.internal.RGValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RGValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidRG {
    String message() default "Invalid RG format or number";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}