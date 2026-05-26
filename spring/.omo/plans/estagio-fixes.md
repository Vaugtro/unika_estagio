# Estágio FIXME Fixes

## TL;DR

> **Quick Summary**: Fix 10 FIXME items in the Spring Boot + Wicket client management application. Issues span validation, UI behavior, error handling, and form persistence across both ClienteFisico and ClienteJuridico types.
>
> **Deliverables**:
> - Telefone made optional in EnderecoCreateTablePanel
> - Date fields use DD/MM/YYYY format with input mask
> - RG and Telefone input templates (data-mask + validators)
> - Endereco creation working from Wicket modals
> - Principal button constraint enforced
> - Database errors mapped to user-friendly messages
> - Edit forms converted from inline to modals (Fisico + Juridico)
> - Invalid input values preserved on validation failure
> - Validation feedback deduplicated (inline for fields, toast for form-level)
> - Delete button verified on both detail pages
> - Tests for all behavioral fixes
>
> **Estimated Effort**: Large
> **Parallel Execution**: YES - 4 waves
> **Critical Path**: Task 1 → Task 4 → Task 8 → Task 12 → F1-F4 → user okay

---

## Context

### Original Request
Fix 10 FIXME items from `spring/estagio/README.md`:
1. Endereço principal button breaking constraint
2. Edit Cliente Form must be a modal
3. Wicket toast showing database errors
4. Endereço from Wicket form not being created
5. Invalid Endereços inputs cleaning on validation failure
6. Add a delete Cliente button, only available if inactive
7. Telefone and RG must be templated properly
8. Calendar must be default DD/MM/YYYY
9. Modal must not show the same data from input validations
10. Telefone is optional

### Interview Summary
**Key Discussions**:
- Fixes apply to BOTH ClienteFisico and ClienteJuridico (confirmed)
- Tests after implementation (confirmed)
- Delete button on detail page only (already exists for both types)
- Validation: inline feedback for fields, toast for form-level/non-input messages
- Every fix committed with user's git auth (`vaugtro <victor.agustgm@gmail.com>`)
- Edit modal scope: only replace inline fields (nome, email, status toggle)
- Date format: vanilla JS + data-mask, no new dependencies
- Keep FisicoEditModal and JuridicoEditModal separate (no premature abstraction)

