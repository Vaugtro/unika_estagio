package com.desafio.estagio.wicket.builder;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.behavior.Behavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.validation.IValidator;
import org.apache.wicket.validation.validator.PatternValidator;
import org.apache.wicket.validation.validator.StringValidator;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Fluent builder for constructing Wicket {@link TextField} instances with
 * validators, attribute modifiers, feedback labels, and real-time validation.
 * <p>
 * Usage:
 * <pre>{@code
 * FormFieldBundle field = FormFieldBuilder.create(String.class)
 *     .id("nome").required()
 *     .placeholder("Nome").dataField("nome")
 *     .maxLength(100).minLength(3)
 *     .feedbackLabel("nomeFeedback")
 *     .realTimeValidation().validationStyle(myStyle)
 *     .build();
 * item.add(field.getField());
 * item.add(field.getFeedbackLabel());
 * }</pre>
 *
 * @param <T> the field value type
 */
public class FormFieldBuilder<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Class<T> fieldType;
    private final List<IValidator<? super T>> validators = new ArrayList<>();
    private final List<AttributeModifier> modifiers = new ArrayList<>();
    private String id;
    private boolean required;
    private String placeholder;
    private String dataField;
    private String dataMask;
    private String onblur;
    private String cssClass;
    private String regexPattern;
    private Integer maxLength;
    private Integer minLength;
    private Integer exactLength;
    private String feedbackLabelId;
    private boolean realTimeValidation;
    private Behavior validationStyleBehavior;

    private FormFieldBuilder(Class<T> fieldType) {
        this.fieldType = fieldType;
    }

    /**
     * Creates a new {@link FormFieldBuilder} for the given field type.
     *
     * @param fieldType the value type of the text field (e.g. {@code String.class}, {@code Long.class})
     * @param <T>       the field value type
     * @return a new builder instance
     */
    public static <T> FormFieldBuilder<T> create(Class<T> fieldType) {
        return new FormFieldBuilder<>(fieldType);
    }

    /**
     * Sets the Wicket component id (wicket:id).
     */
    public FormFieldBuilder<T> id(String id) {
        this.id = id;
        return this;
    }

    /**
     * Marks the field as required.
     */
    public FormFieldBuilder<T> required() {
        this.required = true;
        return this;
    }

    /**
     * Adds a {@code placeholder} attribute.
     */
    public FormFieldBuilder<T> placeholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    /**
     * Adds a {@code data-field} attribute.
     */
    public FormFieldBuilder<T> dataField(String dataField) {
        this.dataField = dataField;
        return this;
    }

    /**
     * Adds a {@code data-mask} attribute (for jQuery Mask Plugin).
     */
    public FormFieldBuilder<T> dataMask(String dataMask) {
        this.dataMask = dataMask;
        return this;
    }

    /**
     * Adds an {@code onblur} JavaScript handler.
     */
    public FormFieldBuilder<T> onblur(String onblur) {
        this.onblur = onblur;
        return this;
    }

    /**
     * Adds a CSS class attribute.
     */
    public void cssClass(String cssClass) {
        this.cssClass = cssClass;
    }

    /**
     * Adds a custom validator.
     */
    public FormFieldBuilder<T> validator(IValidator<? super T> validator) {
        this.validators.add(validator);
        return this;
    }

    /**
     * Adds a generic attribute modifier.
     */
    public FormFieldBuilder<T> attribute(String name, String value) {
        this.modifiers.add(new AttributeModifier(name, value));
        return this;
    }

    /**
     * Adds {@link StringValidator#maximumLength(int)}.
     */
    public FormFieldBuilder<T> maxLength(int max) {
        this.maxLength = max;
        return this;
    }

    /**
     * Adds {@link StringValidator#minimumLength(int)}.
     */
    public FormFieldBuilder<T> minLength(int min) {
        this.minLength = min;
        return this;
    }

    /**
     * Adds {@link StringValidator#exactLength(int)}.
     */
    public FormFieldBuilder<T> exactLength(int length) {
        this.exactLength = length;
        return this;
    }

    /**
     * Adds a {@link PatternValidator} with the given regex.
     */
    public FormFieldBuilder<T> pattern(String regex) {
        this.regexPattern = regex;
        return this;
    }

    /**
     * Sets the feedback label id. Creates a feedback label via
     * {@link com.desafio.estagio.wicket.component.ValidationFeedback#createFeedbackLabel}.
     */
    public FormFieldBuilder<T> feedbackLabel(String feedbackId) {
        this.feedbackLabelId = feedbackId;
        return this;
    }

    /**
     * Enables real-time validation on blur using
     * {@link com.desafio.estagio.wicket.component.ValidationFeedback#attachRealTimeValidation}.
     * Requires {@link #feedbackLabel(String)} to also be set.
     */
    public FormFieldBuilder<T> realTimeValidation() {
        this.realTimeValidation = true;
        return this;
    }

    /**
     * Adds a validation style {@link Behavior} (e.g. adds {@code is-invalid} class on error).
     */
    public FormFieldBuilder<T> validationStyle(Behavior behavior) {
        this.validationStyleBehavior = behavior;
        return this;
    }

    /**
     * Builds the {@link TextField} and optional feedback {@link Label},
     * applying all configured validators, attribute modifiers, and behaviors.
     *
     * @return a {@link FormFieldBundle} containing the field and its feedback label
     */
    @SuppressWarnings("unchecked")
    public FormFieldBundle build() {
        TextField<T> field = new TextField<>(id, fieldType);
        if (required) {
            field.setRequired(true);
        }

        // String-based validators (maxLength, minLength, exactLength, pattern)
        // are cast to IValidator<? super T> because they are never used with non-String types.
        if (maxLength != null || minLength != null) {
            if (minLength != null && maxLength != null) {
                field.add((IValidator<? super T>) StringValidator.lengthBetween(minLength, maxLength));
            } else if (maxLength != null) {
                field.add((IValidator<? super T>) StringValidator.maximumLength(maxLength));
            } else if (minLength != null) {
                field.add((IValidator<? super T>) StringValidator.minimumLength(minLength));
            }
        }
        if (exactLength != null) {
            field.add((IValidator<? super T>) StringValidator.exactLength(exactLength));
        }
        if (regexPattern != null) {
            field.add((IValidator<? super T>) new PatternValidator(regexPattern));
        }
        validators.forEach(field::add);

        // Attribute modifiers
        if (placeholder != null) {
            field.add(new AttributeModifier("placeholder", placeholder));
        }
        if (dataField != null) {
            field.add(new AttributeModifier("data-field", dataField));
        }
        if (dataMask != null) {
            field.add(new AttributeModifier("data-mask", dataMask));
        }
        if (onblur != null) {
            field.add(new AttributeModifier("onblur", onblur));
        }
        if (cssClass != null) {
            field.add(new AttributeModifier("class", cssClass));
        }
        modifiers.forEach(field::add);

        // Feedback label
        Label feedbackLabel = null;
        if (feedbackLabelId != null) {
            feedbackLabel = com.desafio.estagio.wicket.component.ValidationFeedback.createFeedbackLabel(feedbackLabelId, field);
            if (realTimeValidation) {
                com.desafio.estagio.wicket.component.ValidationFeedback.attachRealTimeValidation(field, feedbackLabel);
            }
        }

        if (validationStyleBehavior != null) {
            field.add(validationStyleBehavior);
        }

        field.setOutputMarkupId(true);
        return new FormFieldBundle(field, feedbackLabel);
    }
}
