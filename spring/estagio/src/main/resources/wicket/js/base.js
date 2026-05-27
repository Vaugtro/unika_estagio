// Initialize Lucide icons
document.addEventListener('DOMContentLoaded', function () {
    lucide.createIcons();
});

// Handle browser back/forward cache (bfcache) — DOMContentLoaded
// does NOT fire when page is restored from bfcache on back navigation.
window.addEventListener('pageshow', function () {
    lucide.createIcons();
});

// Reinitialize after Wicket AJAX updates
if (typeof Wicket !== 'undefined') {
    Wicket.Event.subscribe('/ajax/call/response', function () {
        lucide.createIcons();
    });
}

window.showToast = function (type, message) {
    console.log('Showing toast:', type, message);

    // Get or create toast container
    var toastContainer = document.getElementById('toast-container');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.id = 'toast-container';
        toastContainer.className = 'toast-container';
        document.body.appendChild(toastContainer);
    }

    // Determine header color and icon based on type
    var headerClass = '';
    var icon = '';
    switch (type) {
        case 'success':
            headerClass = 'bg-success text-white';
            icon = '\u2705';
            break;
        case 'error':
            headerClass = 'bg-danger text-white';
            icon = '\u274C';
            break;
        case 'warning':
            headerClass = 'bg-warning text-dark';
            icon = '\u26A0\uFE0F';
            break;
        case 'info':
            headerClass = 'bg-info text-white';
            icon = '\u2139\uFE0F';
            break;
        default:
            headerClass = 'bg-secondary text-white';
            icon = '\uD83D\uDCE2';
    }

    // Create unique ID for this toast
    var toastId = 'toast-' + Date.now() + '-' + Math.random().toString(36).substr(2, 9);

    // Create toast HTML
    var toastHtml = '' +
        '<div id="' + toastId + '" class="toast" role="alert" aria-live="assertive" aria-atomic="true" data-bs-autohide="true" data-bs-delay="5000">' +
        '    <div class="toast-header ' + headerClass + '">' +
        '        <strong class="me-auto">' + icon + ' ' + type.charAt(0).toUpperCase() + type.slice(1) + '</strong>' +
        '        <button type="button" class="btn-close btn-close-white" data-bs-dismiss="toast" aria-label="Close"></button>' +
        '    </div>' +
        '    <div class="toast-body">' + message + '</div>' +
        '</div>';

    // Add toast to container
    toastContainer.insertAdjacentHTML('beforeend', toastHtml);

    // Initialize and show the toast
    var toastElement = document.getElementById(toastId);
    var toast = new bootstrap.Toast(toastElement, {
        animation: true,
        autohide: true,
        delay: 5000
    });

    // Remove toast from DOM after it's hidden
    toastElement.addEventListener('hidden.bs.toast', function () {
        toastElement.remove();
    });

    toast.show();
};