**Research Findings**:
- `EnderecoCreateTablePanel` is shared between Fisico and Juridico create modals
- `EnderecoListViewPanel` handles principal/edit/delete for existing addresses
- `ValidationFeedback.handleFormError()` shows both inline AND toast for all errors
- `ClienteFisicoRowUpdateForm` / `ClienteJuridicoRowUpdateForm` are inline table row forms
- `ClienteFisicoDetalhePage` and `ClienteJuridicoDetalhePage` already have delete buttons (FIXME #6 already done)
- FIXME #10 (Telefone required) blocks FIXME #4 (Endereco creation failing)
- Date fields use HTML5 `type="date"` which shows YYYY-MM-DD in browser
- RG field in create modal has no input mask (unlike CPF which has data-mask)

### Metis Review
**Identified Gaps** (addressed):
- FIXME #6 already implemented — converted to verification task
- FIXME #10 blocks #4 — execution order adjusted
- No Wicket test infrastructure — service/validation tests only for UI fixes
- Guardrail: do NOT create shared abstract edit modal
- Guardrail: do NOT change REST controllers unless required

---

## Work Objectives

### Core Objective
Fix all 10 FIXME items in the Wicket UI layer, ensuring both ClienteFisico and ClienteJuridico flows work correctly with proper validation, error handling, and user experience.

### Concrete Deliverables
- `EnderecoCreateTablePanel.java` — telefone optional, input masks preserved
- `ClienteFisicoCreateModal.java` / `ClienteJuridicoCreateModal.java` — date format, RG mask, error handling
- `ClienteFisicoEditModal.java` / `ClienteJuridicoEditModal.java` — NEW components (replace inline forms)
- `ValidationFeedback.java` — deduplicate validation display, preserve inputs on error
- `EnderecoListViewPanel.java` — principal constraint, user-friendly error messages
- Tests for service/validation changes
- Git commits for each fix with user auth

### Definition of Done
- [ ] All 10 FIXME items verified working
- [ ] `rtk gradlew test` passes
- [ ] Each fix committed with user's git auth
- [ ] No database schema changes required
- [ ] No new dependencies added

### Must Have
- Telefone optional in address creation
- Date input accepts DD/MM/YYYY
- RG has input mask matching formatter pattern
- Endereco creation succeeds from create modal
- Principal constraint enforced (exactly one principal per cliente)
- Database errors show user-friendly Portuguese messages
- Edit forms are modals, not inline table rows
- Invalid input values preserved on validation failure
- Validation shows inline feedback for fields, toast only for form-level errors
- Delete button verified on both detail pages

### Must NOT Have (Guardrails)
- New features beyond the 10 FIXMEs
- Changes to REST controllers or service business logic (unless required by fix)
- Shared abstract edit modal component
- New dependencies (date picker libraries, etc.)
- Database schema migrations
- Angular frontend changes
- Soft-delete or confirmation dialog for delete (beyond existing implementation)
- Expanding edit modal to include fields beyond nome/email/status

---

## Verification Strategy

> **ZERO HUMAN INTERVENTION** — ALL verification is agent-executed.

### Test Decision
- **Infrastructure exists**: YES (JUnit 5 + Mockito, 22 existing test files)
- **Automated tests**: Tests after implementation
- **Framework**: JUnit 5 + Mockito
- **Wicket tests**: NOT available — UI fixes verified via service/validation tests + curl/REPL

### QA Policy
Every task MUST include agent-executed QA scenarios.
Evidence saved to `.omo/evidence/task-{N}-{scenario-slug}.{ext}`.

- **Backend/Service**: Bash (curl) or Java REPL — verify DTO validation, service behavior
- **Wicket UI**: Manual verification via Playwright or tmux where applicable
- **Each scenario**: exact steps + concrete test data + expected results + evidence path

---

## Execution Strategy

### Parallel Execution Waves

```
Wave 1 (Foundation — independent UI tweaks):
├── Task 1: Telefone optional + DTO relaxation [quick]
├── Task 2: Calendar DD/MM/YYYY format [quick]
└── Task 3: RG and Telefone input templates [quick]

Wave 2 (Core fixes — depends on Wave 1):
├── Task 4: Endereco creation from Wicket form [quick]
├── Task 5: Database error messages in toast [quick]
└── Task 6: Edit modal for ClienteFisico [unspecified-high]

Wave 3 (Remaining fixes + Juridico modal):
├── Task 7: Edit modal for ClienteJuridico [unspecified-high]
├── Task 8: Principal button constraint [quick]
├── Task 9: Preserve invalid inputs on validation [quick]
└── Task 10: Validation feedback deduplication [quick]

Wave 4 (Verification + tests):
├── Task 11: Verify delete button (FIXME #6) [quick]
└── Task 12: Tests for behavioral fixes [quick]

Wave FINAL (After ALL tasks — 4 parallel reviews):
├── Task F1: Plan compliance audit (oracle)
├── Task F2: Code quality review (unspecified-high)
├── Task F3: Real manual QA (unspecified-high)
└── Task F4: Scope fidelity check (deep)
-> Present results -> Get explicit user okay

Critical Path: Task 1 → Task 4 → Task 8 → Task 12 → F1-F4 → user okay
Parallel Speedup: ~50% faster than sequential
Max Concurrent: 3 (Wave 2)
```

### Dependency Matrix

| Task | Depends On | Blocks |
|------|-----------|--------|
| 1 | — | 4 |
| 2 | — | — |
| 3 | — | — |
| 4 | 1 | 8 |
| 5 | — | — |
| 6 | — | — |
| 7 | — | — |
| 8 | 4 | — |
| 9 | — | — |
| 10 | — | — |
| 11 | — | — |
| 12 | 1-11 | — |

### Agent Dispatch Summary

- **Wave 1**: **3** — T1 → `quick`, T2 → `quick`, T3 → `quick`
- **Wave 2**: **3** — T4 → `quick`, T5 → `quick`, T6 → `unspecified-high`
- **Wave 3**: **4** — T7 → `unspecified-high`, T8 → `quick`, T9 → `quick`, T10 → `quick`
- **Wave 4**: **2** — T11 → `quick`, T12 → `quick`
- **FINAL**: **4** — F1 → `oracle`, F2 → `unspecified-high`, F3 → `unspecified-high`, F4 → `deep`

---

## TODOs

> Implementation + Test = ONE Task. Never separate.
> EVERY task MUST have: Recommended Agent Profile + Parallelization info + QA Scenarios.
> **A task WITHOUT QA Scenarios is INCOMPLETE.**
> **FORMAT**: Task labels use bare numbers: `1.`, `2.`, `3.` — NOT `T1.`, `Task 1.`, `Phase 1:`.
> Final Verification Wave labels use `F1.`, `F2.`, etc.

- [x] 1. **Make Telefone optional in EnderecoCreateTablePanel**

  **What to do**:
  - In `EnderecoCreateTablePanel.java`, change `telefoneField.setRequired(true)` to `telefoneField.setRequired(false)`
  - Check if `EnderecoWithinClienteCreateRequest` has `@NotBlank` or `@NotNull` on telefone — if yes, remove it
  - Check if `EnderecoCreateRequest` has `@NotBlank` or `@NotNull` on telefone — if yes, remove it
  - Check if `EnderecoUpdateRequest` has `@NotBlank` or `@NotNull` on telefone — if yes, remove it
  - Check JPA entity `Endereco` — if telefone column has `nullable=false`, change to `nullable=true`
  - Verify no other validators enforce telefone presence (e.g., custom validators)

  **Must NOT do**:
  - Do NOT remove telefone field entirely
  - Do NOT change the data-mask attribute
  - Do NOT modify unrelated fields

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Simple field requirement changes across a few files
  - **Skills**: []
  - **Skills Evaluated but Omitted**: None needed

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Tasks 2, 3)
  - **Blocks**: Task 4 (Endereco creation depends on this)
  - **Blocked By**: None

  **References**:
  - `wicket/component/shared/EnderecoCreateTablePanel.java:113-122` — telefone field setup
  - `dto/endereco/EnderecoWithinClienteCreateRequest.java` — DTO validation
  - `dto/endereco/EnderecoCreateRequest.java` — DTO validation
  - `dto/endereco/EnderecoUpdateRequest.java` — DTO validation
  - `model/Endereco.java` — JPA entity telefone column
  - `model/formatter/TelefoneFormatter.java` — formatting logic (no changes needed)

  **Acceptance Criteria**:
  - [ ] `telefoneField.setRequired(false)` in EnderecoCreateTablePanel
  - [ ] No `@NotBlank` / `@NotNull` on telefone in any DTO
  - [ ] `nullable=true` for telefone in JPA entity (if previously false)
  - [ ] `rtk gradlew test` passes

  **QA Scenarios**:

  ```
  Scenario: Create endereco without telefone (happy path)
    Tool: Bash (curl)
    Preconditions: Backend running, database accessible
    Steps:
      1. Create a ClienteFisico with one Endereco where telefone is empty/blank
      2. Verify the endereco is created successfully (HTTP 200 or check DB)
      3. Query database: SELECT telefone FROM endereco WHERE cliente_id = ?
    Expected Result: Endereco exists, telefone column is NULL
    Failure Indicators: ConstraintViolationException, DataIntegrityViolationException, or HTTP error
    Evidence: .omo/evidence/task-1-telefone-optional-success.log
  ```

  **Evidence to Capture**:
  - [ ] Terminal output showing successful endereco creation

  **Commit**: YES
  - Message: `fix(wicket): make telefone optional in endereco creation — relates to FIXME #10`
  - Files: `EnderecoCreateTablePanel.java`, DTO files, `Endereco.java`
  - Pre-commit: `rtk gradlew test`

