# Specification — Cliente Management System Refactor

## 1. Overview

This document defines the target architecture and design rules for refactoring the Cliente (Client) management system. The system manages two client types (Fisico/PF and Juridico/PJ) with their addresses, providing both a REST API (Spring MVC) and a web UI (Apache Wicket 7).

## 2. Core Principles

- **SOLID**: Every class has one responsibility; abstractions over implementations; interfaces are minimal.
- **Wicket Best Practices**: `@SpringBean` never with `transient`; `LoadableDetachableModel` for entities; `CompoundPropertyModel` for forms; components are self-contained; no raw objects passed to components.
- **Spring Best Practices**: Constructor injection; `@Transactional` at service layer; `@Valid` on public API boundaries.
- **DRY**: Zero structural duplication between Fisico and Juridico implementations.
- **Clean Code**: Meaningful names; no magic literals; methods under 30 lines; no commented-out code.

## 3. Target Architecture

```
Controller (REST)       Wicket Pages/Panels
       |                       |
  Service Interface        Service Interface
       |                       |
  Service Impl (w/ @Valid)
       |
  Repository (JPA)
       |
  Entity (JPA)
```

### 3.1 Layer Rules

- **Controller layer**: Thin — validates input (`@Valid`), delegates to service, returns HTTP response.
- **Wicket layer**: Thin — builds components, delegates to service via `@SpringBean`.
- **Service layer**: Contains all business logic, transaction boundaries, validation.
- **Repository layer**: Only standard JPA queries; no business logic.

## 4. Service Layer Specification

### 4.1 AbstractClienteService

```java
public abstract class AbstractClienteService<T extends Cliente, R extends JpaRepository<T, Long>> {
    protected final R repository;

    // Shared CRUD: findModelById, ensureIsActive, activate, inactivate, delete, hardDelete, count
    // NO findAll — each concrete service owns its DTO mapping
    // activate/inactivate are FINAL — no override by subclasses
}
```

Rules:
- `activate(id)` and `inactivate(id)` are `final` — subclasses MUST NOT override them. The abstract base provides the canonical implementation.
- No `EntityManager` usage — Spring's transaction management handles the persistence context.
- `delete(id)` delegates to `inactivate(id)` (soft delete).

### 4.2 Query/Lifecycle Interface Pattern

Each client type has two segregated interfaces (already exist, must be preserved):

```
Cliente{X}QueryService     — read operations (findById, findAll, findAllActive, etc.)
Cliente{X}LifecycleService — write operations (create, update, activate, inactivate, delete, hardDelete)
```

These are combined into a marker interface:

```
Cliente{X}Service extends Cliente{X}QueryService, Cliente{X}LifecycleService
```

### 4.3 Concrete Service Rules

- `create(Cliente{X}CreateRequest)`: MUST validate uniqueness, save entity, create addresses via `EnderecoService`, return response DTO.
- `update(id, Cliente{X}UpdateRequest)`: MUST find entity, ensure active, apply partial update via mapper, save.
- `activate/inactivate`: NOT overridden (use base class `final` methods).
- `findById(id)`: Return response DTO.
- `findAll(pageable)`: Return page of list response DTOs.
- All public methods MUST accept and return DTOs, never entities.

### 4.4 Fixes Required

1. **ClienteJuridicoServiceImpl.create()**: Add address handling (iterate `request.enderecos()`, call `enderecoService.createForCliente`). Currently missing — this is a bug.
2. **ClienteFisicoServiceImpl**: Remove `EntityManager` usage (flush/detach in activate/inactivate, clear in findById). The abstract base handles these correctly.
3. **ClienteFisicoServiceImpl.activate/inactivate**: Remove `@CacheEvict` — caching concerns are cross-cutting and should be handled via AOP or removed. If cache is needed, apply at a higher level.
4. **ClienteJuridicoServiceImpl**: Add `EnderecoService` dependency, wire it in create method.

## 5. Wicket Layer Specification

### 5.1 Shared Component Architecture

Eliminate duplication between Fisico and Juridico Wicket components by extracting shared abstractions:

```
ClienteDataView<T extends Cliente{X}ListResponse>     (shared DataView logic)
ClienteDataProvider<T extends Cliente{X}ListResponse> (shared DataProvider logic)
AbstractClienteRowUpdateForm                           (shared row edit form)
```

BUT — careful not to over-abstract. Wicket components are inherently type-specific due to HTML templates and field names. The priority is:

1. **Extract shared logic** where types allow (DataProvider, DataView population patterns).
2. **Keep type-specific markup and field bindings** in concrete components.

### 5.2 Modal Address Table

The address table inside `ClienteFisicoCreateModal` and `ClienteJuridicoCreateModal` is structurally identical. Extract into a reusable component:

```
shared/EnderecoCreateTablePanel.java + .html
```

Responsibilities:
- Renders a table with address fields (logradouro, numero, bairro, cep, cidade, estado, telefone, complemento, principal).
- Add/remove row buttons.
- Provides `List<EnderecoCreateFormModel>` to the parent form.
- Uses `ListView<EnderecoCreateFormModel>` internally.

### 5.3 Form Model Consolidation

- `ClienteFisicoCreateFormModel` and `ClienteJuridicoCreateFormModel` MUST extend a base `ClienteCreateFormModel` if they share fields (nome/razaoSocial, email).
- Keep type-specific fields (cpf/rg vs cnpj/inscricaoEstadual) in concrete models.
- `EnderecoCreateFormModel` stays as-is (already shared).

### 5.4 Submit Handler Rules

- `onSubmit` in modals: map form model → DTO → call service → reset form → show toast.
- NO business logic in `onSubmit` — only orchestration.
- Date strings parsed in `onSubmit` before DTO construction.

## 6. Controller Layer Specification

### 6.1 Current State (already good)

- Thin controllers delegating to services.
- `@Valid` on request bodies.
- Proper HTTP status codes.
- Swagger annotations.

### 6.2 Changes Required

- None structurally. Endpoints and contracts remain unchanged.

## 7. Repository Layer

### 7.1 Current State (already good)

- Standard Spring Data JPA repositories.
- Custom queries for findByCpf, findByCnpj, findByEstaAtivoTrue.

### 7.2 Changes Required

- None. Repositories are minimal and correctly scoped.

## 8. Entity Layer

### 8.1 Notes

- `Cliente` is the base class with `@Inheritance(strategy = JOINED)`.
- `ClienteFisico` and `ClienteJuridico` extend it.
- `Endereco` has a `@ManyToOne` to `Cliente`.

No structural changes needed.

## 9. Migration Guide

1. Apply service layer fixes first (address handling, EntityManager removal, final methods).
2. Compile and verify tests pass.
3. Extract shared Wicket components.
4. Update modals to use shared components.
5. Run full test suite.
6. Delete `GEMINI.md` and `TODO.md`.

## 10. Non-Goals (Out of Scope)

- Changing database schema or migrations.
- Changing REST API endpoints or response formats.
- Changing entity mappings.
- Adding new features.
- Upgrading Wicket or Spring versions.
- Changing the Factory pattern (kept as-is, though noted as possibly redundant).
- Refactoring JasperReportService interface (noted but deferred).
