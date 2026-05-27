# Session Handoff — 2026-05-27

## Issues Worked On

### 1. dataMask not working in ClienteFisicoCreateModal / ClienteJuridicoCreateModal

**Root cause:** Wicket 7's managed jQuery reference (set via `setJQueryReference()` in WicketApplication) loaded jQuery a second time as a dependency of wicket-ajax.js, which re-executed `window.$` and wiped `$.fn.mask` registered by the mask plugin. Also, `UrlResourceReference` (CDN) and `JavaScriptResourceReference` (mask-init.js) had no Wicket dependency relationship — Wicket could reorder them.

**Fix applied:**
- Downloaded `jquery.mask.min.js` locally into `src/main/resources/.../util/js/`
- Added `JavaScriptUtils.getMaskLibraryReference()` / `getMaskInitReference()` — shared `JavaScriptResourceReference` singletons declaring proper Wicket dependency chain: jQuery → jquery.mask.min.js → mask-init.js
- Both modals now use these managed references instead of CDN + raw local ref
- Removed duplicate jQuery `<script>` from `BasePage.html` (Wicket's managed reference handles it)
- Added `reapplyMasksSafe(target)` in `attachRealTimeValidation.onUpdate` so masks survive AJAX blur validation
- Added `typeof m === 'string'` guard on mask value in all mask-apply code paths (plugin throws `d.charAt is not a function` on non-string values)

### 2. Descending sort on client table

**Change:** `AbstractClienteDataProvider.java:22` — `Sort.by("id").descending()` so newest records appear first.

**Status:** Open question — user may prefer ascending sort + navigate-to-new-record behavior instead.

## Unresolved

- Sort direction preference (descending vs ascending + page-jump)

## Files Changed (this session)

| File | Change |
|------|--------|
| `.../util/js/jquery.mask.min.js` | **New** — local copy of mask plugin |
| `.../util/JavaScriptUtils.java` | Added `getMaskLibraryReference()`, `getMaskInitReference()`; added string guard on all mask reapply methods |
| `.../util/js/mask-init.js` | Added string guard in `aplicarMascaras()` |
| `.../component/modal/ClienteFisicoCreateModal.java` | Uses managed refs; removed CPF workaround + unused imports |
| `.../component/modal/ClienteJuridicoCreateModal.java` | Uses managed refs; removed unused imports |
| `.../component/ValidationFeedback.java` | Added `reapplyMasksSafe` in `attachRealTimeValidation.onUpdate` |
| `.../page/base/BasePage.html` | Removed duplicate jQuery `<script>` tag |
| `.../provider/AbstractClienteDataProvider.java` | Changed sort to descending |
