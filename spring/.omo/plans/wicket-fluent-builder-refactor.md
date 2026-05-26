# Wicket FluentBuilder Refactor

## TL;DR

> **Quick Summary**: Refactor the entire Wicket UI layer (~31 Java + 14 HTML files) using FluentBuilder patterns to eliminate severe DRY violations, split SRP violations, and centralize scattered inline JavaScript and error handling. All `wicket:id` values and component hierarchies preserved to maintain HTML template compatibility.
>
> **Deliverables**:
> - `FormFieldBuilder<T>` + `AttributeModifierBuilder` fluent APIs
> - `JavaScriptUtils` + `ErrorHandler` centralized utilities
> - `DtoMapper` classes extracting DTO construction from UI components
> - Unified generic `ClientesTablePanel` replacing duplicated fisico/juridico panels
> - Split `EnderecoListViewPanel` into UI + service + file-handling concerns
> - WicketTester integration test suite (added after refactor)
> - Externalized JavaScript resource files (where appropriate)
>
> **Estimated Effort**: Large
> **Parallel Execution**: YES — 4 waves
> **Critical Path**: Task 1-2 (builders) → Task 7-19 (structural refactor) → Task 20-24 (behavioral) → Task 25-29 (tests) → F1-F4

---

## Context

### Original Request
Refactor `wicket/` with FluentBuilder to improve readability. Detach JavaScript, CSS, and other code per responsibility. Follow SOLID principles. Single-pass execution.

### Interview Summary
**Key Decisions**:
- **Execution**: Single pass (all files in one branch, not incremental over multiple PRs)
- **Scope**: All 31 Java + 14 HTML files in `wicket/` are in scope
- **Feature branch**: Already available
- **Tests**: Tests-after refactor (Option B) — no pre-existing Wicket test coverage

**Research Findings**:
- 31 Java files, 14 HTML files, **0 Wicket tests**
- Severe DRY: `EnderecoCreateTablePanel` repeats identical 8-step field setup ~10 times (186 lines)
- SRP violation: `EnderecoListViewPanel` 379 lines mixing UI, DTO mapping, file export/import, error handling
- Inline JS scattered: `appendJavaScript()` in ~13 files, 34 total calls
- Duplicate error handling: same `try/catch` pattern in 7+ places
- Business logic in UI: DTO construction, date parsing, CEP cleaning inline in modals
- `ClientesFisicosTablePanel` and `ClientesJuridicosTablePanel` are ~90% identical (~200 lines each)

### Metis Review
**Identified Gaps** (addressed in this plan):
- **Gap**: "Single pass for 31 files" is risky without tests. **Resolution**: Organize into 4 dependency-ordered waves, all within same branch. Add smoke-test infrastructure as Wave 1.
- **Gap**: FluentBuilder API not concretely defined. **Resolution**: Prototype on `EnderecoCreateTablePanel` first (Task 2), then propagate.
- **Gap**: `wicket:id` changes break HTML silently. **Resolution**: Hard guardrail — all `wicket:id` values MUST stay identical.
- **Gap**: `findParent()` chains break if hierarchy changes. **Resolution**: Hard guardrail — preserve component tree nesting.
- **Gap**: HTML templates have inline JS too (not just Java). **Resolution**: Include HTML template JS extraction in Wave 3.
- **Gap**: Some files should NOT be touched. **Resolution**: Explicit OUT list in Must NOT Have.

---

## Work Objectives

### Core Objective
Introduce FluentBuilder APIs for Wicket component construction, centralize cross-cutting concerns (JS, error handling, DTO mapping), eliminate duplication, and split SRP violations — all while preserving runtime behavior and HTML template compatibility.

### Concrete Deliverables
- `wicket/builder/FormFieldBuilder.java`
- `wicket/builder/AttributeModifierBuilder.java`
- `wicket/util/JavaScriptUtils.java`
- `wicket/util/ErrorHandler.java`
- `wicket/mapper/DtoMapper.java` + `ClienteFisicoDtoMapper.java` + `ClienteJuridicoDtoMapper.java` + `EnderecoDtoMapper.java`
- `wicket/component/table/ClientesTablePanel.java` (unified generic replacement)
- Refactored versions of all 19 major component/page files
- WicketTester test classes in `src/test/java/.../wicket/`
- External JS resource files in `src/main/resources/wicket/js/`

### Definition of Done
- [ ] `rtk gradlew compileJava` succeeds with zero errors
- [ ] `rtk gradlew test` passes (new WicketTester tests)
- [ ] All `wicket:id` values in HTML templates resolve to Java component IDs
- [ ] No `appendJavaScript("lucide.createIcons()")` scattered in individual components
- [ ] `ClientesFisicosTablePanel` + `ClientesJuridicosTablePanel` unified into single generic component
- [ ] `EnderecoListViewPanel` < 200 lines (was 379)

### Must Have
- FluentBuilder APIs for common Wicket component construction patterns
- Centralized `JavaScriptUtils` eliminating duplicate `appendJavaScript` calls
- Centralized `ErrorHandler` for service-call exception → toast mapping
- DTO construction extracted from all modal/page components into dedicated mapper classes
- Unified generic `ClientesTablePanel<T>` replacing duplicated table panels
- All `wicket:id` values preserved exactly (HTML template compatibility)
- All `findParent()` call chains preserved (component hierarchy compatibility)
- `@Serial` fields preserved on all Wicket components and inner classes
- `@SpringBean` injection patterns unchanged

### Must NOT Have (Guardrails)
- **MUST NOT** change any `wicket:id` string value in Java or HTML
- **MUST NOT** change `CompoundPropertyModel` binding or FormModel getter/setter signatures
- **MUST NOT** refactor `AbstractClienteDataProvider`, `AbstractClienteDataView`, `ValidationFeedback`, `ByteArrayResourceStream`, `WicketApplication`, `BasePage`
- **MUST NOT** touch FormModel classes (`ClienteFisicoCreateFormModel`, etc.) — they are fine for CPM binding
- **MUST NOT** change `onComponentTag()` tag-renaming behavior in `ClienteFisicoRowUpdateForm` / `ClienteJuridicoRowUpdateForm`
- **MUST NOT** introduce anonymous inner classes inside FluentBuilder chains (breaks Wicket serialization)
- **MUST NOT** change AJAX callback URLs or behavior response timing
- **MUST NOT** add new dependencies without verification

---

## Verification Strategy

> **ZERO HUMAN INTERVENTION** — ALL verification is agent-executed. No exceptions.

