package com.desafio.estagio.wicket.builder;

import com.desafio.estagio.wicket.component.ValidationFeedback;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.validation.IValidator;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Fluent builder for creating a {@link TextField} with its associated feedback label
 * and validation wiring in a single chain.
 * <p>
 * Usage:
 * <pre>{@code
 * var bundle = FormFieldBuilder.create(String.class)
 *     .id("logradouro")
 *     .required()
 *     .validator(StringValidator.lengthBetween(1, 100))
 *     .placeholder("Logradouro")
 *     .attribute("data-field", "logradouro")
 *     .feedbackLabel("logradouroFeedback")
 *     .realTimeValidation()
 *     .build();
 * bundle.field().add(VALIDATION_STYLE_INSTANCE);
 * item.add(bundle.field());
 * item.add(bundle.feedbackLabel());
 * }</pre>
 */
public class FormFieldBuilder<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Class<T> type;
    private String id;
    private boolean required;
    private String placeholder;
    private final List<IValidator<? super T>> validators = new ArrayList<>();
    private final Map<String, String> attributes = new LinkedHashMap<>();
    private String feedbackId;
    private boolean realTimeValidation;

    private FormFieldBuilder(Class<T> type) {
        this.type = type;
    }

    /**
     * Creates a new builder for a field of the given type.
     *
     * @param <T>  the field value type
     * @param type the concrete class for generic type resolution
     * @return a new {@code FormFieldBuilder}
     */
    public static <T> FormFieldBuilder<T> create(Class<T> type) {
        return new FormFieldBuilder<>(type);
    }

    /**
     * Sets the Wicket component id (also used as the property expression).
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
     * Sets the {@code placeholder} attribute.
     */
    public FormFieldBuilder<T> placeholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

    /**
     * Adds a validator to the field.
     */
    public FormFieldBuilder<T> validator(IValidator<? super T> validator) {
        this.validators.add(validator);
        return this;
    }

    /**
     * Adds a custom attribute to the field.
     */
    public FormFieldBuilder<T> attribute(String name, String value) {
        this.attributes.put(name, value);
        return this;
    }

    /**
     * Sets the feedback label id. A feedback label will be created
     * via {@link ValidationFeedback#createFeedbackLabel}.
     */
    public FormFieldBuilder<T> feedbackLabel(String feedbackId) {
        this.feedbackId = feedbackId;
        return this;
    }

    /**
     * Enables real-time (blur-triggered) validation via
     * {@link ValidationFeedback#attachRealTimeValidation}.
     * Only effective when a {@code feedbackLabel} has also been set.
     */
    public FormFieldBuilder<T> realTimeValidation() {
        this.realTimeValidation = true;
        return this;
    }

    /**
     * Builds the {@link TextField} and its associated feedback label,
     * applies all configured settings, and returns both wrapped in a
     * {@link FormFieldBundle}.
     */
    public FormFieldBundle build() {
        TextField<T> field = new TextField<>(id, type);
        field.setRequired(required);

        for (IValidator<? super T> validator : validators) {
            field.add(validator);
        }

        if (placeholder != null) {
            field.add(new AttributeModifier("placeholder", placeholder));
        }

        for (Map.Entry<String, String> entry : attributes.entrySet()) {
            field.add(new AttributeModifier(entry.getKey(), entry.getValue()));
        }

        Label feedback = null;
        if (feedbackId != null) {
            feedback = ValidationFeedback.createFeedbackLabel(feedbackId, field);
            if (realTimeValidation) {
                ValidationFeedback.attachRealTimeValidation(field, feedback);
            }
        }

        return new FormFieldBundle(field, feedback);
    }
}
