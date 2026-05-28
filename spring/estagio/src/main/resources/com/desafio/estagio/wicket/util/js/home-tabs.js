/**
 * Active tab highlight for HomePage's client switch buttons.
 *
 * Wicket AjaxLink does not set active class natively, so we use
 * a simple click handler to keep the visual state consistent.
 *
 * Extracted from: HomePage.html
 */
document.addEventListener('DOMContentLoaded', function () {
    var buttons = document.querySelectorAll('[wicket\\:id="btnFisicos"], [wicket\\:id="btnJuridicos"]');
    buttons.forEach(function (btn) {
        btn.addEventListener('click', function () {
            buttons.forEach(function (b) {
                b.style.background = 'white';
                b.style.color = '#495057';
            });
        });
    });
});
