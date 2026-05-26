package com.desafio.estagio.wicket.component;

import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.behavior.AttributeAppender;
import org.apache.wicket.feedback.FeedbackCollector;
import org.apache.wicket.feedback.FeedbackMessage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.FormComponent;
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

    /**
     * Collects form validation errors, highlights invalid fields via JS,
     * and shows a toast with the error summary. Replaces the duplicated
     * FeedbackCollector + highlightJS + showToast pattern in modals and pages.
     */
    public static void handleFormError(AjaxRequestTarget target, Form<?> form) {
        // Preserve user input: update model even for invalid components
        // so the form re-renders with what the user typed, not the stale model.
        form.visitFormComponents((fc, visit) -> {
            if (!fc.isValid()) {
                Object converted = fc.getConvertedInput();
                if (converted != null) {
                    try {
                        ((FormComponent) fc).setModelObject(converted);
                    } catch (Exception ignored) {
                        // component may not support setting model this way
                    }
                }
            }
        });
        target.add(form);
        target.appendJavaScript("lucide.createIcons();");
        target.appendJavaScript(
                "if(typeof $ !== 'undefined' && $.fn.mask) $('[data-mask]').each(function(){$(this).mask($(this).data('mask'));});"
        );
        boolean hasErrors = false;
        StringBuilder highlightJS = new StringBuilder();
        for (FeedbackMessage msg : new FeedbackCollector(form).collect()) {
            if (msg.getLevel() != FeedbackMessage.ERROR) continue;
            hasErrors = true;
            var source = msg.getReporter();
            if (source instanceof FormComponent) {
                String inputName = ((FormComponent) source).getInputName();
                if (inputName != null) {
                    String escapedMsg = msg.getMessage().toString()
                            .replace("\\", "\\\\").replace("'", "\\'");
                    highlightJS.append(
                            "var inp=document.querySelector('[name=\"" + inputName + "\"]');" +
                                    "if(inp){inp.classList.add('is-invalid');" +
                                    "var fb=inp.parentNode.querySelector('.invalid-feedback');" +
                                    "if(fb)fb.textContent='" + escapedMsg + "';}"
                    );
                }
            }
        }
        if (highlightJS.length() > 0) {
            target.appendJavaScript(highlightJS.toString());
        }
        if (hasErrors) {
            showToast(target, "error", "Corrija os campos destacados.");
        }
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