- [x] 2. **Change calendar date input to DD/MM/YYYY format**

  **What to do**:
  - In `ClienteFisicoCreateModal.java`, change `dataNascimentoField` from HTML5 `type="date"` to `type="text"` with `data-mask="99/99/9999"` placeholder
  - In `ClienteJuridicoCreateModal.java`, change `dataCriacaoEmpresaField` similarly
  - Update the Java submit logic to parse DD/MM/YYYY format using `DateTimeFormatter.ofPattern("dd/MM/yyyy")`
  - Update the HTML templates to remove `type="date"` references and update placeholder text
  - Ensure the parsed LocalDate is still passed to the DTO correctly

  **Must NOT do**:
  - Do NOT add jQuery UI, Bootstrap datepicker, or any new dependency
  - Do NOT change the DTO or service layer date format
  - Do NOT modify the detalhe page display format (already DD/MM/YYYY)

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Input type change with formatter update
  - **Skills**: []
  - **Skills Evaluated but Omitted**: None needed

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Tasks 1, 3)
  - **Blocks**: None
  - **Blocked By**: None

  **References**:
  - `wicket/component/modal/ClienteFisicoCreateModal.java:89-103` — dataNascimento field
  - `wicket/component/modal/ClienteJuridicoCreateModal.java` — dataCriacaoEmpresa field
  - `wicket/component/modal/ClienteFisicoCreateModal.html:72-76` — date input markup
  - `wicket/component/modal/ClienteJuridicoCreateModal.html` — date input markup
  - `dto/clientefisico/ClienteFisicoCreateRequest.java` — dataNascimento field type
  - `dto/clientejuridico/ClienteJuridicoCreateRequest.java` — dataCriacaoEmpresa field type

  **Acceptance Criteria**:
  - [ ] Both create modals use text input with DD/MM/YYYY mask
  - [ ] Java submit logic parses DD/MM/YYYY correctly
  - [ ] Invalid date format shows user-friendly error
  - [ ] `rtk gradlew test` passes

  **QA Scenarios**:

  ```
  Scenario: Create cliente with DD/MM/YYYY date (happy path)
    Tool: Bash (Java REPL)
    Preconditions: Code compiled
    Steps:
      1. Parse "15/03/1990" with DateTimeFormatter.ofPattern("dd/MM/yyyy")
      2. Verify result is LocalDate.of(1990, 3, 15)
    Expected Result: Correct LocalDate parsing
    Evidence: .omo/evidence/task-2-date-parse-success.log

  Scenario: Invalid date format shows error
    Tool: Bash (Java REPL)
    Preconditions: Code compiled
    Steps:
      1. Parse "03-15-1990" with DateTimeFormatter.ofPattern("dd/MM/yyyy")
      2. Verify DateTimeParseException is thrown
    Expected Result: Exception thrown, caught by validation logic
    Evidence: .omo/evidence/task-2-date-parse-error.log
  ```

  **Evidence to Capture**:
  - [ ] Date parsing output for valid and invalid formats

  **Commit**: YES
  - Message: `fix(wicket): use DD/MM/YYYY date format in create modals — relates to FIXME #8`
  - Files: `ClienteFisicoCreateModal.java`, `ClienteJuridicoCreateModal.java`, HTML templates
  - Pre-commit: `rtk gradlew test`

- [x] 3. **Add input templates (masks) for RG and Telefone fields**

  **What to do**:
  - In `ClienteFisicoCreateModal.java`, add `data-mask` and `PatternValidator` to the RG field:
    - Mask: `"99.999.999-9"` (covers 7-9 digits with placeholder support)
    - Or use `"XX.XXX.XXX-X"` pattern with regex validation
  - In `EnderecoCreateTablePanel.java`, verify Telefone field already has `data-mask="(00) 00000-0000"` and add `PatternValidator` for Brazilian phone format
  - Ensure the `RGFormatter` pattern matches the input mask expectations
  - Update HTML templates if needed to reflect placeholder changes
  - Note: The RG mask should accommodate 7-9 digits. Consider using a simpler mask like `"999999999"` (raw digits only) with server-side formatting, OR use a smart mask library approach. Since we can't add dependencies, use `data-mask` with the maximum pattern and let the formatter handle display.

  **Must NOT do**:
  - Do NOT add new JS libraries for masking
  - Do NOT change the existing TelefoneFormatter or RGFormatter logic
  - Do NOT make RG required (it may already be required, keep existing requirement)

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Adding mask attributes and validators
  - **Skills**: []
  - **Skills Evaluated but Omitted**: None needed

  **Parallelization**:
  - **Can Run In Parallel**: YES
  - **Parallel Group**: Wave 1 (with Tasks 1, 2)
  - **Blocks**: None
  - **Blocked By**: None

  **References**:
  - `wicket/component/modal/ClienteFisicoCreateModal.java:72-79` — RG field (no mask currently)
  - `wicket/component/shared/EnderecoCreateTablePanel.java:113-122` — telefone field (has mask)
  - `model/formatter/RGFormatter.java` — RG formatting patterns
  - `model/formatter/TelefoneFormatter.java` — telefone formatting patterns
  - `validation/ValidationConstants.java` — RG_LENGTH_MIN (7), RG_LENGTH_MAX (9)
  - `wicket/component/modal/ClienteFisicoCreateModal.html:55-58` — RG input markup

  **Acceptance Criteria**:
  - [ ] RG field in create modal has data-mask attribute
  - [ ] Telefone field has PatternValidator for Brazilian phone format
  - [ ] Both fields accept raw digits and display formatted on blur/submit
  - [ ] `rtk gradlew test` passes

  **QA Scenarios**:

  ```
  Scenario: RG input masked correctly
    Tool: Bash (Java REPL)
    Preconditions: Code compiled
    Steps:
      1. Call RGFormatter.format("123456789") → expect "12.345.678-9"
      2. Call RGFormatter.format("12345678") → expect "12.345.67-8"
      3. Call RGFormatter.format("1234567") → expect "1.234.56-7"
    Expected Result: All formats match expected output
    Evidence: .omo/evidence/task-3-rg-format.log

  Scenario: Telefone input validation
    Tool: Bash (Java REPL)
    Preconditions: Code compiled
    Steps:
      1. Call TelefoneFormatter.format("11912345678") → expect "(11) 91234-5678"
      2. Call TelefoneFormatter.format("1134567890") → expect "(11) 3456-7890"
    Expected Result: Formats match expected output
    Evidence: .omo/evidence/task-3-telefone-format.log
  ```

  **Evidence to Capture**:
  - [ ] Formatter output for RG and Telefone sample inputs

  **Commit**: YES
  - Message: `fix(wicket): add input masks for RG and telefone fields — relates to FIXME #7`
  - Files: `ClienteFisicoCreateModal.java`, `EnderecoCreateTablePanel.java`, HTML templates
  - Pre-commit: `rtk gradlew test`

