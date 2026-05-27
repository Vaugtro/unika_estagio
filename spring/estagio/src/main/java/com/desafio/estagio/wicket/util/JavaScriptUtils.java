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

    // ──────────────────────────────────────────────
    //  Wave 3 — extracted from inline appendJavaScript calls
    // ──────────────────────────────────────────────

    /**
     * Calls {@code lucide.createIcons()} only when the Lucide library is loaded.
     * <p>
     * Safer than {@link #createIcons(AjaxRequestTarget)} for contexts where
     * {@code lucide} may not be on the page yet.
     */
    public static void createIconsSafe(AjaxRequestTarget target) {
        target.appendJavaScript("if(typeof lucide !== 'undefined') lucide.createIcons();");
    }

    /**
     * Re-applies jQuery input masks with a guard for the {@code $} and
     * {@code $.fn.mask} globals.
     * <p>
     * Safer than {@link #reapplyMasks(AjaxRequestTarget)} for contexts where
     * jQuery or the mask plugin may not be available.
     */
    public static void reapplyMasksSafe(AjaxRequestTarget target) {
        target.appendJavaScript(
                "if(typeof $ !== 'undefined' && $.fn.mask) $('[data-mask]').each(function(){$(this).mask($(this).data('mask'));});"
        );
    }

    /**
     * Shows a Bootstrap modal by its DOM id.
     *
     * @param target  the AJAX target to append JS to
     * @param modalId the DOM id of the modal element (without {@code #})
     */
    public static void showModal(AjaxRequestTarget target, String modalId) {
        target.appendJavaScript("$('#" + modalId + "').modal('show');");
    }

    /**
     * Shows a Bootstrap modal and re-initialises Lucide icons (with guard).
     *
     * @param target  the AJAX target to append JS to
     * @param modalId the DOM id of the modal element (without {@code #})
     */
    public static void showModalWithIcons(AjaxRequestTarget target, String modalId) {
        target.appendJavaScript(
                "$('#" + modalId + "').modal('show');" +
                        "if(typeof lucide !== 'undefined') lucide.createIcons();"
        );
    }

    /**
     * Hides a Bootstrap modal and removes its DOM element after a short delay.
     *
     * @param target  the AJAX target to append JS to
     * @param modalId the DOM id of the modal element (without {@code #})
     */
    public static void hideModalAndRemove(AjaxRequestTarget target, String modalId) {
        target.appendJavaScript(
                "$('#" + modalId + "').modal('hide');" +
                        "setTimeout(function(){ $('#" + modalId + "').remove(); }, 500);"
        );
    }

    /**
     * Reloads the current page after a delay.
     *
     * @param target  the AJAX target to append JS to
     * @param delayMs delay in milliseconds before reload
     */
    public static void reloadAfterDelay(AjaxRequestTarget target, int delayMs) {
        target.appendJavaScript(
                "setTimeout(function(){window.location.reload();}," + delayMs + ");"
        );
    }

    /**
     * Calls the custom {@code abrirModalEndereco()} function defined in
     * the EnderecoListViewPanel HTML template.
     */
    public static void callAbrirModalEndereco(AjaxRequestTarget target) {
        target.appendJavaScript("abrirModalEndereco();");
    }

    /**
     * Calls the custom {@code fecharModalEndereco()} function defined in
     * the EnderecoListViewPanel HTML template.
     */
    public static void callFecharModalEndereco(AjaxRequestTarget target) {
        target.appendJavaScript("fecharModalEndereco();");
    }
}
