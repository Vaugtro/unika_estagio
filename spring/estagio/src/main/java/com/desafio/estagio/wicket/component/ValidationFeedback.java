package com.desafio.estagio.wicket.component;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;

import java.io.Serial;
import java.io.Serializable;

public final class ValidationFeedback implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public static Label createFeedbackLabel(String id, Component component) {
        Label label = new Label(id, new AbstractReadOnlyModel<String>() {
            @Serial
            private static final long serialVersionUID = 1L;
            @Override
            public String getObject() {
                var msg = component.getFeedbackMessages().first();
                return msg != null ? msg.getMessage().toString() : "";
            }
        });
        label.setOutputMarkupId(true);
        return label;
    }

    public static void attachRealTimeValidation(Component field, Component feedback) {
        field.setOutputMarkupId(true);
        field.add(new AttributeAppender("class", new AbstractReadOnlyModel<String>() {
            @Serial
            private static final long serialVersionUID = 1L;
            @Override
            public String getObject() {
                return !field.getFeedbackMessages().isEmpty() ? " is-invalid" : "";
            }
        }));
        field.add(new AjaxFormComponentUpdatingBehavior("blur") {
            @Serial
            private static final long serialVersionUID = 1L;
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.add(field);
                target.add(feedback);
            }
        });
    }

    public static void showToast(AjaxRequestTarget target, String type, String message) {
        String escapedMessage = message
                .replace("\\", "\\\\").replace("'", "\\'")
                .replace("\"", "\\\"").replace("\n", "\\n")
                .replace("\r", "\\r");
        target.appendJavaScript(String.format(
                "if (typeof window.showToast === 'function') { window.showToast('%s', '%s'); }" +
                " else { console.error('showToast function not found'); alert('%s'); }",
                type, escapedMessage, escapedMessage));
    }
}
