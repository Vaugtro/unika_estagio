# Wicket UI Changes — Endereço UF/Municipio Dependent Dropdowns

## What Changed

The Endereco form replaced a free-text Cidade input with a **dependent dropdown pair**:
UF (estado) → Municipio. Both come from the IBGE tables, so values are
validated against the database instead of free text.

---

## Key UI Pattern

### 1. Estado Dropdown (first)

- **Source**: `GET /v1/unidades-federativas` → list of `{ sigla, nome }`
- **Sorted by**: `nome`
- **Required**: yes — `setRequired(true)`
- **Label**: `"Estado"`
- **Null option**: `"Selecione..."` (the `null` key in `WicketApplication.properties`)
- **On change**: triggers re-population of the Municipio dropdown choices

### 2. Municipio Dropdown (second, depends on Estado)

- **Source**: `GET /v1/municipios?ufSigla={sigla}` → list of `{ id, nome, ufSigla }`
- **Filtered by**: the selected UF's sigla
- **Choices empty** when no UF selected
- **Required**: yes — `setRequired(true)`
- **Label**: `"Município"`
- **Value type**: `number` (IBGE municipio code, not string)

### 3. Form Submission

- **Request body** sends `municipioId` (number, IBGE code), **not** `estado` + `cidade`
- `estado` (sigla) is a transient UI helper — not sent to the API

### 4. Edit Pre-population

- Read `municipioId` from the response DTO
- Set the Estado dropdown to the matching UF
- Then set the Municipio dropdown to the matching `municipioId`
- Display `cidade` + `estado` from response as read-only text

### 5. Validation Messages (Portuguese)

```
null = Selecione...
Required = ${label} é necessário.
```

### 6. All form fields need explicit labels

Every form field must have a `.setLabel()` call. Validation messages use the
label as `${label}` placeholder.

---

## Files Changed (for reference)

| File | Change |
|------|--------|
| `wicket/model/EnderecoCreateFormModel.java` | Removed `cidade`, added `municipioId`; kept `estado` as transient |
| `wicket/component/shared/EnderecoCreateTablePanel.java` | Removed Cidade text field; added Estado + Municipio dropdown pair with dependent refresh |
| `wicket/component/shared/EnderecoCreateTablePanel.html` | Removed Cidade `<td>` column |
| `wicket/mapper/EnderecoDtoMapper.java` | Changed `formModel.getCidade()` → `formModel.getMunicipioId()` |
| `wicket/builder/FormFieldBuilder.java` | Added `label(String)` method → calls `field.setLabel(Model.of(label))` |
| `wicket/application/WicketApplication.properties` | New file: `null=Selecione...`, `Required=${label} é necessário.` |

---

## What Angular Should Replicate

1. Replace the free-text Cidade field with a Municipio `<select>` that is:
   - Disabled/empty until a UF is chosen
   - Populated via `GET /v1/municipios?ufSigla={sigla}` when UF changes
2. Keep the UF dropdown (Estado) — populate via `GET /v1/unidades-federativas`
3. Send `municipioId` (number) in create/update payloads instead of `estado` + `cidade`
4. On edit: read `municipioId` from response, use it to restore both dropdown selections
5. Show `"Selecione..."` as the blank option on both dropdowns
6. Show required-validation messages in Portuguese: `"Campo é necessário."`
7. Assign explicit labels to every field for validation messages
