/**
 * Shared jQuery mask initialisation for [data-mask] fields.
 *
 * Dependencies: jQuery + jquery.mask plugin (loaded via CDN).
 *
 * Extracted from:
 *   - ClienteFisicoCreateModal.html
 *   - ClienteJuridicoCreateModal.html
 *   - EnderecoListViewPanel.html
 *
 * This file is loaded via JavaScriptResourceReference so it participates
 * in Wicket's header contribution deduplication.
 */
function aplicarMascaras() {
    $('[data-mask]').each(function () {
        var m = $(this).data('mask');
        if (typeof m === 'string') $(this).mask(m);
    });
}

$(document).ready(function () {
    aplicarMascaras();
});

// Reapply masks when modals are shown (survives Wicket AJAX re-renders)
$(document).on('shown.bs.modal', '#createClienteFisicoModal, #createClienteJuridicoModal', function () {
    aplicarMascaras();
});
