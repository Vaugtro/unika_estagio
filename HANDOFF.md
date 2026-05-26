HANDOFF CONTEXT
===============

USER REQUESTS (AS-IS)
---------------------
- Fix 10 FIXME items in the Spring Boot + Wicket client management application (ClienteFisico + ClienteJuridico)
- "Everytime a fix happens, a commit with my auth must be created" — git auth: `vaugtro <victor.agustgm@gmail.com>`
- Fixes apply to BOTH ClienteFisico and ClienteJuridico
- One commit per FIXME (or per logical change group) — format: `fix(wicket): description — relates to FIXME #{N}`
- Validation: inline feedback for fields, toast for form-level/non-input-scope only
- Edit modal scope: only replace inline fields (nome/razaoSocial, email) — status toggle stays in table row
- Keep FisicoEditModal and JuridicoEditModal separate (no premature abstraction)
- Do NOT change REST controllers or service business logic unless required
- Tests after implementation (JUnit 5 + Mockito)
- Every AJAX component change MUST call: `target.appendJavaScript("if(typeof lucide !== 'undefined') lucide.createIcons();");`
- Every AJAX form re-render MUST re-apply masks: `if(typeof $ !== 'undefined' && $.fn.mask) $('[data-mask]').each(function(){$(this).mask($(this).data('mask'));});`
- Always catch `BusinessException` in AJAX event handlers — service layer throws for validation failures
- When toggling status from DataView row, refresh the entire `tableContainer` (not just the row) because captured local variables become stale after the first DB update

GOAL
----
The estagio-fixes plan is 100% complete. All 10 FIXME items fixed, 7 commits, 1199 tests passing, Final Wave approved. Ready for next work items.

WORK COMPLETED
--------------
- Made Telefone optional: setRequired(false) in EnderecoCreateTablePanel, removed @NotBlank from DTOs, set nullable=true in entity
- Changed date inputs from HTML5 type="date" to text inputs with data-mask="99/99/9999" and DD/MM/YYYY parsing via DateTimeFormatter
- Added data-mask="99.999.999-9" and PatternValidator for RG field in ClienteFisicoCreateModal
- Added PatternValidator for telefone field with Brazilian phone format regex
- Created ClienteFisicoEditModal.java and .html (nome + email fields, save/cancel, pre-populated data)
- Created ClienteJuridicoEditModal.java and .html (mirror of Fisico)
- Rewrote both DataViews (Fisico/Juridico) to use read-only labels + toggleBtn + editarBtn + detalhesBtn
- Added refreshTable() to both TablePanels with mask re-apply and lucide.createIcons()
- Centralized error handling in ValidationFeedback.handleFormError(): instanceof checks for DataIntegrityViolationException, BusinessException, generic fallback
- Added input preservation via visitFormComponents in handleFormError() — saves converted input to model before re-render
- Deduplicated validation: toast shows generic "Corrija os campos destacados." only, inline labels show per-field errors
- Enforced principal constraint: button disabled + tooltip when size<=1, service properly demotes/promotes
- Fixed hardDelete cascade: demotes all enderecos' principal=false before delete
- Verified delete button visibility (hidden when active, shown when inactive)
- Added 4 test classes: EnderecoServiceImplTest, EnderecoRequestValidationTest, DateParsingTest, RGFormatterTest
- Mask re-apply on AJAX events in both handleFormError() and refreshTable()
- $(document).ready() replaced with shown.bs.modal event for modal mask initialization

CURRENT STATE
-------------
- All 16/16 tasks in plan complete
- git status: clean (all changes committed)
- 7 commits by vaugtro <victor.agustgm@gmail.com> on master since c532327
- rtk gradlew compileJava: BUILD SUCCESSFUL
- rtk gradlew test: 1199 tests, 0 failures, BUILD SUCCESSFUL
- Final Verification Wave: ALL APPROVE (F1-F4)

PENDING TASKS
-------------
- No pending tasks from the plan
- Potential next items (not yet discussed): V7 Flyway migration with CHECK constraints, TODO.md row refresh issue

KEY FILES
---------
- spring/estagio/src/main/java/.../wicket/component/modal/ClienteFisicoEditModal.java — Edit modal for Fisico (nome + email)
- spring/estagio/src/main/java/.../wicket/component/modal/ClienteJuridicoEditModal.java — Edit modal for Juridico (razaoSocial + email)
- spring/estagio/src/main/java/.../wicket/component/ValidationFeedback.java — Centralized error handling + input preservation + mask re-apply
- spring/estagio/src/main/java/.../wicket/component/shared/EnderecoListViewPanel.java — Principal constraint + friendly error messages
- spring/estagio/src/main/java/.../wicket/component/dataview/ClienteFisicoDataView.java — Read-only row + toggle/editar/detalhes buttons
- spring/estagio/src/main/java/.../wicket/component/table/ClientesFisicosTablePanel.java — refreshTable() with masks + lucide icons
- spring/estagio/src/main/java/.../service/impl/EnderecoServiceImpl.java — setAsPrincipal() with correct demote/promote logic
- spring/estagio/src/main/java/.../service/AbstractClienteService.java — hardDelete() with endereco principal demotion
- spring/.omo/plans/estagio-fixes.md — Complete plan with all 16 tasks marked done
- .omo/notepads/estagio-fixes/learnings.md — Documented patterns for AJAX mask init, lucide icons, toggle refresh

IMPORTANT DECISIONS
-------------------
- Telefone optional requires changes at 4 levels: Wicket setRequired(false), DTO @ValidTelefone (no @NotNull), entity nullable=true
- Edit modals include ONLY nome/razaoSocial + email fields (no status, no endereco editing)
- Status toggle (Ativar/Inativar) stays in table row as a clickable AjaxLink badge, not in the modal
- handleFormError() is the centralized point for mask re-apply and input preservation — covers ALL modals
- refreshTable() in both table panels is centralized for table refresh + lucide icons + mask re-apply
- hardDelete() must demote all enderecos' principal=false before cascade to bypass @PreRemove constraint
- unspecified-high, visual-engineering, deep categories FAIL (route to experimental Google models) — use quick or oracle instead

EXPLICIT CONSTRAINTS
--------------------
- "Everytime a fix happens, a commit with my auth must be created"
- "Telefone and RG must be templated properly"
- "Calendar must be default DD/MM/YYYY"
- "Modal must not show the same data from the input validations"
- "Tests after implementation"
- "Do NOT change REST controllers or service business logic unless required by the fix"
- "Do NOT create shared abstract edit modal component"
- "One commit per FIXME (or per logical change group)"
- "fix(wicket): description — relates to FIXME #{N}" (commit message format)
- "readd the ativo/inativo button like before, and remove from edit form"
- "Must refresh the component when editar button is used. a toast of success must be emitted too and the modal close. every modal must follow that behavior."
- "add the ajax lucide icons refresh too"

CONTEXT FOR CONTINUATION
------------------------
- Only `quick` category and `oracle` agent work reliably in this environment — other categories route to experimental models that produce no output
- Wicket HTML templates live alongside Java files in src/main/java, NOT in resources
- Use `rtk gradlew compileJava` / `rtk gradlew test` / `rtk gradlew buildWar` for building
- Docker compose mariadb for local DB: docker-compose up -d mariadb
- MapStruct generates mappers at compile time — check impl classes in build/generated
- If continuing with Flyway CHECK constraints migration, the base is V6 (V5 was the last ALTER), next is V7
- There is a new FIXME noted in spring/TODO.md: "After editing, wicket doesn't refresh the edited row." — user said to ignore for now
