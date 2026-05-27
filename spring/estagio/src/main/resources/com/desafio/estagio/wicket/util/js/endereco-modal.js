/**
 * EnderecoListViewPanel modal open/close helpers.
 *
 * Dependencies: jQuery + jquery.mask + Bootstrap 5 + Lucide.
 *
 * Extracted from: EnderecoListViewPanel.html
 *
 * Referenced by Java code via JavaScriptUtils.callAbrirModalEndereco()
 * and JavaScriptUtils.callFecharModalEndereco().
 */
function abrirModalEndereco() {
    var modal = new bootstrap.Modal(document.getElementById('enderecoModal'));
    modal.show();

    setTimeout(function () {
        $('#enderecoModal [data-mask]').each(function () {
            $(this).mask($(this).data('mask'));
        });
        lucide.createIcons();
    }, 100);
}

function fecharModalEndereco() {
    var modalEl = document.getElementById('enderecoModal');
    var modal = bootstrap.Modal.getInstance(modalEl);
    if (modal) modal.hide();
}