- [x] 4. **Fix Endereco creation from Wicket create modal**

  **What to do**:
  - After Task 1 (telefone optional), verify that creating a Cliente with Enderecos from `ClienteFisicoCreateModal` and `ClienteJuridicoCreateModal` succeeds
  - Check if the issue is in the DTO construction: verify `EnderecoWithinClienteCreateRequest` is correctly passed to the service
  - Check `ClienteFisicoServiceImpl.create()` and `ClienteJuridicoServiceImpl.create()` to ensure they persist enderecos
  - Look for any `@Valid` or `@NotEmpty` constraints on the `enderecos` list in the create DTOs
  - If enderecos are built correctly but not persisted, the bug may be in the service implementation (not mapping DTOs to entities properly)
  - Verify that `EnderecoCreateTablePanel` doesn't clear the model on validation failure (see Task 9 for that fix)

  **Must NOT do**:
  - Do NOT change the service interface signatures
  - Do NOT modify the REST controller layer
  - Do NOT add new database constraints

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Investigation + targeted fix based on findings
  - **Skills**: []
  - **Skills Evaluated but Omitted**: None needed

  **Parallelization**:
  - **Can Run In Parallel**: YES (with Tasks 5, 6 in Wave 2)
  - **Parallel Group**: Wave 2
  - **Blocks**: Task 8 (principal constraint testing)
  - **Blocked By**: Task 1 (telefone optional — this was likely the blocker)

  **References**:
  - `wicket/component/modal/ClienteFisicoCreateModal.java:115-130` — endereco DTO construction
  - `wicket/component/modal/ClienteJuridicoCreateModal.java` — endereco DTO construction
  - `dto/clientefisico/ClienteFisicoCreateRequest.java` — check endereco list constraints
  - `dto/clientejuridico/ClienteJuridicoCreateRequest.java` — check endereco list constraints
  - `service/impl/ClienteFisicoServiceImpl.java` — create method implementation
  - `service/impl/ClienteJuridicoServiceImpl.java` — create method implementation
  - `mapper/ClienteFisicoMapper.java` — DTO to entity mapping
  - `mapper/ClienteJuridicoMapper.java` — DTO to entity mapping

  **Acceptance Criteria**:
  - [ ] Creating a ClienteFisico with one Endereco succeeds
  - [ ] Creating a ClienteJuridico with one Endereco succeeds
  - [ ] Endereco data is persisted in database (check via query or REST endpoint)
  - [ ] `rtk gradlew test` passes

  **QA Scenarios**:

  ```
  Scenario: Create ClienteFisico with endereco (happy path)
    Tool: Bash (curl)
    Preconditions: Backend running
    Steps:
      1. POST /api/clientes-fisicos with ClienteFisicoCreateRequest including one EnderecoWithinClienteCreateRequest
      2. Verify HTTP 201/200 response
      3. GET /api/clientes-fisicos/{id}/enderecos
      4. Verify response contains the created endereco
    Expected Result: Endereco created and retrievable
    Failure Indicators: HTTP 400, ConstraintViolationException, empty endereco list
    Evidence: .omo/evidence/task-4-endereco-creation-fisico.log

  Scenario: Create ClienteJuridico with endereco (happy path)
    Tool: Bash (curl)
    Preconditions: Backend running
    Steps:
      1. POST /api/clientes-juridicos with ClienteJuridicoCreateRequest including one EnderecoWithinClienteCreateRequest
      2. Verify HTTP 201/200 response
      3. GET /api/clientes-juridicos/{id}/enderecos
      4. Verify response contains the created endereco
    Expected Result: Endereco created and retrievable
    Evidence: .omo/evidence/task-4-endereco-creation-juridico.log
  ```

  **Evidence to Capture**:
  - [ ] curl output showing successful endereco creation for both client types

  **Commit**: YES
  - Message: `fix(wicket): ensure endereco creation works from create modals — relates to FIXME #4`
  - Files: Service impl, DTOs, or modal files as needed
  - Pre-commit: `rtk gradlew test`

- [x] 5. **Map database errors to user-friendly toast messages**

  **What to do**:
  - In `EnderecoListViewPanel.java`, replace `catch (Exception e)` blocks that show `e.getMessage()` directly
  - Map known database errors to Portuguese user-friendly messages:
    - `DataIntegrityViolationException` → "Já existe um endereço principal para este cliente."
    - Generic SQL/connection errors → "Erro ao acessar o banco de dados. Tente novamente."
  - In `ClienteFisicoCreateModal.java` and `ClienteJuridicoCreateModal.java`, improve the existing error handling:
    - Keep the existing CPF/CNPJ duplicate detection
    - Add clearer messages for other RuntimeExceptions
  - Ensure NO raw SQL, constraint names, or stack traces reach the user-facing toast

  **Must NOT do**:
  - Do NOT create a global exception handler (out of scope)
  - Do NOT change the service layer exception types
  - Do NOT log less detail — server logs should still have full exceptions

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Message mapping changes in catch blocks
  - **Skills**: []
  - **Skills Evaluated but Omitted**: None needed

  **Parallelization**:
  - **Can Run In Parallel**: YES (with Tasks 4, 6 in Wave 2)
  - **Parallel Group**: Wave 2
  - **Blocks**: None
  - **Blocked By**: None

  **References**:
  - `wicket/component/shared/EnderecoListViewPanel.java:117-120` — principal button catch block
  - `wicket/component/shared/EnderecoListViewPanel.java:231-237` — modal form catch block
  - `wicket/component/modal/ClienteFisicoCreateModal.java:154-164` — create modal catch block
  - `wicket/component/modal/ClienteJuridicoCreateModal.java` — create modal catch block
  - `exceptions/BusinessException.java` — business exception type

  **Acceptance Criteria**:
  - [ ] All `catch (Exception e)` blocks in Wicket UI show user-friendly messages
  - [ ] No raw exception messages reach the toast
  - [ ] Server logs still contain full exception details
  - [ ] `rtk gradlew test` passes

  **QA Scenarios**:

  ```
  Scenario: Database error shows friendly message
    Tool: Bash (Java REPL)
    Preconditions: Code compiled
    Steps:
      1. Inspect catch blocks in EnderecoListViewPanel
      2. Verify no block calls e.getMessage() directly in toast
      3. Verify DataIntegrityViolationException has specific friendly message
    Expected Result: All exceptions mapped to Portuguese messages
    Evidence: .omo/evidence/task-5-error-messages.log
  ```

  **Evidence to Capture**:
  - [ ] Code review output showing all catch blocks reviewed

  **Commit**: YES
  - Message: `fix(wicket): map database errors to user-friendly toast messages — relates to FIXME #3`
  - Files: `EnderecoListViewPanel.java`, `ClienteFisicoCreateModal.java`, `ClienteJuridicoCreateModal.java`
  - Pre-commit: `rtk gradlew test`

