---
name: testing-patterns
description: Use when writing tests for services, controllers, Wicket pages, or validators in a Spring Boot + Wicket project
---

# Testing Patterns

## Overview
Tests in this project use JUnit 5 + Mockito. Service tests are `@ExtendWith(MockitoExtension.class)`. Integration tests extend `AbstractIntegrationTest`. Wicket tests use `wicket-tester`.

## Test Data Requirements

**DTO requests must pass `@Valid` validation.** Since services are `@Validated`, all `create()` and `update()` calls trigger Bean Validation.

### CPF / CNPJ

Use real validated numbers — Hibernate `@CPF` and `@CNPJ` check digits:

```java
// ✅ Valid CPFs
"529.982.247-25"
"088.945.771-68"

// ✅ Valid CNPJ
"11.222.333/0001-81"

// ❌ Will fail validation
"123.456.789-01"  // check digits don't match
```

### Enderecos within Cliente Create

| Context | DTO type | Notes |
|---------|----------|-------|
| Within `ClienteFisicoCreateRequest` | `EnderecoWithinClienteCreateRequest` | No `clienteId` field |
| Within `ClienteJuridicoCreateRequest` | `EnderecoWithinClienteCreateRequest` | No `clienteId` field |
| Standalone `EnderecoService.create()` | `EnderecoCreateRequest` | Requires `@NotNull clienteId` |
| Update via `ClienteJuridicoUpdateRequest` | `EnderecoUpdateRequest` | Empty list fails `@NotEmpty` |

### Update Requests

When testing update via `ClienteJuridicoUpdateRequest`, provide a non-empty `enderecos` list (or validation groups would need adjustment):

```java
var endereco = new EnderecoUpdateRequest(
    "Av Paulista", 1000L, "01310-100", "Bela Vista",
    "(11) 99999-8888", "SP", "São Paulo", true, "Sala 1"
);
var updateReq = new ClienteJuridicoUpdateRequest(
    "New Name", "987654321",
    "email@example.com", LocalDate.of(2020, 1, 15), true, List.of(endereco)
);
```

## Field Validation Rules

| Field | Validator | Valid test value |
|-------|-----------|-----------------|
| CPF | `@CPF` | `529.982.247-25` |
| CNPJ | `@CNPJ` | `11.222.333/0001-81` |
| CEP | `@ValidCEP` | `01001-000` or `01001000` |
| Telefone | `@ValidTelefone` | `(11) 91234-5678` (11 digits) or `(11) 3234-5678` (10 digits, starts with 2-8) |
| RG | `@ValidRG` | `123456789` (just digits) |
| Data | `@PastOrPresent` | `1990-05-15` (past, ISO format) |
| Nome / Razão Social | `@Size(min=3, max=150)` | At least 3 chars |
| Inscrição Estadual | `@Pattern(regexp="^\d+$")` | Only digits |

## Mapper Verification

When verifying that `EnderecoService.create()` is called internally:

```java
// The service creates EnderecoCreateRequest objects with the saved cliente's ID
verify(enderecoService).create(any(EnderecoCreateRequest.class));
```

## Anti-patterns

- **Using invalid CPF/CNPJ like `123.456.789-01`** — passes formatting but fails the Hibernate validator check digits. Always use validated numbers.
- **Empty `List.of()` for `@NotEmpty` fields** — update DTOs with `@NotEmpty` on enderecos need at least one entry.
- **`null` for `@NotNull Long clienteId`** — standalone `EnderecoCreateRequest` requires non-null `clienteId`. Use `EnderecoWithinClienteCreateRequest` within creation contexts.
- **Assuming `@Valid` works on services without `@Validated`** — both are needed: `@Validated` on the class enables method validation, `@Valid` on the interface/implementation declares the constraint.
