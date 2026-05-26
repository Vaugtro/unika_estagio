# Handoff: Wicket FluentBuilder Refactor

**Session Date**: 2026-05-26
**Status**: Plan generated, awaiting Oracle phase 2 + self-review + user approval
**Plan Location**: `.omo/plans/wicket-fluent-builder-refactor.md`
**Draft Location**: `.omo/drafts/wicket-fluent-builder-refactor.md`

---

## Where We Are

This session completed the **full interview + plan generation** for a large-scale Wicket frontend refactor. The plan file is written and contains 29 implementation tasks across 4 waves + 4 final verification tasks.

### Completed Steps
1. ✅ Explored full Wicket codebase (31 Java + 14 HTML files)
2. ✅ Analyzed SOLID violations, DRY violations, inline JS, duplicate error handling
3. ✅ Interviewed user on scope, risk tolerance, test strategy, branch availability
4. ✅ Consulted Metis for gap analysis (identified 6 major gaps, all addressed)
5. ✅ Oracle phase 1 passed: CHECK [5/5] PASS | VERDICT: GO
6. ✅ Generated complete work plan to `.omo/plans/wicket-fluent-builder-refactor.md`

### Pending Steps
1. ⏳ Oracle phase 2 verification (plan compliance check) — attempted but aborted due to model issues
2. ⏳ Self-review: classify gaps
3. ⏳ Present summary to user with auto-resolved items and decisions
4. ⏳ Ask user about high accuracy mode (Momus review)
5. ⏳ Oracle phase 3 verification (if Momus loop used)
6. ⏳ Delete draft and guide user to `/start-work`

---

## Key Decisions Made

| Decision | Value |
|----------|-------|
| Execution style | Single pass (all files in one branch) |
| Scope | All 31 Java + 14 HTML files in `wicket/` |
| Test strategy | Tests-after refactor (Option B) — no pre-existing Wicket coverage |
| Feature branch | Already available |
| JS/CSS extraction | Yes — inline JS extracted to utilities and external resource files |
| SOLID principles | Yes — explicit requirement |

---

## Plan Structure Summary

### Wave 1: Foundation (6 tasks, can all run in parallel)
1. WicketTester dependency + base test class
2. FormFieldBuilder API + prototype on EnderecoCreateTablePanel
3. AttributeModifierBuilder + ComponentAttributeBuilder
4. JavaScriptUtils centralized utility
5. ErrorHandler centralized utility
6. DtoMapper foundation (Endereco, ClienteFisico, ClienteJuridico)

### Wave 2: Structural Refactoring (13 tasks, depends on Wave 1 builders)
7. EnderecoCreateTablePanel with FormFieldBuilder
8. ClienteFisicoCreateModal with builders + DtoMapper
9. ClienteJuridicoCreateModal with builders + DtoMapper
10. ClienteFisicoDetalhePage
11. ClienteJuridicoDetalhePage
12. ClienteFisicoRowUpdateForm with builders
13. ClienteJuridicoRowUpdateForm with builders
14. ClienteFisicoDataView with builders
15. ClienteJuridicoDataView with builders
16. Create unified generic ClientesTablePanel<T>
17. ClientesFisicosTablePanel extends generic panel
18. ClientesJuridicosTablePanel extends generic panel
19. Split EnderecoListViewPanel SRP

### Wave 3: Behavioral Extraction (5 tasks, depends on Wave 2)
20. Extract inline JS from Java components to JavaScriptUtils
21. Extract inline JS from HTML templates to external resource files
22. Extract error handling in all modals to ErrorHandler
23. Extract DTO construction in all modals to DtoMappers
24. Refactor ImportModal + ExportModal with builders

### Wave 4: Tests (5 tasks, depends on Waves 1-3)
25. EnderecoCreateTablePanelTest
26. CreateModalTest (Fisico + Juridico)
27. TablePanelTest (Fisico + Juridico)
28. DetalhePageTest (Fisico + Juridico)
29. EnderecoListViewPanelTest

### Wave FINAL: Verification (4 parallel reviews)
F1. Plan compliance audit (oracle)
F2. Code quality review (unspecified-high)
F3. Real manual QA (unspecified-high)
F4. Scope fidelity check (deep)

---

## Critical Guardrails

