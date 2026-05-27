function aplicarMascaras() {
    $('[data-mask]').each(function () {
        $(this).mask($(this).data('mask'));
    });
}