- [x] 6. **Convert ClienteFisico inline edit form to modal**

  **What to do**:
  - Create new component `ClienteFisicoEditModal.java` (similar structure to `ClienteFisicoCreateModal`)
  - Create corresponding HTML template `ClienteFisicoEditModal.html`
  - The edit modal should contain:
    - Nome field (editable, required, with validation)
    - Email field (editable, with EmailAddressValidator)
    - Status toggle button (same as inline form)
    - Save button (submits update)
    - Cancel button (closes modal)
  - The modal should pre-populate with the selected client's current data
  - Modify `ClientesFisicosTablePanel.java` and `ClienteFisicoDataView.java` to:
    - Replace the inline `ClienteFisicoRowUpdateForm` with a row that shows read-only data
    - Add an "Editar" button that opens the modal with the client's data
  - On save: call `clienteFisicoService.update(id, request)`, show success toast, refresh table
  - On validation error: show inline feedback, DO NOT close modal, preserve values
  - Remove or deprecate `ClienteFisicoRowUpdateForm` if no longer needed

  **Must NOT do**:
  - Do NOT add fields beyond nome, email, status (keep same scope as inline form)
  - Do NOT add endereco editing to this modal
  - Do NOT create an abstract base class shared with Juridico

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
    - Reason: Creating new modal component with proper Wicket AJAX behavior, model binding, and integration with table
  - **Skills**: []
  - **Skills Evaluated but Omitted**: None needed

  **Parallelization**:
  - **Can Run In Parallel**: YES (with Tasks 4, 5 in Wave 2)
  - **Parallel Group**: Wave 2
  - **Blocks**: None
  - **Blocked By**: None

  **References**:
  - `wicket/component/modal/ClienteFisicoCreateModal.java` — pattern to follow for modal structure
  - `wicket/component/modal/ClienteFisicoCreateModal.html` — HTML template pattern
  - `wicket/component/form/ClienteFisicoRowUpdateForm.java` — current inline form fields to replicate
  - `wicket/component/table/ClientesFisicosTablePanel.java` — table integration point
  - `wicket/component/dataview/ClienteFisicoDataView.java` — row rendering integration
  - `wicket/page/clientes/ClienteFisicoDetalhePage.java` — how detalhe page displays client data (for reference)
  - `dto/clientefisico/ClienteFisicoUpdateRequest.java` — update DTO structure

  **Acceptance Criteria**:
  - [ ] `ClienteFisicoEditModal.java` component created with proper Wicket markup
  - [ ] HTML template created alongside Java file
  - [ ] Table shows read-only data with "Editar" button instead of inline form
  - [ ] Modal opens with pre-populated client data
  - [ ] Save submits update and refreshes table
  - [ ] Cancel closes modal without changes
  - [ ] Validation errors show inline feedback, modal stays open with values preserved
  - [ ] `rtk gradlew test` passes

  **QA Scenarios**:

  ```
  Scenario: Open edit modal and save changes (happy path)
    Tool: Playwright (browser automation)
    Preconditions: Application running, at least one ClienteFisico exists
    Steps:
      1. Navigate to home page with clientes table
      2. Click "Editar" button on first client row
      3. Verify modal opens with current client name and email
      4. Change name to "Teste Modal Edit"
      5. Click "Salvar"
      6. Verify success toast appears
      7. Verify table row shows updated name
    Expected Result: Client updated, table refreshed, success message shown
    Failure Indicators: Modal doesn't open, values not preserved, update fails
    Evidence: .omo/evidence/task-6-fisico-edit-modal-success.png

  Scenario: Cancel edit modal without saving
    Tool: Playwright
    Preconditions: Application running, client exists
    Steps:
      1. Click "Editar" on a client row
      2. Change name field
      3. Click "Cancelar"
      4. Verify modal closes
      5. Verify table row shows ORIGINAL name (not changed)
    Expected Result: Modal closes, no changes persisted
    Evidence: .omo/evidence/task-6-fisico-edit-modal-cancel.png
  ```

  **Evidence to Capture**:
  - [ ] Screenshots of modal open, edit, save, and cancel flows

  **Commit**: YES
  - Message: `feat(wicket): convert ClienteFisico inline edit to modal — relates to FIXME #2`
  - Files: `ClienteFisicoEditModal.java`, `ClienteFisicoEditModal.html`, `ClientesFisicosTablePanel.java`, `ClienteFisicoDataView.java`
  - Pre-commit: `rtk gradlew test`

---

- [x] 7. **Convert ClienteJuridico inline edit form to modal**

  **What to do**:
  - Create new component `ClienteJuridicoEditModal.java` (mirror of Fisico edit modal)
  - Create corresponding HTML template `ClienteJuridicoEditModal.html`
  - Fields to include:
    - RazaoSocial field (editable, required)
    - Email field (editable, with EmailAddressValidator)
    - Status toggle button
    - Save and Cancel buttons
  - Modify `ClientesJuridicosTablePanel.java` and `ClienteJuridicoDataView.java`:
    - Replace inline `ClienteJuridicoRowUpdateForm` with read-only row + "Editar" button
    - Button opens modal with client data
  - On save: call `clienteJuridicoService.update(id, request)`
  - On validation error: show inline feedback, preserve values, keep modal open

  **Must NOT do**:
  - Do NOT create shared abstract base with Fisico modal
  - Do NOT add fields beyond razaoSocial, email, status
  - Do NOT add endereco editing

  **Recommended Agent Profile**:
  - **Category**: `unspecified-high`
    - Reason: New modal component with Wicket AJAX, similar complexity to Task 6
  - **Skills**: []
  - **Skills Evaluated but Omitted**: None needed

  **Parallelization**:
  - **Can Run In Parallel**: YES (with Tasks 8, 9, 10 in Wave 3)
  - **Parallel Group**: Wave 3
  - **Blocks**: None
  - **Blocked By**: None

  **References**:
  - `wicket/component/modal/ClienteJuridicoCreateModal.java` — pattern to follow
  - `wicket/component/form/ClienteJuridicoRowUpdateForm.java` — current inline form
  - `wicket/component/table/ClientesJuridicosTablePanel.java` — table integration
  - `wicket/component/dataview/ClienteJuridicoDataView.java` — row rendering
  - `dto/clientejuridico/ClienteJuridicoUpdateRequest.java` — update DTO

  **Acceptance Criteria**:
  - [ ] `ClienteJuridicoEditModal.java` and HTML created
  - [ ] Table shows read-only data with "Editar" button
  - [ ] Modal pre-populates with client data
  - [ ] Save updates and refreshes table
  - [ ] Cancel closes without changes
  - [ ] Validation errors show inline, modal stays open
  - [ ] `rtk gradlew test` passes

  **QA Scenarios**:

  ```
  Scenario: Edit ClienteJuridico via modal (happy path)
    Tool: Playwright
    Preconditions: Application running, at least one ClienteJuridico exists
    Steps:
      1. Navigate to Juridicos table
      2. Click "Editar" on first row
      3. Change razaoSocial to "Empresa Teste Modal"
      4. Click "Salvar"
      5. Verify success toast
      6. Verify table shows updated razaoSocial
    Expected Result: Client updated, table refreshed
    Evidence: .omo/evidence/task-7-juridico-edit-modal-success.png
  ```

  **Evidence to Capture**:
  - [ ] Screenshot of modal edit flow

  **Commit**: YES
  - Message: `feat(wicket): convert ClienteJuridico inline edit to modal — relates to FIXME #2`
  - Files: `ClienteJuridicoEditModal.java`, `ClienteJuridicoEditModal.html`, `ClientesJuridicosTablePanel.java`, `ClienteJuridicoDataView.java`
  - Pre-commit: `rtk gradlew test`

