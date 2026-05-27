package com.desafio.estagio.wicket.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WicketField {

    /** wicket:id — defaults to the Java field name if empty */
    String id();

    boolean required() default false;

    String placeholder() default "";

    /** data-field attribute */
    String dataField() default "";

    /** data-mask attribute (jQuery Mask Plugin) */
    String dataMask() default "";

    /** onblur JavaScript snippet */
    String onblur() default "";

    /** CSS class added via AttributeModifier */
    String cssClass() default "";

    /** Regex forwarded to PatternValidator */
    String pattern() default "";

    int maxLength() default -1;
    int minLength() default -1;
    int exactLength() default -1;

    /** If set, creates a feedback label with this wicket:id */
    String feedbackLabel() default "";

    /** Enables real-time validation on blur */
    boolean realTimeValidation() default false;

    /** Generic attributes as key-value pairs: {"key1","val1","key2","val2"} */
    String[] attributes() default {};
}