1. **ALL `wicket:id` values MUST stay identical** — HTML template compatibility
2. **ALL `findParent()` call chains MUST be preserved** — component hierarchy compatibility
3. **@Serial fields preserved** on all Wicket components
4. **@SpringBean injection patterns unchanged**
5. **Do NOT touch**: AbstractClienteDataProvider, AbstractClienteDataView, ValidationFeedback, ByteArrayResourceStream, WicketApplication, BasePage, FormModel classes
6. **Do NOT change** `onComponentTag()` behavior in RowUpdateForm classes
7. **Do NOT introduce anonymous inner classes** inside FluentBuilder chains (breaks serialization)

---

## Key Findings from Exploration

### Worst Offenders (by component add operations)
| File | Component Adds | Lines | Primary Issue |
|------|---------------|-------|--------------|
| EnderecoCreateTablePanel.java | 29 | 186 | Severe DRY — repeats 8-step field setup ~10 times |
| EnderecoListViewPanel.java | 25 | 379 | SRP violation — UI + DTO + file + error handling |
| ClienteFisicoCreateModal.java | 23 | 200 | Business logic inline (date parse, DTO build, service call) |
| ClienteJuridicoCreateModal.java | 18 | ~180 | Same as above |
| ClientesFisicosTablePanel.java | 10 | 218 | 90% duplicate of JuridicosTablePanel |
| ClientesJuridicosTablePanel.java | 10 | ~220 | 90% duplicate of FisicosTablePanel |

### Test Coverage
- **0 Wicket-specific tests** exist
- No `WicketTester` usage anywhere
- Test infrastructure must be added as Wave 1, Task 1

---

## Metis Review: Key Gaps Addressed

1. **"Single pass for 31 files is risky"** → Organized into 4 dependency-ordered waves, all in same branch
2. **"FluentBuilder API not defined"** → Prototype on EnderecoCreateTablePanel first (Task 2), then propagate
3. **"wicket:id changes break HTML silently"** → Hard guardrail: IDs must stay identical
4. **"findParent() chains break if hierarchy changes"** → Hard guardrail: preserve tree nesting
5. **"HTML templates have inline JS too"** → Included HTML JS extraction in Wave 3, Task 21
6. **"Some files should not be touched"** → Explicit OUT list in Must NOT Have

---

## Files Created in This Session

- `.omo/drafts/wicket-fluent-builder-refactor.md` — working draft (should be deleted after plan finalization)
- `.omo/plans/wicket-fluent-builder-refactor.md` — the complete work plan (1382 lines)

---

## Next Steps to Complete Planning

When resuming this session:

1. **Run Oracle phase 2** (plan compliance verification):
   ```
   task(subagent_type="oracle", run_in_background=false,
        prompt="Verify .omo/plans/wicket-fluent-builder-refactor.md ...")
   ```
   The user mentioned they may need to reopen with a different Oracle model if it aborts.

2. **Self-review gaps** — classify any remaining gaps as critical/minor/ambiguous

3. **Present summary** to user with:
   - Key decisions made
   - Scope IN/OUT
   - Guardrails applied
   - Auto-resolved items
   - Defaults applied
   - Any decisions still needed

4. **Ask high accuracy question** — "Would you like Momus review for extra rigor?"

5. **If high accuracy**: Submit to Momus and loop until OKAY

6. **Oracle phase 3** (final readiness check)

7. **Delete draft** and guide user to `/start-work wicket-fluent-builder-refactor`

---

## How to Resume Execution

When you're ready to execute (not just plan), run:

```
/start-work wicket-fluent-builder-refactor
```

This will:
1. Register the plan as the active boulder
2. Begin execution starting with Wave 1, Task 1
3. Track progress across sessions
4. Enable automatic continuation if interrupted

---

## Important Notes for Continuation

- The plan uses **bare-number task labels** (`1.`, `2.`, `F1.`) — this is required for `/start-work` progress tracking
- The plan expects evidence files in `.omo/evidence/task-{N}-{scenario-slug}.{ext}`
- All QA scenarios are agent-executable (Bash commands) — no human intervention required
- The feature branch is already available (per user); verify it's checked out before starting
- `rtk gradlew compileJava` is the primary compilation command
- `rtk gradlew test` runs tests
- `rtk gradlew buildWar` builds the deployable WAR