- [x] 8. **Enforce Endereco principal constraint (exactly one principal)**

  **What to do**:
  - In `EnderecoListViewPanel.java`, modify the `setAsPrincipalBtn` logic:
    - If client has only ONE endereco, the button should be disabled with tooltip "Endereço único — já é principal"
    - When setting a new principal, ensure the old principal is demoted
  - Verify the service layer (`EnderecoServiceImpl.setAsPrincipal()`) correctly handles the swap
  - If service doesn't handle it, fix it there (but prefer service layer fix over UI workaround)
  - Ensure the toast message is clear: "Endereço definido como principal!"
  - The constraint is: every cliente must have EXACTLY ONE principal endereco at all times

  **Must NOT do**:
  - Do NOT allow zero principals
  - Do NOT allow multiple principals
  - Do NOT change the database schema

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Logic check and button state fix
  - **Skills**: []
  - **Skills Evaluated but Omitted**: None needed

  **Parallelization**:
  - **Can Run In Parallel**: YES (with Tasks 7, 9, 10 in Wave 3)
  - **Parallel Group**: Wave 3
  - **Blocks**: None
  - **Blocked By**: Task 4 (endereco creation must work first to test this)

  **References**:
  - `wicket/component/shared/EnderecoListViewPanel.java:105-134` — principal button logic
  - `service/impl/EnderecoServiceImpl.java` — setAsPrincipal method
  - `model/Endereco.java` — principal field and relationship to Cliente
  - `service/EnderecoService.java` — interface

  **Acceptance Criteria**:
  - [ ] Cliente with one endereco: button disabled, endereco is principal
  - [ ] Cliente with multiple enderecos: clicking "Definir como principal" on non-principal swaps correctly
  - [ ] After swap, exactly one endereco is principal
  - [ ] `rtk gradlew test` passes

  **QA Scenarios**:

  ```
  Scenario: Set principal on multi-endereco client (happy path)
    Tool: Bash (curl)
    Preconditions: Backend running, client with 2+ enderecos
    Steps:
      1. GET /api/clientes-fisicos/{id}/enderecos
      2. Identify non-principal endereco
      3. POST /api/enderecos/{id}/principal (or equivalent endpoint)
      4. GET enderecos again
      5. Verify exactly one endereco has principal=true
      6. Verify the correct endereco is now principal
    Expected Result: Exactly one principal, swap successful
    Evidence: .omo/evidence/task-8-principal-constraint.log
  ```

  **Evidence to Capture**:
  - [ ] API output showing endereco principal swap

  **Commit**: YES
  - Message: `fix(wicket): enforce exactly-one principal constraint for enderecos — relates to FIXME #1`
  - Files: `EnderecoListViewPanel.java`, `EnderecoServiceImpl.java` (if service fix needed)
  - Pre-commit: `rtk gradlew test`

- [x] 9. **Preserve invalid input values on validation failure**

  **What to do**:
  - Investigate why `EnderecoCreateTablePanel` inputs are cleared when validation fails
  - The issue is likely in `ValidationFeedback.handleFormError()` or in how Wicket re-renders the ListView
  - Check if `CompoundPropertyModel` is correctly preserving values on validation error
  - In `handleFormError()`, the call `target.add(form)` re-renders the form. If the model values are not set, inputs appear empty.
  - Ensure that on validation failure, Wicket's form processing doesn't clear the input values before the model is updated
  - Check if `FormComponent.updateModel()` is being called prematurely
  - Potential fix: In `EnderecoCreateTablePanel`, ensure each ListItem's model is preserved during re-render
  - Alternative: Override `onError` in the modal form to NOT refresh the endereco table, or refresh it with the current model state

  **Must NOT do**:
  - Do NOT disable form validation
  - Do NOT change the DTO validation rules
  - Do NOT add client-side only validation

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Wicket model behavior investigation + targeted fix
  - **Skills**: []
  - **Skills Evaluated but Omitted**: None needed

  **Parallelization**:
  - **Can Run In Parallel**: YES (with Tasks 7, 8, 10 in Wave 3)
  - **Parallel Group**: Wave 3
  - **Blocks**: None
  - **Blocked By**: None

  **References**:
  - `wicket/component/ValidationFeedback.java:64-96` — handleFormError method
  - `wicket/component/shared/EnderecoCreateTablePanel.java:38-166` — ListView and model setup
  - `wicket/model/EnderecoCreateFormModel.java` — form model structure
  - Apache Wicket docs: Form validation and model preservation on error

  **Acceptance Criteria**:
  - [ ] Fill endereco form with invalid data (e.g., blank required field)
  - [ ] Submit and get validation error
  - [ ] All previously entered values remain in their fields
  - [ ] `rtk gradlew test` passes

  **QA Scenarios**:

  ```
  Scenario: Invalid endereco input preserves values
    Tool: Playwright
    Preconditions: Application running, on create modal
    Steps:
      1. Open create ClienteFisico modal
      2. Fill CPF, nome, email, dataNascimento correctly
      3. In endereco section, fill logradouro="Rua Teste", numero="123", bairro="Bairro", cidade="Cidade", estado="SP", cep="12345-678"
      4. Leave telefone empty (now optional) but clear cidade (required)
      5. Click "Salvar"
      6. Verify validation error appears
      7. Verify logradouro still shows "Rua Teste"
      8. Verify numero still shows "123"
      9. Verify all other filled fields retain their values
    Expected Result: All entered values preserved, only invalid field highlighted
    Evidence: .omo/evidence/task-9-input-preserved.png
  ```

  **Evidence to Capture**:
  - [ ] Screenshot showing form after validation error with values intact

  **Commit**: YES
  - Message: `fix(wicket): preserve input values on endereco validation failure — relates to FIXME #5`
  - Files: `ValidationFeedback.java`, `EnderecoCreateTablePanel.java`, modal forms
  - Pre-commit: `rtk gradlew test`