### Test Decision
- **Infrastructure exists**: NO (0 Wicket tests)
- **Automated tests**: Tests-after (added in Wave 4)
- **Framework**: JUnit 5 + Mockito + WicketTester (to be added)
- **If TDD**: N/A — tests added after implementation

### QA Policy
Every task MUST include agent-executed QA scenarios. Evidence saved to `.omo/evidence/task-{N}-{scenario-slug}.{ext}`.

- **Frontend/UI**: Use Playwright (playwright skill) — Navigate, interact, assert DOM, screenshot
- **TUI/CLI**: Use interactive_bash (tmux) — Run command, send keystrokes, validate output
- **API/Backend**: Use Bash (curl) — Send requests, assert status + response fields
- **Library/Module**: Use Bash (bun/node REPL) — Import, call functions, compare output

---

## Execution Strategy

### Parallel Execution Waves

```
Wave 1 (Foundation — can all start immediately):
├── Task 1: WicketTester dependency + base test class
├── Task 2: FormFieldBuilder API + prototype on EnderecoCreateTablePanel
├── Task 3: AttributeModifierBuilder + ComponentAttributeBuilder
├── Task 4: JavaScriptUtils centralized utility
├── Task 5: ErrorHandler centralized utility
└── Task 6: DtoMapper foundation (Endereco, ClienteFisico, ClienteJuridico)

Wave 2 (Structural — depends on Wave 1 builders):
├── Task 7: Refactor EnderecoCreateTablePanel with FormFieldBuilder
├── Task 8: Refactor ClienteFisicoCreateModal with builders + DtoMapper
├── Task 9: Refactor ClienteJuridicoCreateModal with builders + DtoMapper
├── Task 10: Refactor ClienteFisicoDetalhePage
├── Task 11: Refactor ClienteJuridicoDetalhePage
├── Task 12: Refactor ClienteFisicoRowUpdateForm with builders
├── Task 13: Refactor ClienteJuridicoRowUpdateForm with builders
├── Task 14: Refactor ClienteFisicoDataView with builders
├── Task 15: Refactor ClienteJuridicoDataView with builders
├── Task 16: Create unified generic ClientesTablePanel<T>
├── Task 17: Refactor ClientesFisicosTablePanel to use unified panel
├── Task 18: Refactor ClientesJuridicosTablePanel to use unified panel
└── Task 19: Split EnderecoListViewPanel SRP (UI vs service vs file handling)

Wave 3 (Behavioral — depends on Wave 2):
├── Task 20: Extract inline JS from Java components to JavaScriptUtils
├── Task 21: Extract inline JS from HTML templates to external resource files
├── Task 22: Extract error handling in all modals to ErrorHandler
├── Task 23: Extract DTO construction in all modals to DtoMappers
└── Task 24: Refactor ImportModal + ExportModal with builders

Wave 4 (Tests — depends on Waves 1-3):
├── Task 25: WicketTester tests for EnderecoCreateTablePanel
├── Task 26: WicketTester tests for CreateModals
├── Task 27: WicketTester tests for TablePanels
├── Task 28: WicketTester tests for DetalhePages
└── Task 29: WicketTester tests for EnderecoListViewPanel

Wave FINAL (After ALL tasks — 4 parallel reviews, then user okay):
├── Task F1: Plan compliance audit (oracle)
├── Task F2: Code quality review (unspecified-high)
├── Task F3: Real manual QA (unspecified-high)
└── Task F4: Scope fidelity check (deep)
-> Present results -> Get explicit user okay
```

### Dependency Matrix

| Task | Depends On | Blocks |
|------|-----------|--------|
| 1 | — | 25-29 |
| 2 | — | 7-19, 20-24 |
| 3 | — | 7-19, 20-24 |
| 4 | — | 20-21 |
| 5 | — | 22 |
| 6 | — | 8-9, 19, 23 |
| 7 | 2, 3 | 20, 25 |
| 8 | 2, 3, 6 | 22, 23, 26 |
| 9 | 2, 3, 6 | 22, 23, 26 |
| 10 | 2, 3 | 28 |
| 11 | 2, 3 | 28 |
| 12 | 2, 3 | — |
| 13 | 2, 3 | — |
| 14 | 2, 3 | — |
| 15 | 2, 3 | — |
| 16 | 2, 3 | 17, 18, 27 |
| 17 | 16 | 27 |
| 18 | 16 | 27 |
| 19 | 2, 3, 6 | 29 |
| 20 | 4, 7-19 | — |
| 21 | 4 | — |
| 22 | 5, 8-9 | — |
| 23 | 6, 8-9 | — |
| 24 | 2, 3 | — |
| 25 | 1, 7 | — |
| 26 | 1, 8-9 | — |
| 27 | 1, 16-18 | — |
| 28 | 1, 10-11 | — |
| 29 | 1, 19 | — |
| F1-F4 | 1-29 | — |

### Agent Dispatch Summary

- **Wave 1**: 6 tasks — `quick` (scaffolding + utilities)
- **Wave 2**: 13 tasks — `unspecified-high` (component refactoring)
- **Wave 3**: 5 tasks — `unspecified-high` (behavioral extraction)
- **Wave 4**: 5 tasks — `unspecified-high` (test writing)
- **Wave FINAL**: 4 tasks — F1=`oracle`, F2=`unspecified-high`, F3=`unspecified-high`, F4=`deep`

---

## TODOs

