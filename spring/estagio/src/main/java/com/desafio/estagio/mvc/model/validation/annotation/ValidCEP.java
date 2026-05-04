package com.desafio.estagio.mvc.model.validation.annotation;

import com.desafio.estagio.mvc.model.validation.internal.CEPValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = CEPValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidCEP {
    String message() default "Invalid CEP format or number";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}