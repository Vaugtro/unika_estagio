package com.desafio.estagio.wicket.builder;

import org.apache.wicket.Component;

import java.io.Serial;
import java.io.Serializable;

/**
 * Simple holder for a form field {@link Component} and its associated
 * feedback label. Returned by {@link FormFieldBuilder#build()}.
 */
public class FormFieldBundle implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final Component field;
    private final Component feedbackLabel;

    /**
     * @param field         the form field component
     * @param feedbackLabel the feedback label component (may be {@code null})
     */
    public FormFieldBundle(Component field, Component feedbackLabel) {
        this.field = field;
        this.feedbackLabel = feedbackLabel;
    }

    /**
     * Returns the form field component.
     */
    public Component field() {
        return field;
    }

    /**
     * Returns the feedback label component, or {@code null} if none was configured.
     */
    public Component feedbackLabel() {
        return feedbackLabel;
    }
}
