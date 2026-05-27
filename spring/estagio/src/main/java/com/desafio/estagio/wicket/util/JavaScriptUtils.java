package com.desafio.estagio.wicket.util;

import org.apache.wicket.ajax.AjaxRequestTarget;

import java.io.Serial;
import java.io.Serializable;

/**
 * Centralized static methods for common JavaScript snippets used across Wicket components.
 * <p>
 * Keeps inline JS strings in one place so that changes (e.g. library upgrades,
 * selector changes) need only one edit instead of a project-wide grep.
 */
public final class JavaScriptUtils implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private JavaScriptUtils() {
        // utility class — no instances
    }

    /**
     * Re-initialises Lucide icons after an AJAX DOM update.
     * <p>
     * Call whenever the DOM is replaced so that dynamically added icons render correctly.
     */
    public static void createIcons(AjaxRequestTarget target) {
        target.appendJavaScript("lucide.createIcons();");
    }

    /**
     * Re-applies jQuery input masks (<code>[data-mask]</code>) after an AJAX DOM update.
     * <p>
     * Masks are lost when the markup is re-rendered; this restores them without
     * needing to re-run the full page initialiser.
     */
    public static void reapplyMasks(AjaxRequestTarget target) {
        target.appendJavaScript(
                "$('[data-mask]').each(function(){$(this).mask($(this).data('mask'));});"
        );
    }

    /**
     * Shows a toast notification via the global <code>window.showToast</code>
     * function defined in {@code BasePage.html}.
     * <p>
     * Falls back to {@code alert()} if the function is not available.
     *
     * @param target  the AJAX target to append JS to
     * @param type    toast type ({@code "success"}, {@code "error"}, {@code "warning"})
     * @param message the message to display
     */
    public static void showToast(AjaxRequestTarget target, String type, String message) {
        String escapedMessage = message
                .replace("\\", "\\\\")
                .replace("'", "\\'")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
        target.appendJavaScript(String.format(
                "if (typeof window.showToast === 'function') { window.showToast('%s', '%s'); }" +
                        " else { console.error('showToast function not found'); alert('%s'); }",
                type, escapedMessage, escapedMessage));
    }

    /**
     * Highlights a form field as invalid by adding the <code>is-invalid</code> CSS class
     * and setting its sibling <code>.invalid-feedback</code> text.
     *
     * @param target    the AJAX target to append JS to
     * @param inputName the {@code name} attribute of the input element
     * @param message   the validation message to display
     */
    public static void highlightInvalidField(AjaxRequestTarget target, String inputName, String message) {
        String escapedMsg = message
                .replace("\\", "\\\\")
                .replace("'", "\\'");
        target.appendJavaScript(
                "var inp=document.querySelector('[name=\"" + inputName + "\"]');" +
                        "if(inp){inp.classList.add('is-invalid');" +
                        "var fb=inp.parentNode.querySelector('.invalid-feedback');" +
                        "if(fb)fb.textContent='" + escapedMsg + "';}");
    }

    /**
     * Returns a JavaScript snippet that navigates to the given URL.
     * <p>
     * Useful when a component needs to redirect but cannot do so via
     * Wicket's server-side redirect (e.g. after a timeout).
     *
     * @param url the destination URL (single quotes will be escaped)
     * @return a JS string suitable for {@code target.appendJavaScript(...)}
     */
    public static String navigateTo(String url) {
        String escaped = url.replace("'", "\\'");
        return "window.location.href='" + escaped + "';";
    }
}
