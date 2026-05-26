## 2026-05-26 Task: Input mask AJAX initialization

### Problem
Input masks (CEP, CPF, CNPJ, TELEFONE, RG via `data-mask` + jQuery Mask plugin) stopped working after AJAX updates. On initial page load, `$(document).ready()` fires once and masks work. But when Wicket re-renders components via AJAX (modal opens, new endereco row added, form re-rendered on validation error), the `$(document).ready()` handler never re-executes for the new DOM elements.

### Root Cause
`$(document).ready()` fires exactly ONCE when the page's initial DOM is ready. Wicket AJAX responses extract `<script>` content and evaluate it separately, but `$(document).ready()` callbacks inside those scripts do NOT re-fire because the document has already loaded. This means any `$(document).ready()`-based initialization is invisible to AJAX-updated content.

### Fix Pattern (3-part)

**1. HTML templates — use `shown.bs.modal` event instead of relying solely on `$(document.ready()`:**
```javascript
function aplicarMascaras() {
    $('[data-mask]').each(function () {
        $(this).mask($(this).data('mask'));
    });
}
$(document).ready(aplicarMascaras);
$('#createClienteFisicoModal').on('shown.bs.modal', aplicarMascaras);
```

**2. Java components — append JS after AJAX replace:**
When a Wicket component re-renders a fragment via AJAX (e.g., adding a table row), append a JavaScript call that re-applies masks:
```java
target.appendJavaScript("$('[data-mask]').each(function(){$(this).mask($(this).data('mask'));});");
```

**3. Modal placeholder initialization (if already working with `abrirModal` pattern):**
If a component already has a JavaScript function that opens the modal (like `abrirModalEndereco()`), add a `setTimeout` with mask re-apply inside it.

### Files that needed changes
- `ClienteFisicoCreateModal.html` — modal show listener
- `ClienteJuridicoCreateModal.html` — modal show listener
- `EnderecoCreateTablePanel.java` — JS append on new row add

## 2026-05-26 Task: Toggle button AJAX refresh
- Every AJAX component change MUST call: `target.appendJavaScript("if(typeof lucide !== 'undefined') lucide.createIcons();");`
- When toggling status from DataView row, refresh the entire `tableContainer` (not just the row) because captured local variables become stale after the first DB update
- Always catch `BusinessException` in AJAX event handlers — the service layer throws these for validation failures

### Prevention
When adding ANY jQuery plugin initialization (masks, tooltips, select2, etc.) to Wicket HTML templates:
1. NEVER rely solely on `$(document).ready()` — it won't fire for AJAX content
2. Use modal show events (`shown.bs.modal`) or targeted JS after AJAX replace
3. For non-modal AJAX updates, always use `target.appendJavaScript(...)` from the Java side
4. The `$('[data-mask]').each()` pattern is safe to call multiple times — jQuery Mask plugin replaces existing masks
