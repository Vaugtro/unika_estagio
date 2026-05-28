/**
 * ViaCEP address lookup for CEP autocomplete fields.
 *
 * Dependencies: jQuery (loaded via BasePage).
 *
 * Extracted from: EnderecoCreateTablePanel.html
 *
 * Called via onblur="pesquisacep(this)" on CEP input fields.
 */
var viacepLastCallbackId = 0;

function pesquisacep(input) {
    var cep = input.value.replace(/\D/g, '');
    if (cep.length !== 8) {
        return;
    }
    var $row = $(input).closest('tr');
    var overlay = document.getElementById('cepLoadingOverlay');
    if (overlay) overlay.classList.add('active');

    var callbackId = 'viacepCallback_' + (++viacepLastCallbackId);
    var script = document.createElement('script');
    script.src = 'https://viacep.com.br/ws/' + cep + '/json/?callback=' + callbackId;

    window[callbackId] = function (data) {
        if (!data.erro) {
            $row.find('input[data-field="logradouro"]').val(data.logradouro);
            $row.find('input[data-field="bairro"]').val(data.bairro);
            $row.find('input[data-field="cidade"]').val(data.localidade);
            $row.find('[data-field="estado"]').val(data.uf);
        } else {
            var msg = 'CEP n\u00e3o encontrado.';
            if (typeof window.showToast === 'function') {
                window.showToast('warning', msg);
            } else {
                alert(msg);
            }
        }
        if (overlay) overlay.classList.remove('active');
        try {
            delete window[callbackId];
        } catch (e) {
        }
        if (script.parentNode) script.parentNode.removeChild(script);
    };

    script.onerror = function () {
        if (overlay) overlay.classList.remove('active');
        try {
            delete window[callbackId];
        } catch (e) {
        }
        if (script.parentNode) script.parentNode.removeChild(script);
        var msg = 'Erro ao consultar CEP.';
        if (typeof window.showToast === 'function') {
            window.showToast('error', msg);
        } else {
            alert(msg);
        }
    };

    document.body.appendChild(script);
}
