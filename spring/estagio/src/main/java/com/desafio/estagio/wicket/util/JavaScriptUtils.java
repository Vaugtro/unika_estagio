package com.desafio.estagio.wicket.util;

import org.apache.wicket.ajax.AjaxRequestTarget;

import java.io.Serial;
import java.io.Serializable;

public final class JavaScriptUtils implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private JavaScriptUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    public static void reloadLucideIcons(AjaxRequestTarget target) {
        target.appendJavaScript("lucide.createIcons();");
    }

    public static void reloadLucideIconsSafe(AjaxRequestTarget target) {
        target.appendJavaScript("if(typeof lucide !== 'undefined') lucide.createIcons();");
    }

    public static void reinitializeMasks(AjaxRequestTarget target) {
        target.appendJavaScript(
                "$('[data-mask]').each(function(){$(this).mask($(this).data('mask'));});"
        );
    }

    public static void reinitializeMasksSafe(AjaxRequestTarget target) {
        target.appendJavaScript(
                "if(typeof $ !== 'undefined' && $.fn.mask) $('[data-mask]').each(function(){$(this).mask($(this).data('mask'));});"
        );
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

    public static void highlightInvalidField(AjaxRequestTarget target, String inputName, String message) {
        String escapedMsg = message != null ? message
                .replace("\\", "\\\\").replace("'", "\\'") : "";
        target.appendJavaScript(
                "var inp=document.querySelector('[name=\"" + inputName + "\"]');" +
                        "if(inp){inp.classList.add('is-invalid');" +
                        "var fb=inp.parentNode.querySelector('.invalid-feedback');" +
                        "if(fb)fb.textContent='" + escapedMsg + "';}"
        );
    }

    public static void reloadPage(AjaxRequestTarget target, int delayMs) {
        target.appendJavaScript(
                "setTimeout(function(){window.location.reload();}," + delayMs + ");");
    }

    public static void focusAndScroll(AjaxRequestTarget target, String componentMarkupId) {
        target.appendJavaScript(
                "setTimeout(function(){var el=document.getElementById('" + componentMarkupId + "');" +
                        "if(el){el.focus();el.scrollIntoView({behavior:'smooth',block:'center'});}},100);"
        );
    }

    public static void showBootstrapModal(AjaxRequestTarget target, String modalId) {
        target.appendJavaScript("$('#" + modalId + "').modal('show');");
    }

    public static void hideBootstrapModal(AjaxRequestTarget target, String modalId) {
        target.appendJavaScript("$('#" + modalId + "').modal('hide');");
    }

    public static void hideAndRemoveBootstrapModal(AjaxRequestTarget target, String modalId) {
        target.appendJavaScript(
                "$('#" + modalId + "').modal('hide');" +
                        "setTimeout(function(){ $('#" + modalId + "').remove(); }, 500);"
        );
    }
}
