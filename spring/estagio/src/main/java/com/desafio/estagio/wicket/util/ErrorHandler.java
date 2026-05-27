package com.desafio.estagio.wicket.util;

import com.desafio.estagio.exceptions.BusinessException;
import com.desafio.estagio.wicket.component.ValidationFeedback;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.springframework.dao.DataIntegrityViolationException;

public final class ErrorHandler {

    private ErrorHandler() {
    }

    @FunctionalInterface
    public interface ThrowingRunnable {
        void run() throws Exception;
    }

    /**
     * Handles service calls (create/update) with standardized error handling.
     * Catches {@link DataIntegrityViolationException}, {@link BusinessException},
     * and generic {@link Exception} to provide user-friendly toast messages.
     *
     * @param target   Ajax request target for toast feedback
     * @param form     Form for validation error handling
     * @param runnable The service call to execute
     */
    public static void handleServiceCall(AjaxRequestTarget target, Form<?> form, ThrowingRunnable runnable) {
        try {
            runnable.run();
        } catch (DataIntegrityViolationException e) {
            ValidationFeedback.showToast(target, "error", "Registro já existe.");
        } catch (BusinessException e) {
            ValidationFeedback.showToast(target, "error", e.getMessage());
        } catch (Exception e) {
            ValidationFeedback.showToast(target, "error", "Erro interno.");
        }
    }

    /**
     * Handles delete operations with standardized error handling.
     * On success, shows a confirmation toast.
     *
     * @param target   Ajax request target for toast feedback
     * @param runnable The delete operation to execute
     */
    public static void handleDelete(AjaxRequestTarget target, ThrowingRunnable runnable) {
        try {
            runnable.run();
            ValidationFeedback.showToast(target, "success", "Registro excluído com sucesso!");
        } catch (DataIntegrityViolationException e) {
            ValidationFeedback.showToast(target, "error", "Registro em uso.");
        } catch (Exception e) {
            ValidationFeedback.showToast(target, "error", "Erro ao excluir registro.");
        }
    }

    /**
     * Handles file operations (export/import) with standardized error handling.
     *
     * @param target   Ajax request target for toast feedback
     * @param runnable The file operation to execute
     */
    public static void handleFileOperation(AjaxRequestTarget target, ThrowingRunnable runnable) {
        try {
            runnable.run();
        } catch (DataIntegrityViolationException e) {
            ValidationFeedback.showToast(target, "error", "Registro em uso.");
        } catch (Exception e) {
            ValidationFeedback.showToast(target, "error", "Erro na operação de arquivo: " + e.getMessage());
        }
    }
}
