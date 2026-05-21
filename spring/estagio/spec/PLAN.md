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

## Phase 2: Validation Redundancy

### V1. Create `ValidationConstants.java`
- Shared constants for all field lengths (RG, CPF, CNPJ, NOME, LOGRADOURO, etc.)
- **_File:_** `validation/ValidationConstants.java` (NEW)

### V2. Add `@Valid` to service method parameters
- Add `@Valid` to `create(Cliente{X}CreateRequest)` in both service implementations
- Add `@Valid` to `update(Long, Cliente{X}UpdateRequest)` in both service implementations
- **_Files:_** `ClienteFisicoServiceImpl.java`, `ClienteJuridicoServiceImpl.java`

### V3. Move sanitization from DTO to service layer
- Remove CPF `replaceAll("\\D", "")` from `ClienteFisicoCreateRequest` compact constructor
- Remove CEP/telefone sanitization from `EnderecoWithinClienteCreateRequest` compact constructor
- Add sanitization in `ClienteFisicoServiceImpl.create()` before persistence
- **_Files:_** `ClienteFisicoCreateRequest.java`, `EnderecoWithinClienteCreateRequest.java`, `ClienteFisicoServiceImpl.java`

### V4. Update Wicket validators to use `ValidationConstants`
- Replace magic length literals in both modals and row update forms
- E.g., `StringValidator.lengthBetween(ValidationConstants.RG_LENGTH_MIN, ValidationConstants.RG_LENGTH_MAX)`
- **_Files:_** `ClienteFisicoCreateModal.java`, `ClienteJuridicoCreateModal.java`
  `ClienteFisicoRowUpdateForm.java`, `ClienteJuridicoRowUpdateForm.java`

---

## Phase 3: Wicket Shared Components

### W1. Extract `EnderecoCreateTablePanel`
- New component at `wicket/component/shared/EnderecoCreateTablePanel.java` + `.html`
- Contains the `ListView<EnderecoCreateFormModel>` with all address fields
- Exposes add/remove row buttons
- Accepts `List<EnderecoCreateFormModel>` via model
- **_Files:_** `EnderecoCreateTablePanel.java`, `EnderecoCreateTablePanel.html` (NEW)

### W2. Update modals to use `EnderecoCreateTablePanel`
- Replace inline address table code in both `ClienteFisicoCreateModal` and `ClienteJuridicoCreateModal`
- Keep `enderecos` list in form models (unchanged)
- **_Files:_** `ClienteFisicoCreateModal.java`/`.html`, `ClienteJuridicoCreateModal.java`/`.html`

---

## Phase 4: Verification

### T1. Dubug
```bash
./gradlew compileJava
./gradlew test
```

### T2. Delete project-guideline files
- Remove `GEMINI.md`
- Remove `TODO.md` (from estagio/ root)

---

## Task Dependencies

```
S1 ──→ S2 ──→ V1 ──→ V2 ──→ V3 ──→ V4
  └──→ S3 ──→ ↑                        │
                                        │
                              W1 ──→ W2 ┘
                                        │
                                  T1 ──→ T2
```
