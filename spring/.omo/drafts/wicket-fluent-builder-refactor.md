# Draft: Wicket FluentBuilder Refactor

## User Decisions
- **Execution style**: Single pass (not incremental)
- **Scope boundaries**: None — all Wicket pages/components are in scope
- **Feature branch**: Already available
- **Detach JS/CSS per responsibility**: Explicitly required
- **Follow SOLID principles**: Explicitly required

## Research Findings

### File Inventory
- **31 Java files** + **14 HTML files** in Wicket layer
- **0 Wicket-specific test files** — zero test coverage for Wicket UI

### Key SOLID Violations Found

1. **DRY Violation (Severe)**: `EnderecoCreateTablePanel` repeats identical 8-step field setup pattern ~10 times per file. Same pattern in `ClienteFisicoCreateModal`, `ClienteJuridicoCreateModal`, etc.
2. **Single Responsibility Violation**: `EnderecoListViewPanel` (379 lines) mixes UI construction, DTO mapping, business logic, file export/import, error handling, and template download.
3. **Inline Business Logic in UI**: `ClienteFisicoCreateModal` does date parsing (`LocalDate.parse`), DTO construction, service calls directly in panel.
4. **Inline JavaScript Scattered**: `target.appendJavaScript()` in ~10 files for lucide icons, input masks, modal open/close, debounce behavior.
5. **Duplicate Error Handling**: Same `try/catch` with `DataIntegrityViolationException` / `BusinessException` / generic `Exception` appears 5+ times across files.
6. **Inline JS in renderHead**: `ClientesFisicosTablePanel` embeds debounce JS directly in Java code.

### Complexity Ranking (by component add operations)
| File | Component Adds | Lines | Complexity |
|------|---------------|-------|----------|
| `EnderecoListViewPanel.java` | 25 | 379 | High |
| `EnderecoCreateTablePanel.java` | 29 | 186 | High |
| `ClienteFisicoCreateModal.java` | 23 | 200 | Medium |
| `ClienteJuridicoCreateModal.java` | 18 | ~180 | Medium |
| `ClientesFisicosTablePanel.java` | 10 | 218 | Medium |
| `ClientesJuridicosTablePanel.java` | 10 | ~220 | Medium |

### FluentBuilder Opportunities
- **FormFieldBuilder<T>**: Chain `.id()`, `.required()`, `.placeholder()`, `.validator()`, `.feedback()`, `.mask()`, `.build()`
- **ErrorHandler**: Centralized `try/catch` for service calls with toast feedback
- **DtoMapper**: Extract DTO construction from UI components
- **JavaScriptUtils**: Centralize common JS snippets (lucide, mask, modal)
- **AttributeModifierBuilder**: Chain multiple `AttributeModifier`s cleanly

## Open Questions
- Test strategy (TDD vs tests-after vs none) — CRITICAL: zero coverage currently
- Risk tolerance for behavior changes during refactor
- Whether to extract inline JS to external `.js` resource files vs centralized Java utilities
