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
public record FormFieldBundle(TextField<?> field, Label feedbackLabel) implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
}
