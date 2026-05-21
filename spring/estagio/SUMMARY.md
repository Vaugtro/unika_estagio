# Why DTOs Cannot Replace FormModels in Wicket

## Problem

DTOs (`ClienteFisicoCreateRequest`, `ClienteFisicoResponse`, etc.) are Java **records** — immutable by design. Wicket's `CompoundPropertyModel` requires **mutable beans** (getters + setters) for two-way data binding.

## Two Specific Issues

### 1. Immutability

| Requirement | Record (DTO) | JavaBean (FormModel) |
|---|---|---|
| Wicket reads value | `record.cpf()` ✓ | `model.getCpf()` ✓ |
| Wicket writes value on submit | ❌ No setter | `model.setCpf(...)` ✓ |

`CompoundPropertyModel` calls `setXxx()` via reflection during form processing. Records have no setters → `PropertyNotWritableException`.

### 2. Type Mismatch

| Field | DTO Type | HTML Form Needs |
|---|---|---|
| `dataNascimento` / `dataCriacaoEmpresa` | `LocalDate` | `String` (HTML `<input type="date">` produces strings) |
| Endereços list | `List<EnderecoWithinClienteCreateRequest>` | `List<EnderecoCreateFormModel>` (mutable, with validation) |

DTOs would need custom converters for date fields — the String→LocalDate conversion happens in `onSubmit`, not during form editing. Endereço form models carry Wicket-specific state (validation messages, temporary values).

## Conclusion

FormModels are not unnecessary duplication — they serve as **mutable, Wicket-specialized adapters** between the HTTP form and the immutable DTO layer. The consolidation we did (extracting `ClienteCreateFormModel` base class) removes the duplication while preserving the adapter role.
