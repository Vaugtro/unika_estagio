package com.desafio.estagio.wicket.builder;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.TextField;

import java.io.Serial;
import java.io.Serializable;

/**
 * Container holding a {@link TextField} and its associated feedback {@link Label}.
 * <p>
 * Returned by {@link FormFieldBuilder#build()} so both field and feedback
 * label can be added to the component tree together.
 */
public class FormFieldBundle implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final TextField<?> field;
    private final Label feedbackLabel;

    public FormFieldBundle(TextField<?> field, Label feedbackLabel) {
        this.field = field;
        this.feedbackLabel = feedbackLabel;
    }

    public TextField<?> getField() {
        return field;
    }

    public Label getFeedbackLabel() {
        return feedbackLabel;
    }
}