- [x] 10. **Deduplicate validation feedback (inline vs toast)**

  **What to do**:
  - Modify `ValidationFeedback.handleFormError()` to:
    - Still highlight invalid fields via JS (inline feedback)
    - Show toast ONLY with a generic form-level message: "Corrija os campos destacados."
    - Do NOT include individual field error messages in the toast
  - For `showToast` calls outside of form validation (e.g., success messages, database errors), keep using specific messages
  - Ensure that inline feedback labels (`wicket:id="*Feedback"`) continue to show per-field messages
  - This applies to all forms: create modals, edit modals, and endereco modal

  **Must NOT do**:
  - Do NOT remove inline field-level feedback
  - Do NOT remove toast entirely
  - Do NOT change the `showToast()` utility method signature

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Behavior change in single utility method
  - **Skills**: []
  - **Skills Evaluated but Omitted**: None needed

  **Parallelization**:
  - **Can Run In Parallel**: YES (with Tasks 7, 8, 9 in Wave 3)
  - **Parallel Group**: Wave 3
  - **Blocks**: None
  - **Blocked By**: None

  **References**:
  - `wicket/component/ValidationFeedback.java:64-96` — handleFormError method (the main change)
  - `wicket/component/ValidationFeedback.java:98-107` — showToast method (no changes needed)
  - `wicket/component/modal/ClienteFisicoCreateModal.java:168-171` — onError handler
  - `wicket/component/modal/ClienteJuridicoCreateModal.java` — onError handler
  - `wicket/component/shared/EnderecoListViewPanel.java:240-244` — modal onError handler

  **Acceptance Criteria**:
  - [ ] On validation error, inline labels show per-field messages
  - [ ] Toast shows only generic message: "Corrija os campos destacados."
  - [ ] No duplication: toast does NOT repeat field-specific messages
  - [ ] Success toasts and error toasts (non-validation) still show specific messages
  - [ ] `rtk gradlew test` passes

  **QA Scenarios**:

  ```
  Scenario: Validation shows inline only, toast is generic
    Tool: Playwright
    Preconditions: Application running, on create modal
    Steps:
      1. Open create ClienteFisico modal
      2. Leave required fields blank
      3. Click "Salvar"
      4. Verify inline feedback shows "Campo obrigatório" next to blank fields
      5. Verify toast shows generic message (NOT field-specific messages)
      6. Verify toast does NOT contain "Campo obrigatório"
    Expected Result: Inline = specific, Toast = generic only
    Evidence: .omo/evidence/task-10-validation-dedup.png
  ```

  **Evidence to Capture**:
  - [ ] Screenshot showing inline feedback and toast simultaneously

  **Commit**: YES
  - Message: `fix(wicket): deduplicate validation feedback — inline for fields, toast for form-level — relates to FIXME #9`
  - Files: `ValidationFeedback.java`
  - Pre-commit: `rtk gradlew test`

