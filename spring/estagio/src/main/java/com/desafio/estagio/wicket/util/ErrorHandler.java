package com.desafio.estagio.wicket.util;

import com.desafio.estagio.exceptions.BusinessException;
import com.desafio.estagio.wicket.component.ValidationFeedback;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.springframework.dao.DataIntegrityViolationException;

import java.io.Serial;
import java.io.Serializable;
import java.util.function.Supplier;

public final class ErrorHandler implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private ErrorHandler() {
        // utility class — prevent instantiation
    }

    /**
     * Wraps a service call with try/catch handling, showing toast messages for errors.
     * <ul>
     *   <li>{@link DataIntegrityViolationException} → "Registro já existe."</li>
     *   <li>{@link BusinessException} → {@code e.getMessage()}</li>
     *   <li>{@link Exception} → "Erro inesperado. Tente novamente."</li>
     * </ul>
     */
    public static void handleServiceCall(Runnable serviceCall, AjaxRequestTarget target) {
        try {
            serviceCall.run();
        } catch (DataIntegrityViolationException e) {
            ValidationFeedback.showToast(target, "error", "Registro já existe.");
        } catch (BusinessException e) {
            ValidationFeedback.showToast(target, "error", e.getMessage());
        } catch (Exception e) {
            ValidationFeedback.showToast(target, "error", "Erro inesperado. Tente novamente.");
        }
    }

    /**
     * Wraps a service call with try/catch handling and returns a value.
     * On error, shows a toast and returns {@code null}.
     * <ul>
     *   <li>{@link DataIntegrityViolationException} → "Registro já existe."</li>
     *   <li>{@link BusinessException} → {@code e.getMessage()}</li>
     *   <li>{@link Exception} → "Erro inesperado. Tente novamente."</li>
     * </ul>
     *
     * @param <T> the return type of the service call
     * @return the service result, or {@code null} if an exception was caught
     */
    public static <T> T handleServiceCall(Supplier<T> serviceCall, AjaxRequestTarget target) {
        try {
            return serviceCall.get();
        } catch (DataIntegrityViolationException e) {
            ValidationFeedback.showToast(target, "error", "Registro já existe.");
        } catch (BusinessException e) {
            ValidationFeedback.showToast(target, "error", e.getMessage());
        } catch (Exception e) {
            ValidationFeedback.showToast(target, "error", "Erro inesperado. Tente novamente.");
        }
        return null;
    }

    /**
     * Wraps a delete call with error handling, showing entity-specific messages.
     * <ul>
     *   <li>{@link DataIntegrityViolationException} → "Não é possível excluir {entityName}..."</li>
     *   <li>{@link BusinessException} → {@code e.getMessage()}</li>
     *   <li>{@link Exception} → "Erro inesperado ao excluir {entityName}..."</li>
     * </ul>
     *
     * @param entityName the display name of the entity being deleted (e.g. "cliente", "endereço")
     */
    public static void handleDelete(Runnable deleteCall, AjaxRequestTarget target, String entityName) {
        try {
            deleteCall.run();
        } catch (DataIntegrityViolationException e) {
            ValidationFeedback.showToast(target, "error",
                    "Não é possível excluir " + entityName + " pois está vinculado a outros registros.");
        } catch (BusinessException e) {
            ValidationFeedback.showToast(target, "error", e.getMessage());
        } catch (Exception e) {
            ValidationFeedback.showToast(target, "error",
                    "Erro inesperado ao excluir " + entityName + ". Tente novamente.");
        }
    }

    /**
     * Wraps a save call with success feedback, form update, and error handling.
     * On success, shows the provided success message and adds the form to the AJAX target.
     * On error, shows the appropriate error toast (the form is not updated).
     * <ul>
     *   <li>{@link DataIntegrityViolationException} → "Registro já existe."</li>
     *   <li>{@link BusinessException} → {@code e.getMessage()}</li>
     *   <li>{@link Exception} → "Erro inesperado ao salvar. Tente novamente."</li>
     * </ul>
     *
     * @param form       the form component to update on success (e.g. for re-rendering the list)
     * @param successMsg the message to show on success
     */
    public static void handleSave(Runnable saveCall, AjaxRequestTarget target, Component form, String successMsg) {
        try {
            saveCall.run();
            ValidationFeedback.showToast(target, "success", successMsg);
            target.add(form);
        } catch (DataIntegrityViolationException e) {
            ValidationFeedback.showToast(target, "error", "Registro já existe.");
        } catch (BusinessException e) {
            ValidationFeedback.showToast(target, "error", e.getMessage());
        } catch (Exception e) {
            ValidationFeedback.showToast(target, "error", "Erro inesperado ao salvar. Tente novamente.");
        }
    }
}
