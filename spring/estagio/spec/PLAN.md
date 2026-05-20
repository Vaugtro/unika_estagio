# Refactoring Plan

## Phase 1: Service Layer

### S1. Make `activate`/`inactivate` final in `AbstractClienteService`
- Remove `@CacheEvict` and `EntityManager.flush/detach` from `ClienteFisicoServiceImpl` overrides
- Remove the overridden methods entirely — base class provides the canonical implementation
- **_File:_** `AbstractClienteService.java`, `ClienteFisicoServiceImpl.java`

### S2. Add address handling to `ClienteJuridicoServiceImpl.create()`
- Inject `EnderecoService` dependency
- After saving the entity, iterate `request.enderecos()` and call `enderecoService.createForCliente()`
- Add validation (at least one endereco required, at least one principal)
- **_File:_** `ClienteJuridicoServiceImpl.java`

### S3. Remove `EntityManager` from `ClienteFisicoServiceImpl`
- Remove `entityManager.flush()` and `entityManager.detach()` from `activate`/`inactivate`
- Remove `entityManager.clear()` from `findById`
- Remove `EntityManager` field and constructor parameter
- **_File:_** `ClienteFisicoServiceImpl.java`

---

## Phase 2: Wicket Shared Components

### W1. Extract `EnderecoCreateTablePanel`
- New component at `wicket/component/shared/EnderecoCreateTablePanel.java` + `.html`
- Contains the `ListView<EnderecoCreateFormModel>` with all address fields
- Exposes add/remove row buttons
- Accepts `List<EnderecoCreateFormModel>` via constructor or model
- **_Files:_** `EnderecoCreateTablePanel.java`, `EnderecoCreateTablePanel.html` (NEW)

### W2. Update modals to use `EnderecoCreateTablePanel`
- Replace inline address table code in both `ClienteFisicoCreateModal` and `ClienteJuridicoCreateModal`
- Keep `enderecos` list in form models (unchanged)
- **_Files:_** `ClienteFisicoCreateModal.java`/`.html`, `ClienteJuridicoCreateModal.java`/`.html`

---

## Phase 3: Verification

### V1. Compile and test
```bash
./gradlew compileJava
./gradlew test
```

### V2. Delete project-guideline files
- Remove `GEMINI.md`
- Remove `TODO.md` (from estagio/ root)

---

## Task Dependencies

```
S1 ──→ S2 ──→ V1
  └──→ S3 ──→ V1
                 │
W1 ──→ W2 ──→ V1
                 │
                 └──→ V2
```