- [ ] 1. Add WicketTester dependency + base test class

  **What to do**:
  - Add `wicket-tester` to `build.gradle` (or verify it's already a transitive dependency through `wicket-spring`)
  - Create `src/test/java/com/desafio/estagio/wicket/WicketTestBase.java` extending `WicketTestCase` or using `WicketTester` directly
  - Configure `WicketTester` with the same `WicketApplication` class used in production
  - Add a minimal smoke test that renders `HomePage` to verify setup works

  **Must NOT do**:
  - Do NOT add non-Wicket dependencies
  - Do NOT change production application configuration

  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Skills**: `test-driven-development`
    - Needed to set up test infrastructure correctly

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1
  - **Blocks**: Tasks 25-29
  - **Blocked By**: None

  **References**:
  - `spring/estagio/build.gradle` — check existing test dependencies
  - `spring/estagio/src/main/java/com/desafio/estagio/wicket/application/WicketApplication.java` — application class to mount in tester
  - `spring/estagio/src/main/java/com/desafio/estagio/wicket/page/home/HomePage.java` — page to smoke-test

  **Acceptance Criteria**:
  - [ ] `rtk gradlew compileTestJava` succeeds
  - [ ] Smoke test renders HomePage without exceptions

  **QA Scenarios**:
  ```
  Scenario: WicketTester renders HomePage
    Tool: Bash
    Preconditions: Dependencies added, WicketTestBase created
    Steps:
      1. Run: rtk gradlew test --tests "com.desafio.estagio.wicket.*"
    Expected Result: BUILD SUCCESSFUL, at least 1 test passes
    Failure Indicators: WicketTester initialization errors, page mount errors
    Evidence: .omo/evidence/task-1-wicket-tester-smoke.txt
  ```

  **Commit**: YES
  - Message: `test(wicket): add WicketTester base class and smoke test`
  - Files: `build.gradle`, `src/test/java/com/desafio/estagio/wicket/WicketTestBase.java`, `src/test/java/com/desafio/estagio/wicket/HomePageSmokeTest.java`

---

- [ ] 2. FormFieldBuilder API + prototype on EnderecoCreateTablePanel

  **What to do**:
  - Create `wicket/builder/FormFieldBuilder.java` with fluent API: `.id(String)`, `.required()`, `.placeholder(String)`, `.validator(IValidator)`, `.attribute(String, String)`, `.feedbackLabel(String)`, `.realTimeValidation()`, `.build()`
  - The builder returns a `FormFieldBundle` containing the field component + feedback label
  - Prototype by refactoring `EnderecoCreateTablePanel.populateItem()` to use the builder
  - Reduce the ~80 lines of repetitive field setup to ~20 lines using builder chains

  **Must NOT do**:
  - Do NOT use anonymous inner classes inside the builder (breaks Wicket serialization)
  - Do NOT change any `wicket:id` values
  - Do NOT change validation behavior — same validators, same error messages

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
  - **Skills**: None needed

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1
  - **Blocks**: Tasks 7-19, 20-24
  - **Blocked By**: None

  **References**:
  - `wicket/component/shared/EnderecoCreateTablePanel.java:43-151` — the repetitive pattern to replace
  - `wicket/component/ValidationFeedback.java:21-57` — feedback label + real-time validation pattern

  **Acceptance Criteria**:
  - [ ] `FormFieldBuilder.java` compiles
  - [ ] `EnderecoCreateTablePanel` compiles and is functionally equivalent
  - [ ] `rtk gradlew compileJava` succeeds

  **QA Scenarios**:
  ```
  Scenario: Builder produces equivalent field setup
    Tool: Bash
    Preconditions: FormFieldBuilder created, EnderecoCreateTablePanel refactored
    Steps:
      1. Run: rtk gradlew compileJava
      2. Grep EnderecoCreateTablePanel for "new TextField" — should show fewer occurrences
    Expected Result: Compilation succeeds; TextField count reduced from ~10 to 1 (via builder)
    Failure Indicators: Compilation errors, missing wicket:id
    Evidence: .omo/evidence/task-2-builder-prototype.txt
  ```

  **Commit**: YES
  - Message: `feat(wicket): add FormFieldBuilder and prototype on EnderecoCreateTablePanel`
  - Files: `wicket/builder/FormFieldBuilder.java`, `wicket/component/shared/EnderecoCreateTablePanel.java`

---

- [ ] 3. AttributeModifierBuilder + ComponentAttributeBuilder

  **What to do**:
  - Create `wicket/builder/AttributeModifierBuilder.java` for chaining multiple `AttributeModifier`s
  - Create `wicket/builder/ComponentAttributeBuilder.java` for fluent `.add()`, `.setVisible()`, `.setEnabled()`, `.setOutputMarkupId()` on any component
  - Support common patterns: `.cssClass(String)`, `.placeholder(String)`, `.dataAttr(String, String)`, `.title(String)`, `.disabled(boolean)`
  - These builders return the configured component (or a wrapper), not anonymous inner classes

  **Must NOT do**:
  - Do NOT break existing `AttributeModifier` behavior
  - Do NOT introduce serialization-unsafe patterns

  **Recommended Agent Profile**:
  - **Category**: `quick`
  - **Skills**: None needed

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1
  - **Blocks**: Tasks 7-19, 20-24
  - **Blocked By**: None

  **References**:
  - `wicket/component/shared/EnderecoListViewPanel.java:133-143` — AttributeModifier chain example
  - `wicket/component/modal/ClienteFisicoCreateModal.java:62-63` — placeholder + data-mask pattern

  **Acceptance Criteria**:
  - [ ] Both builder classes compile
  - [ ] Can replace at least 3 existing `new AttributeModifier(...)` chains

  **QA Scenarios**:
  ```
  Scenario: AttributeModifierBuilder replaces existing chains
    Tool: Bash
    Preconditions: Builders created
    Steps:
      1. Grep for "new AttributeModifier" across wicket/ — record count
      2. Apply builders to 3+ files
      3. Grep again — count should decrease
    Expected Result: AttributeModifier count decreases; compilation succeeds
    Evidence: .omo/evidence/task-3-attribute-builder.txt
  ```

  **Commit**: YES
  - Message: `feat(wicket): add AttributeModifierBuilder and ComponentAttributeBuilder`
  - Files: `wicket/builder/AttributeModifierBuilder.java`, `wicket/builder/ComponentAttributeBuilder.java`

---

- [ ] 4. JavaScriptUtils centralized utility

  **What to do**:
  - Create `wicket/util/JavaScriptUtils.java` with static methods for common JS snippets
  - Replace all scattered `appendJavaScript` calls in Java files with these utilities

  **Must NOT do**:
  - Do NOT change the timing of JS execution
  - Do NOT remove JS that is conditionally executed

  **Recommended Agent Profile**:
  - **Category**: `quick`

  **Parallelization**:
  - **Can Run In Parallel**: YES — Wave 1
  - **Blocks**: Tasks 20-21
  - **Blocked By**: None

  **Acceptance Criteria**:
  - [ ] `JavaScriptUtils.java` compiles
  - [ ] `appendJavaScript` count in `wicket/` reduced by at least 50%

  **QA Scenarios**:
  ```
  Scenario: JS centralized and compilation succeeds
    Tool: Bash
    Steps:
      1. Grep count of appendJavaScript before and after
      2. Run: rtk gradlew compileJava
    Expected Result: Count reduced; compilation succeeds
    Evidence: .omo/evidence/task-4-js-utils.txt
  ```

  **Commit**: NO (groups with Task 20)

---

- [ ] 5. ErrorHandler centralized utility

  **What to do**:
  - Create `wicket/util/ErrorHandler.java` wrapping repeated try/catch patterns
  - Uses existing `ValidationFeedback.showToast`

  **Must NOT do**:
  - Do NOT change error messages
  - Do NOT swallow exceptions silently

  **Recommended Agent Profile**:
  - **Category**: `quick`

  **Parallelization**:
  - **Can Run In Parallel**: YES — Wave 1
  - **Blocks**: Task 22
  - **Blocked By**: None

  **Acceptance Criteria**:
  - [ ] `ErrorHandler.java` compiles
  - [ ] At least 3 call sites migrated

  **QA Scenarios**:
  ```
  Scenario: ErrorHandler replaces duplicate patterns
    Tool: Bash
    Steps:
      1. Grep for "DataIntegrityViolationException" in wicket/ — count before and after
      2. Run: rtk gradlew compileJava
    Expected Result: Count reduced; compilation succeeds
    Evidence: .omo/evidence/task-5-error-handler.txt
  ```

  **Commit**: NO (groups with Task 22)

---

- [ ] 6. DtoMapper foundation

  **What to do**:
  - Create `EnderecoDtoMapper`, `ClienteFisicoDtoMapper`, `ClienteJuridicoDtoMapper`
  - Each handles cleaning (CEP, telefone, RG) and date parsing
  - Stateless utility classes

  **Must NOT do**:
  - Do NOT change DTO field mapping logic

  **Recommended Agent Profile**:
  - **Category**: `quick`

  **Parallelization**:
  - **Can Run In Parallel**: YES — Wave 1
  - **Blocks**: Tasks 8-9, 19, 23
  - **Blocked By**: None

  **Acceptance Criteria**:
  - [ ] All 3 mapper classes compile
  - [ ] Inline DTO construction can be replaced by mapper calls

  **QA Scenarios**:
  ```
  Scenario: Mapper produces equivalent DTOs
    Tool: Bash
    Steps:
      1. Create test form model with known values
      2. Call mapper; compare output fields
    Expected Result: All fields match; CEP/telefone cleaned correctly
    Evidence: .omo/evidence/task-6-dto-mapper.txt
  ```

  **Commit**: NO (groups with Tasks 8-9)

---

- [ ] 7. Refactor EnderecoCreateTablePanel with FormFieldBuilder

  **What to do**:
  - Apply the `FormFieldBuilder` from Task 2 to fully refactor `EnderecoCreateTablePanel`
  - Replace all ~10 repetitive field constructions with builder chains
  - Keep `ValidationStyleBehavior` intact (it's already well-factored)

  **Must NOT do**:
  - Do NOT change any `wicket:id` values
  - Do NOT change validation rules or error messages

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`

  **Parallelization**:
  - **Can Run In Parallel**: YES — Wave 2
  - **Blocks**: Task 20, 25
  - **Blocked By**: Tasks 2, 3

  **Acceptance Criteria**:
  - [ ] File compiles
  - [ ] `new TextField` count reduced from ~10 to 1 (via builder)
  - [ ] `rtk gradlew compileJava` succeeds

  **QA Scenarios**:
  ```
  Scenario: EnderecoCreateTablePanel compiles with builder
    Tool: Bash
    Steps:
      1. Run: rtk gradlew compileJava
      2. Grep EnderecoCreateTablePanel for "new TextField" — count = 1
    Expected Result: Compilation succeeds; count = 1
    Evidence: .omo/evidence/task-7-endereco-table.txt
  ```

  **Commit**: YES
  - Message: `refactor(wicket): EnderecoCreateTablePanel with FormFieldBuilder`

---

- [ ] 8. Refactor ClienteFisicoCreateModal with builders + DtoMapper

  **What to do**:
  - Replace all inline `TextField` + `AttributeModifier` + `ValidationFeedback` chains with `FormFieldBuilder`
  - Replace inline DTO construction with `ClienteFisicoDtoMapper`
  - Replace inline error handling with `ErrorHandler`
  - Keep `onSubmit` logic but delegate DTO building and error handling

  **Must NOT do**:
  - Do NOT change the date parsing logic (dd/MM/yyyy → LocalDate)
  - Do NOT change CPF/RG validation behavior

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`

  **Parallelization**:
  - **Can Run In Parallel**: YES — Wave 2
  - **Blocks**: Tasks 22, 23, 26
  - **Blocked By**: Tasks 2, 3, 6

  **Acceptance Criteria**:
  - [ ] File compiles
  - [ ] Line count reduced by at least 30%
  - [ ] `rtk gradlew compileJava` succeeds

  **QA Scenarios**:
  ```
  Scenario: Modal compiles with builders and mappers
    Tool: Bash
    Steps:
      1. Run: rtk gradlew compileJava
      2. wc -l ClienteFisicoCreateModal.java — compare before/after
    Expected Result: Compilation succeeds; line count reduced by ≥30%
    Evidence: .omo/evidence/task-8-fisico-modal.txt
  ```

  **Commit**: YES
  - Message: `refactor(wicket): ClienteFisicoCreateModal with builders and DtoMapper`

---

- [ ] 9. Refactor ClienteJuridicoCreateModal with builders + DtoMapper

  **What to do**:
  - Same pattern as Task 8 but for `ClienteJuridicoCreateModal`
  - Use `ClienteJuridicoDtoMapper` from Task 6
  - Use `FormFieldBuilder` + `AttributeModifierBuilder` from Tasks 2-3

  **Must NOT do**:
  - Do NOT change CNPJ validation behavior

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`

  **Parallelization**:
  - **Can Run In Parallel**: YES — Wave 2
  - **Blocks**: Tasks 22, 23, 26
  - **Blocked By**: Tasks 2, 3, 6

  **Acceptance Criteria**:
  - [ ] File compiles
  - [ ] Line count reduced by at least 30%
  - [ ] `rtk gradlew compileJava` succeeds

  **QA Scenarios**:
  ```
  Scenario: Modal compiles with builders and mappers
    Tool: Bash
    Steps:
      1. Run: rtk gradlew compileJava
      2. wc -l ClienteJuridicoCreateModal.java — compare before/after
    Expected Result: Compilation succeeds; line count reduced by ≥30%
    Evidence: .omo/evidence/task-9-juridico-modal.txt
  ```

  **Commit**: YES
  - Message: `refactor(wicket): ClienteJuridicoCreateModal with builders and DtoMapper`

---

- [ ] 10. Refactor ClienteFisicoDetalhePage

  **What to do**:
  - Refactor `ClienteFisicoDetalhePage` using `ComponentAttributeBuilder` for label formatting and visibility
  - Keep `EnderecoListViewPanel` usage intact (refactored separately in Task 19)

  **Must NOT do**:
  - Do NOT change formatter behavior (CPFFormatter, RGFormatter)

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`

  **Parallelization**:
  - **Can Run In Parallel**: YES — Wave 2
  - **Blocks**: Task 28
  - **Blocked By**: Tasks 2, 3

  **Acceptance Criteria**:
  - [ ] File compiles
  - [ ] `rtk gradlew compileJava` succeeds

  **QA Scenarios**:
  ```
  Scenario: DetalhePage compiles after refactor
    Tool: Bash
    Steps:
      1. Run: rtk gradlew compileJava
    Expected Result: Compilation succeeds
    Evidence: .omo/evidence/task-10-fisico-detalhe.txt
  ```

  **Commit**: YES
  - Message: `refactor(wicket): ClienteFisicoDetalhePage with ComponentAttributeBuilder`

---

- [ ] 11. Refactor ClienteJuridicoDetalhePage

  **What to do**:
  - Same pattern as Task 10 for `ClienteJuridicoDetalhePage`

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`

  **Parallelization**:
  - **Can Run In Parallel**: YES — Wave 2
  - **Blocks**: Task 28
  - **Blocked By**: Tasks 2, 3

  **Acceptance Criteria**:
  - [ ] File compiles
  - [ ] `rtk gradlew compileJava` succeeds

  **QA Scenarios**:
  ```
  Scenario: DetalhePage compiles after refactor
    Tool: Bash
    Steps:
      1. Run: rtk gradlew compileJava
    Expected Result: Compilation succeeds
    Evidence: .omo/evidence/task-11-juridico-detalhe.txt
  ```

  **Commit**: YES
  - Message: `refactor(wicket): ClienteJuridicoDetalhePage with ComponentAttributeBuilder`

---

- [ ] 12. Refactor ClienteFisicoRowUpdateForm with builders

  **What to do**:
  - Refactor `ClienteFisicoRowUpdateForm` using `FormFieldBuilder`
  - Preserve `onComponentTag` behavior exactly (Metis guardrail)

  **Must NOT do**:
  - Do NOT change `onComponentTag` tag-renaming behavior

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`

  **Parallelization**:
  - **Can Run In Parallel**: YES — Wave 2
  - **Blocked By**: Tasks 2, 3

  **Acceptance Criteria**:
  - [ ] File compiles
  - [ ] `rtk gradlew compileJava` succeeds

  **QA Scenarios**:
  ```
  Scenario: RowUpdateForm compiles after refactor
    Tool: Bash
    Steps:
      1. Run: rtk gradlew compileJava
    Expected Result: Compilation succeeds
    Evidence: .omo/evidence/task-12-fisico-row.txt
  ```

  **Commit**: YES
  - Message: `refactor(wicket): ClienteFisicoRowUpdateForm with builders`

---

- [ ] 13. Refactor ClienteJuridicoRowUpdateForm with builders

  **What to do**:
  - Same pattern as Task 12 for `ClienteJuridicoRowUpdateForm`
  - Preserve `onComponentTag` behavior exactly

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`

  **Parallelization**:
  - **Can Run In Parallel**: YES — Wave 2
  - **Blocked By**: Tasks 2, 3

  **Acceptance Criteria**:
  - [ ] File compiles
  - [ ] `rtk gradlew compileJava` succeeds

  **QA Scenarios**:
  ```
  Scenario: RowUpdateForm compiles after refactor
    Tool: Bash
    Steps:
      1. Run: rtk gradlew compileJava
    Expected Result: Compilation succeeds
    Evidence: .omo/evidence/task-13-juridico-row.txt
  ```

  **Commit**: YES
  - Message: `refactor(wicket): ClienteJuridicoRowUpdateForm with builders`

---

- [ ] 14. Refactor ClienteFisicoDataView with builders

  **What to do**:
  - Refactor `ClienteFisicoDataView` using `ComponentAttributeBuilder` for row styling and action buttons

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`

  **Parallelization**:
  - **Can Run In Parallel**: YES — Wave 2
  - **Blocked By**: Tasks 2, 3

  **Acceptance Criteria**:
  - [ ] File compiles
  - [ ] `rtk gradlew compileJava` succeeds

  **QA Scenarios**:
  ```
  Scenario: DataView compiles after refactor
    Tool: Bash
    Steps:
      1. Run: rtk gradlew compileJava
    Expected Result: Compilation succeeds
    Evidence: .omo/evidence/task-14-fisico-dataview.txt
  ```

  **Commit**: YES
  - Message: `refactor(wicket): ClienteFisicoDataView with builders`

---

- [ ] 15. Refactor ClienteJuridicoDataView with builders

  **What to do**:
  - Same pattern as Task 14 for `ClienteJuridicoDataView`

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`

  **Parallelization**:
  - **Can Run In Parallel**: YES — Wave 2
  - **Blocked By**: Tasks 2, 3

  **Acceptance Criteria**:
  - [ ] File compiles
  - [ ] `rtk gradlew compileJava` succeeds

  **QA Scenarios**:
  ```
  Scenario: DataView compiles after refactor
    Tool: Bash
    Steps:
      1. Run: rtk gradlew compileJava
    Expected Result: Compilation succeeds
    Evidence: .omo/evidence/task-15-juridico-dataview.txt
  ```

  **Commit**: YES
  - Message: `refactor(wicket): ClienteJuridicoDataView with builders`

---

- [ ] 16. Create unified generic ClientesTablePanel<T>

  **What to do**:
  - Analyze `ClientesFisicosTablePanel` and `ClientesJuridicosTablePanel` — they are ~90% identical
  - Design a generic `ClientesTablePanel<T extends AbstractClienteListResponse>` parameterized by:
    - `DataProvider<T>` type
    - `DataView<T>` factory
    - Modal component types
    - Export/import file names and methods
  - Move shared logic (search form with debounce JS, pagination, export/import modals, create modal wiring) into the generic base
  - Keep type-specific `wicket:id` values in HTML templates (use `<wicket:panel>` with conditional markup if needed)

  **Must NOT do**:
  - Do NOT change `wicket:id` values in existing HTML templates
  - Do NOT break `findParent(ClientesFisicosTablePanel.class).refreshTable(target)` calls — preserve the concrete type in the generic parameter or add a bridge method

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`

  **Parallelization**:
  - **Can Run In Parallel**: YES — Wave 2
  - **Blocks**: Tasks 17, 18, 27
  - **Blocked By**: Tasks 2, 3

  **Acceptance Criteria**:
  - [ ] `ClientesTablePanel.java` compiles
  - [ ] `rtk gradlew compileJava` succeeds

  **QA Scenarios**:
  ```
  Scenario: Generic panel compiles
    Tool: Bash
    Steps:
      1. Run: rtk gradlew compileJava
    Expected Result: Compilation succeeds
    Evidence: .omo/evidence/task-16-generic-table.txt
  ```

  **Commit**: YES
  - Message: `refactor(wicket): add unified generic ClientesTablePanel<T>`

---

- [ ] 17. Refactor ClientesFisicosTablePanel to use unified panel

  **What to do**:
  - Replace `ClientesFisicosTablePanel extends DevUtilsPanel` with `extends ClientesTablePanel<ClienteFisicoListResponse>`
  - Remove duplicated search form, pagination, export/import modal, create modal wiring
  - Keep only type-specific overrides (e.g., `getPdfData()`, `getXlsxData()`, `getTemplateData()`)

  **Must NOT do**:
  - Do NOT change `wicket:id` values
  - Do NOT break `findParent(ClientesFisicosTablePanel.class)` calls

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`

  **Parallelization**:
  - **Can Run In Parallel**: YES — Wave 2
  - **Blocks**: Task 27
  - **Blocked By**: Task 16

  **Acceptance Criteria**:
  - [ ] File compiles
  - [ ] Line count reduced by at least 50%
  - [ ] `rtk gradlew compileJava` succeeds

  **QA Scenarios**:
  ```
  Scenario: FisicosTablePanel uses generic base
    Tool: Bash
    Steps:
      1. Run: rtk gradlew compileJava
      2. wc -l ClientesFisicosTablePanel.java — compare before/after
    Expected Result: Compilation succeeds; line count reduced by ≥50%
    Evidence: .omo/evidence/task-17-fisico-table.txt
  ```

  **Commit**: YES
  - Message: `refactor(wicket): ClientesFisicosTablePanel extends generic panel`

---

- [ ] 18. Refactor ClientesJuridicosTablePanel to use unified panel

  **What to do**:
  - Same pattern as Task 17 for `ClientesJuridicosTablePanel`

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`

  **Parallelization**:
  - **Can Run In Parallel**: YES — Wave 2
  - **Blocks**: Task 27
  - **Blocked By**: Task 16

  **Acceptance Criteria**:
  - [ ] File compiles
  - [ ] Line count reduced by at least 50%
  - [ ] `rtk gradlew compileJava` succeeds

  **QA Scenarios**:
  ```
  Scenario: JuridicosTablePanel uses generic base
    Tool: Bash
    Steps:
      1. Run: rtk gradlew compileJava
      2. wc -l ClientesJuridicosTablePanel.java — compare before/after
    Expected Result: Compilation succeeds; line count reduced by ≥50%
    Evidence: .omo/evidence/task-18-juridico-table.txt
  ```

  **Commit**: YES
  - Message: `refactor(wicket): ClientesJuridicosTablePanel extends generic panel`

---

- [ ] 19. Split EnderecoListViewPanel SRP

  **What to do**:
  - `EnderecoListViewPanel` (379 lines) violates SRP by mixing:
    1. UI construction (modal form, table, buttons)
    2. Business logic (create/update/delete/setAsPrincipal enderecos)
    3. File handling (export PDF/XLSX, import XLSX, template download)
  - Split into:
    - `EnderecoListViewPanel` — UI only (< 150 lines)
    - `EnderecoServicePanel` — business logic delegation (create/update/delete/principal)
    - `EnderecoFilePanel` — export/import/template download
  - Or alternatively: extract `EnderecoFilePanel` and `EnderecoActionPanel` as separate components
  - Use `FormFieldBuilder` for the modal form fields
  - Use `EnderecoDtoMapper` for DTO construction
  - Use `ErrorHandler` for exception handling

  **Must NOT do**:
  - Do NOT change `wicket:id` values in the HTML template
  - Do NOT change service method signatures

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`

  **Parallelization**:
  - **Can Run In Parallel**: YES — Wave 2
  - **Blocks**: Task 29
  - **Blocked By**: Tasks 2, 3, 6

  **Acceptance Criteria**:
  - [ ] `EnderecoListViewPanel.java` < 200 lines (was 379)
  - [ ] New extracted classes compile
  - [ ] `rtk gradlew compileJava` succeeds

  **QA Scenarios**:
  ```
  Scenario: EnderecoListViewPanel split and compiles
    Tool: Bash
    Steps:
      1. Run: rtk gradlew compileJava
      2. wc -l EnderecoListViewPanel.java — verify < 200 lines
    Expected Result: Compilation succeeds; line count < 200
    Evidence: .omo/evidence/task-19-endereco-srp.txt
  ```

  **Commit**: YES
  - Message: `refactor(wicket): split EnderecoListViewPanel SRP`

---

- [ ] 20. Extract inline JS from Java components to JavaScriptUtils

  **What to do**:
  - After Tasks 7-19 have refactored all components, sweep through all `wicket/` Java files
  - Replace remaining inline `appendJavaScript` calls with `JavaScriptUtils` methods
  - Verify no component has `appendJavaScript` for common patterns (lucide, masks, modals)

  **Must NOT do**:
  - Do NOT touch JS that is unique to one component and not a common pattern

  **Recommended Agent Profile**:
  - **Category**: `quick`

  **Parallelization**:
  - **Can Run In Parallel**: YES — Wave 3
  - **Blocked By**: Tasks 4, 7-19

  **Acceptance Criteria**:
  - [ ] `appendJavaScript` count in `wicket/` Java files < 5 (was 34)
  - [ ] `rtk gradlew compileJava` succeeds

  **QA Scenarios**:
  ```
  Scenario: JS extraction complete
    Tool: Bash
    Steps:
      1. Grep -r "appendJavaScript" wicket/ --include="*.java" | wc -l
    Expected Result: Count < 5
    Evidence: .omo/evidence/task-20-js-extract.txt
  ```

  **Commit**: YES
  - Message: `refactor(wicket): extract inline JS to JavaScriptUtils`

---

- [ ] 21. Extract inline JS from HTML templates to external resource files

  **What to do**:
  - Identify inline `<script>` blocks in HTML templates:
    - `EnderecoCreateTablePanel.html` (ViaCEP lookup)
    - `EnderecoListViewPanel.html` (modal show/hide)
    - `ClienteFisicoCreateModal.html` / `ClienteJuridicoCreateModal.html` (mask re-init)
    - `BasePage.html` (page-level scripts)
  - Extract shared scripts to `src/main/resources/wicket/js/*.js`
  - Load them via `JavaScriptResourceReference` in `BasePage` or relevant components
  - Keep template-specific inline JS if it's tightly coupled to wicket:id values

  **Must NOT do**:
  - Do NOT change JS functionality
  - Do NOT break existing `wicket:panel` markup

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`

  **Parallelization**:
  - **Can Run In Parallel**: YES — Wave 3
  - **Blocked By**: Task 4

  **Acceptance Criteria**:
  - [ ] External JS files created in `resources/wicket/js/`
  - [ ] HTML templates reference external files or have reduced inline JS
  - [ ] `rtk gradlew compileJava` succeeds
  - [ ] `rtk gradlew buildWar` succeeds (resources packaged)

  **QA Scenarios**:
  ```
  Scenario: External JS files packaged in WAR
    Tool: Bash
    Steps:
      1. Run: rtk gradlew buildWar
      2. Unzip WAR; verify wicket/js/ exists
    Expected Result: JS files present in WAR
    Evidence: .omo/evidence/task-21-external-js.txt
  ```

  **Commit**: YES
  - Message: `refactor(wicket): extract inline HTML JS to external resource files`

---

- [ ] 22. Extract error handling in all modals to ErrorHandler

  **What to do**:
  - After Tasks 8-9 have refactored the modals, sweep through all modal and page files
  - Replace remaining inline `try/catch` blocks with `ErrorHandler.handleServiceCall()` or `ErrorHandler.handleDelete()`
  - Verify error messages remain identical

  **Must NOT do**:
  - Do NOT change error messages

  **Recommended Agent Profile**:
  - **Category**: `quick`

  **Parallelization**:
  - **Can Run In Parallel**: YES — Wave 3
  - **Blocked By**: Tasks 5, 8-9

  **Acceptance Criteria**:
  - [ ] `DataIntegrityViolationException` count in `wicket/` < 2 (was 7+)
  - [ ] `rtk gradlew compileJava` succeeds

  **QA Scenarios**:
  ```
  Scenario: Error handling centralized
    Tool: Bash
    Steps:
      1. Grep -r "DataIntegrityViolationException" wicket/ --include="*.java" | wc -l
    Expected Result: Count < 2
    Evidence: .omo/evidence/task-22-error-extract.txt
  ```

  **Commit**: YES
  - Message: `refactor(wicket): centralize error handling with ErrorHandler`

---

- [ ] 23. Extract DTO construction in all modals to DtoMappers

  **What to do**:
  - After Tasks 8-9, sweep through all components with inline DTO construction
  - Replace with calls to `EnderecoDtoMapper`, `ClienteFisicoDtoMapper`, `ClienteJuridicoDtoMapper`
  - Verify field mapping is identical

  **Must NOT do**:
  - Do NOT change DTO field mapping

  **Recommended Agent Profile**:
  - **Category**: `quick`

  **Parallelization**:
  - **Can Run In Parallel**: YES — Wave 3
  - **Blocked By**: Tasks 6, 8-9

  **Acceptance Criteria**:
  - [ ] No inline `new EnderecoCreateRequest(...)` or `new ClienteFisicoCreateRequest(...)` in UI components
  - [ ] `rtk gradlew compileJava` succeeds

  **QA Scenarios**:
  ```
  Scenario: DTO construction centralized
    Tool: Bash
    Steps:
      1. Grep -r "new EnderecoCreateRequest\|new ClienteFisicoCreateRequest\|new ClienteJuridicoCreateRequest" wicket/ --include="*.java"
    Expected Result: No matches in UI components (only in mappers)
    Evidence: .omo/evidence/task-23-dto-extract.txt
  ```

  **Commit**: YES
  - Message: `refactor(wicket): centralize DTO construction in mappers`

---

- [ ] 24. Refactor ImportModal + ExportModal with builders

  **What to do**:
  - Refactor `ImportModal` and `ExportModal` using `ComponentAttributeBuilder` and `FormFieldBuilder`
  - Extract any remaining inline JS to `JavaScriptUtils`

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`

  **Parallelization**:
  - **Can Run In Parallel**: YES — Wave 3
  - **Blocked By**: Tasks 2, 3

  **Acceptance Criteria**:
  - [ ] Files compile
  - [ ] `rtk gradlew compileJava` succeeds

  **QA Scenarios**:
  ```
  Scenario: Modals compile with builders
    Tool: Bash
    Steps:
      1. Run: rtk gradlew compileJava
    Expected Result: Compilation succeeds
    Evidence: .omo/evidence/task-24-import-export.txt
  ```

  **Commit**: YES
  - Message: `refactor(wicket): ImportModal and ExportModal with builders`

---

- [ ] 25. WicketTester tests for EnderecoCreateTablePanel

  **What to do**:
  - Create `EnderecoCreateTablePanelTest.java` using `WicketTestBase`
  - Test: render panel, verify all `wicket:id` components exist
  - Test: add endereco row, verify new row renders
  - Test: remove endereco row, verify row removed

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
  - **Skills**: `test-driven-development`

  **Parallelization**:
  - **Can Run In Parallel**: YES — Wave 4
  - **Blocked By**: Tasks 1, 7

  **Acceptance Criteria**:
  - [ ] Test class compiles
  - [ ] `rtk gradlew test --tests "*EnderecoCreateTablePanelTest*"` passes

  **QA Scenarios**:
  ```
  Scenario: Panel tests pass
    Tool: Bash
    Steps:
      1. Run: rtk gradlew test --tests "*EnderecoCreateTablePanelTest*"
    Expected Result: BUILD SUCCESSFUL, all tests pass
    Evidence: .omo/evidence/task-25-panel-test.txt
  ```

  **Commit**: YES
  - Message: `test(wicket): add EnderecoCreateTablePanelTest`

---

- [ ] 26. WicketTester tests for CreateModals

  **What to do**:
  - Create `ClienteFisicoCreateModalTest.java` and `ClienteJuridicoCreateModalTest.java`
  - Test: render modal, verify form fields exist
  - Test: submit with valid data (mock service)
  - Test: submit with invalid data, verify error feedback

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
  - **Skills**: `test-driven-development`

  **Parallelization**:
  - **Can Run In Parallel**: YES — Wave 4
  - **Blocked By**: Tasks 1, 8-9

  **Acceptance Criteria**:
  - [ ] Both test classes compile
  - [ ] `rtk gradlew test --tests "*CreateModalTest*"` passes

  **QA Scenarios**:
  ```
  Scenario: Modal tests pass
    Tool: Bash
    Steps:
      1. Run: rtk gradlew test --tests "*CreateModalTest*"
    Expected Result: BUILD SUCCESSFUL, all tests pass
    Evidence: .omo/evidence/task-26-modal-test.txt
  ```

  **Commit**: YES
  - Message: `test(wicket): add CreateModal tests`

---

- [ ] 27. WicketTester tests for TablePanels

  **What to do**:
  - Create `ClientesFisicosTablePanelTest.java` and `ClientesJuridicosTablePanelTest.java`
  - Test: render panel, verify data view exists
  - Test: search form, verify AJAX behavior (if WicketTester supports it)
  - Test: pagination navigator renders

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
  - **Skills**: `test-driven-development`

  **Parallelization**:
  - **Can Run In Parallel**: YES — Wave 4
  - **Blocked By**: Tasks 1, 16-18

  **Acceptance Criteria**:
  - [ ] Both test classes compile
  - [ ] `rtk gradlew test --tests "*TablePanelTest*"` passes

  **QA Scenarios**:
  ```
  Scenario: TablePanel tests pass
    Tool: Bash
    Steps:
      1. Run: rtk gradlew test --tests "*TablePanelTest*"
    Expected Result: BUILD SUCCESSFUL, all tests pass
    Evidence: .omo/evidence/task-27-table-test.txt
  ```

  **Commit**: YES
  - Message: `test(wicket): add TablePanel tests`

---

- [ ] 28. WicketTester tests for DetalhePages

  **What to do**:
  - Create `ClienteFisicoDetalhePageTest.java` and `ClienteJuridicoDetalhePageTest.java`
  - Test: render page with parameter, verify labels populated
  - Test: verify `EnderecoListViewPanel` rendered as child

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
  - **Skills**: `test-driven-development`

  **Parallelization**:
  - **Can Run In Parallel**: YES — Wave 4
  - **Blocked By**: Tasks 1, 10-11

  **Acceptance Criteria**:
  - [ ] Both test classes compile
  - [ ] `rtk gradlew test --tests "*DetalhePageTest*"` passes

  **QA Scenarios**:
  ```
  Scenario: DetalhePage tests pass
    Tool: Bash
    Steps:
      1. Run: rtk gradlew test --tests "*DetalhePageTest*"
    Expected Result: BUILD SUCCESSFUL, all tests pass
    Evidence: .omo/evidence/task-28-detalhe-test.txt
  ```

  **Commit**: YES
  - Message: `test(wicket): add DetalhePage tests`

---

- [ ] 29. WicketTester tests for EnderecoListViewPanel

  **What to do**:
  - Create `EnderecoListViewPanelTest.java`
  - Test: render panel, verify endereco rows exist
  - Test: verify modal form renders
  - Test: verify export/import links exist

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
  - **Skills**: `test-driven-development`

  **Parallelization**:
  - **Can Run In Parallel**: YES — Wave 4
  - **Blocked By**: Tasks 1, 19

  **Acceptance Criteria**:
  - [ ] Test class compiles
  - [ ] `rtk gradlew test --tests "*EnderecoListViewPanelTest*"` passes

  **QA Scenarios**:
  ```
  Scenario: EnderecoListViewPanel tests pass
    Tool: Bash
    Steps:
      1. Run: rtk gradlew test --tests "*EnderecoListViewPanelTest*"
    Expected Result: BUILD SUCCESSFUL, all tests pass
    Evidence: .omo/evidence/task-29-endereco-test.txt
  ```

  **Commit**: YES
  - Message: `test(wicket): add EnderecoListViewPanelTest`

---

## Final Verification Wave

> 4 review agents run in PARALLEL. ALL must APPROVE. Present consolidated results to user and get explicit "okay" before completing.

- [ ] F1. **Plan Compliance Audit** — `oracle`
  Read the plan end-to-end. For each "Must Have": verify implementation exists (read file, curl endpoint, run command). For each "Must NOT Have": search codebase for forbidden patterns — reject with file:line if found. Check evidence files exist in `.omo/evidence/`. Compare deliverables against plan.
  Output: `Must Have [N/N] | Must NOT Have [N/N] | Tasks [N/N] | VERDICT: APPROVE/REJECT`

- [ ] F2. **Code Quality Review** — `unspecified-high`
  Run `rtk gradlew compileJava` + `rtk gradlew test` + linter. Review all changed files for: `as any`/`@SuppressWarnings`, empty catches, `console.log` in prod, commented-out code, unused imports. Check AI slop: excessive comments, over-abstraction, generic names (`data`/`result`/`item`/`temp`).
  Output: `Build [PASS/FAIL] | Lint [PASS/FAIL] | Tests [N pass/N fail] | Files [N clean/N issues] | VERDICT`

- [ ] F3. **Real Manual QA** — `unspecified-high`
  Start from clean state. Execute EVERY QA scenario from EVERY task — follow exact steps, capture evidence. Test cross-task integration. Test edge cases: empty state, invalid input. Save to `.omo/evidence/final-qa/`.
  Output: `Scenarios [N/N pass] | Integration [N/N] | Edge Cases [N tested] | VERDICT`

- [ ] F4. **Scope Fidelity Check** — `deep`
  For each task: read "What to do", read actual diff (git log/diff). Verify 1:1 — everything in spec was built (no missing), nothing beyond spec was built (no creep). Check "Must NOT do" compliance. Detect cross-task contamination.
  Output: `Tasks [N/N compliant] | Contamination [CLEAN/N issues] | Unaccounted [CLEAN/N files] | VERDICT`

---

## Commit Strategy

- **Wave 1**: Grouped commits for infrastructure (Task 1) + builder APIs (Tasks 2-3) + utilities (Tasks 4-6)
- **Wave 2**: One commit per task (component-level commits)
- **Wave 3**: Grouped commit for behavioral extraction (Tasks 20-24)
- **Wave 4**: Grouped commit for tests (Tasks 25-29)
- **Final Wave**: No commits — verification only

---

## Success Criteria

### Verification Commands
```bash
# Compilation
rtk gradlew compileJava
# Expected: BUILD SUCCESSFUL

# Tests
rtk gradlew test
# Expected: BUILD SUCCESSFUL, all tests pass

# WAR build
rtk gradlew buildWar
# Expected: BUILD SUCCESSFUL

# wicket:id resolution check
grep -r "wicket:id" wicket/ --include="*.html" | wc -l
# Expected: same count as before refactor (no IDs removed)
```

### Final Checklist
- [ ] All "Must Have" present
- [ ] All "Must NOT Have" absent
- [ ] All tests pass
- [ ] `appendJavaScript` count in Java files < 5
- [ ] `EnderecoListViewPanel` < 200 lines
- [ ] `ClientesFisicosTablePanel` + `ClientesJuridicosTablePanel` unified
- [ ] F1-F4 all APPROVE
- [ ] User explicit "okay"