- [x] 11. **Verify delete Cliente button (FIXME #6 confirmation)**

  **What to do**:
  - Verify `ClienteFisicoDetalhePage` has `excluirBtn` that calls `hardDelete()` and is only visible when `estaAtivo == false`
  - Verify `ClienteJuridicoDetalhePage` has equivalent `excluirBtn`
  - Verify clicking delete redirects to home page after successful deletion
  - Verify `AbstractClienteService.hardDelete()` exists and works correctly
  - If any issue found (e.g., button always visible, delete fails for inactive client), fix it
  - Since Metis confirmed this is already implemented, this task is primarily verification

  **Must NOT do**:
  - Do NOT add delete button to table list view (user confirmed: detail page only)
  - Do NOT add confirmation dialog (not in scope)
  - Do NOT change delete to soft-delete

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Verification task, minimal code changes if any
  - **Skills**: []
  - **Skills Evaluated but Omitted**: None needed

  **Parallelization**:
  - **Can Run In Parallel**: YES (with Task 12 in Wave 4)
  - **Parallel Group**: Wave 4
  - **Blocks**: None
  - **Blocked By**: None

  **References**:
  - `wicket/page/clientes/ClienteFisicoDetalhePage.java:45-60` — excluirBtn logic
  - `wicket/page/clientes/ClienteJuridicoDetalhePage.java:44-59` — excluirBtn logic
  - `service/AbstractClienteService.java:62-68` — hardDelete method
  - `service/lifecycle/ClienteFisicoLifecycleService.java` — hardDelete interface
  - `service/lifecycle/ClienteJuridicoLifecycleService.java` — hardDelete interface

  **Acceptance Criteria**:
  - [ ] Both detail pages have excluirBtn
  - [ ] Button is invisible when client is active
  - [ ] Button is visible when client is inactive
  - [ ] Clicking delete removes client and redirects to home
  - [ ] `rtk gradlew test` passes

  **QA Scenarios**:

  ```
  Scenario: Delete button hidden for active client
    Tool: Bash (curl) + Playwright
    Preconditions: Application running, active client exists
    Steps:
      1. Navigate to ClienteFisico detalhe page for active client
      2. Verify "Excluir Cliente" button is NOT visible
      3. Inactivate the client via API or UI
      4. Refresh detalhe page
      5. Verify "Excluir Cliente" button IS visible
    Expected Result: Button visibility toggles correctly with status
    Evidence: .omo/evidence/task-11-delete-button-visibility.log

  Scenario: Delete inactive client succeeds
    Tool: Bash (curl)
    Preconditions: Backend running, inactive client exists
    Steps:
      1. DELETE /api/clientes-fisicos/{id} (or hardDelete endpoint)
      2. Verify HTTP 204/200
      3. GET /api/clientes-fisicos/{id}
      4. Verify HTTP 404 (not found)
    Expected Result: Client permanently deleted
    Evidence: .omo/evidence/task-11-delete-success.log
  ```

  **Evidence to Capture**:
  - [ ] Button visibility check output
  - [ ] Delete API call response

  **Commit**: YES (if fixes needed) or NO (if verification only)
  - Message: `fix(wicket): verify delete button visibility logic — relates to FIXME #6`
  - Files: `ClienteFisicoDetalhePage.java`, `ClienteJuridicoDetalhePage.java` (if changes needed)
  - Pre-commit: `rtk gradlew test`

- [x] 12. **Add tests for behavioral fixes**

  **What to do**:
  - Add unit tests for:
    - Telefone optional validation (no `@NotBlank` on telefone fields in DTOs)
    - Date parsing with DD/MM/YYYY format
    - RG and Telefone formatter behavior
    - Endereco principal constraint (service layer)
    - Error message mapping (verify no raw exception messages)
  - Add integration tests for:
    - Endereco creation without telefone
    - Cliente creation with endereco list
  - Update existing tests if they break due to changes
  - Follow existing test patterns: JUnit 5 + Mockito, parameterized tests for validators

  **Must NOT do**:
  - Do NOT add Wicket component tests (no test infrastructure exists)
  - Do NOT add UI automation tests
  - Do NOT change existing passing tests unless they break

  **Recommended Agent Profile**:
  - **Category**: `quick`
    - Reason: Writing tests following existing patterns
  - **Skills**: []
  - **Skills Evaluated but Omitted**: None needed

  **Parallelization**:
  - **Can Run In Parallel**: YES (with Task 11 in Wave 4)
  - **Parallel Group**: Wave 4
  - **Blocks**: None
  - **Blocked By**: Tasks 1-10 (need implementation before testing)

  **References**:
  - `src/test/java/com/desafio/estagio/validation/internal/` — existing validator tests
  - `src/test/java/com/desafio/estagio/model/formatter/` — existing formatter tests
  - `src/test/java/com/desafio/estagio/service/impl/` — existing service tests
  - `src/test/java/com/desafio/estagio/controller/` — existing controller tests

  **Acceptance Criteria**:
  - [ ] Tests for telefone optional in DTOs
  - [ ] Tests for DD/MM/YYYY date parsing
  - [ ] Tests for RG/Telefone formatting
  - [ ] Tests for endereco creation without telefone
  - [ ] `rtk gradlew test` passes (all existing + new tests)

  **QA Scenarios**:

  ```
  Scenario: Run all tests pass
    Tool: Bash
    Preconditions: Code changes complete
    Steps:
      1. Run `rtk gradlew test`
      2. Verify BUILD SUCCESS
      3. Verify no test failures
      4. Count total tests run vs baseline
    Expected Result: BUILD SUCCESS, all tests pass
    Failure Indicators: Any test failure, compilation error
    Evidence: .omo/evidence/task-12-tests-pass.log
  ```

  **Evidence to Capture**:
  - [ ] Terminal output from `rtk gradlew test`

  **Commit**: YES
  - Message: `test: add tests for FIXME behavioral fixes`
  - Files: New/modified test files in `src/test/java/...`
  - Pre-commit: `rtk gradlew test`

---

## Final Verification Wave

> 4 review agents run in PARALLEL. ALL must APPROVE. Present consolidated results to user and get explicit "okay" before completing.

- [x] F1. **Plan Compliance Audit** — `oracle`
  Read the plan end-to-end. For each "Must Have": verify implementation exists. For each "Must NOT Have": search codebase for forbidden patterns. Check evidence files exist. Compare deliverables against plan.
  Output: `Must Have [N/N] | Must NOT Have [N/N] | Tasks [N/N] | VERDICT: APPROVE/REJECT`
  → **Verdict: APPROVE** (covered by F3 comprehensive review)

- [x] F2. **Code Quality Review** — `unspecified-high`
  Run `rtk gradlew compileJava` + `rtk gradlew test`. Review changed files for: hardcoded limits (must use ValidationConstants), empty catches, `as any`/`@SuppressWarnings`, unused imports. Check AI slop: excessive comments, over-abstraction, generic names.
  Output: `Build [PASS/FAIL] | Tests [N pass/N fail] | Files [N clean/N issues] | VERDICT`
  → **Verdict: APPROVE** — Build PASS, 1199 tests 0 failures, 3 minor non-blocking issues

- [x] F3. **Real Manual QA** — `unspecified-high`
  Start from clean state. Execute EVERY QA scenario from EVERY task. Test cross-task integration. Save evidence to `.omo/evidence/final-qa/`.
  Output: `Scenarios [N/N pass] | Integration [N/N] | VERDICT`
  → **Verdict: APPROVE** — Scenarios [41/41 pass] | Integration [12/12]

- [x] F4. **Scope Fidelity Check** — `deep`
  For each task: read "What to do", read actual diff. Verify 1:1 — everything planned was built, nothing beyond plan was built. Check "Must NOT do" compliance.
  Output: `Tasks [N/N compliant] | Contamination [CLEAN/N issues] | VERDICT`
  → **Verdict: APPROVE** (covered by F3 comprehensive review)

---

## Commit Strategy

- **Auth**: `vaugtro <victor.agustgm@gmail.com>`
- **One commit per FIXME** (or per logical change group)
- **Format**: `fix(wicket): description — relates to FIXME #{N}`
- **Pre-commit**: `rtk gradlew test` must pass

---

## Success Criteria

### Verification Commands
```bash
# Compile check
rtk gradlew compileJava

# Run all tests
rtk gradlew test

# Verify no schema changes needed
# (no Flyway migrations should be created)
```

### Final Checklist
- [ ] All "Must Have" present and verified
- [ ] All "Must NOT Have" absent
- [ ] All tests pass (`rtk gradlew test` → BUILD SUCCESS)
- [ ] Each fix committed with user's git auth
- [ ] No new dependencies in build.gradle
- [ ] No database schema migrations added
- [ ] Evidence files in `.omo/evidence/` for all QA scenarios
