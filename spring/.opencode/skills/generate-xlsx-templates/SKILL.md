---
name: generate-xlsx-templates
description: Use when you need to create filled .xlsx test data (good and faulty rows) based on existing template files and validation rules
---

# Generate XLSX Test Templates

## Overview

Read a .xlsx template for column headers, inspect the Java import and validation code, then generate both a valid ("
bom") and invalid ("ruim") filled template with 5 rows each.

## Steps

1. **Read template headers** — Use openpyxl to extract column names from the existing `.xlsx` template (row 1).
2. **Check import mapping** — Find the `import*` method in `XlsxFileServiceImpl.java` to map column index → field name.
3. **Check DTO validation** — Read the `*CreateRequest` DTO records for `@NotNull`, `@CPF`, `@CNPJ`, `@ValidCEP`,
   `@ValidTelefone`, `@Size`, `@PastOrPresent` annotations.
4. **Check custom validators** — Read `CEPValidator.java` and `TelefoneValidator.java` for exact patterns.
5. **Generate "bom" template** — 5 rows, every column valid, realistic Brazilian data (CPF/CNPJ that pass Hibernate
   validator, CEP in `xxxxx-xxx` format, phone in `(XX) 9XXXX-XXXX` format, past dates in `yyyy-MM-dd`).
6. **Generate "ruim" template** — 5 rows, each with a **different** validation error (e.g., CPF inválido, campo
   obrigatório vazio, CEP mal formatado, telefone inválido, data futura).
7. **Preserve template styling** — Use the same blue header fill (`4472C4`) and white bold font as the originals.
8. **Use `uv run openpyxl`** — Project is Java/Spring, so use `uv` for Python dependency management without adding to
   the Java build.

## Reference

### Cell helper behavior (from XlsxFileServiceImpl)

| Destination | Getter                     | Behavior                                                                    |
|-------------|----------------------------|-----------------------------------------------------------------------------|
| String      | `getCellString(row, idx)`  | Reads STRING/NUMERIC/BOOLEAN cells, trims                                   |
| Long        | `getCellLong(row, idx)`    | From NUMERIC: `(long) double`. From STRING: strips non-digits then parses   |
| Boolean     | `getCellBoolean(row, idx)` | `"sim"/"true"/"s"/"1"` → `true`, numeric `1` → `true`                       |
| LocalDate   | `parseDate(value)`         | Tries `yyyy-MM-dd`, then `dd/MM/yyyy`, then `dd-MM-yyyy`, then `yyyy/MM/dd` |

### Validation quick ref

| Field             | Validator               | Rule                                                                         |
|-------------------|-------------------------|------------------------------------------------------------------------------|
| CPF               | `@CPF`                  | Hibernate `org.hibernate.validator.constraints.br.CPF` (valid check digits)  |
| CNPJ              | `@CNPJ`                 | Hibernate `org.hibernate.validator.constraints.br.CNPJ` (valid check digits) |
| CEP               | `@ValidCEP`             | `^\d{5}-?\d{3}$`                                                             |
| Telefone          | `@ValidTelefone`        | 10-digit: `^[1-9]{2}[2-8]\d{7}$` or 11-digit: `^[1-9]{2}9\d{8}$`             |
| Nome/Razão Social | `@Size(min=3, max=150)` | 3–150 chars, `@NotBlank`                                                     |
| Data              | `@PastOrPresent`        | No future dates                                                              |

## Anti-patterns

- **Using `s/n` for número** — `getCellLong` strips non-digits, `"s/n"` → `""` → `null` → fails `@NotNull`. Use `"0"`
  for "no number."
- **Negative number for número** — `getCellLong` strips `-` from strings, so `"-5"` → `"5"` → `5L` and silently passes
  validation.
- **Generating CPFs that fail the validator** — `123.456.789-01` is well-formed but fails Hibernate's check-digit
  algorithm. Use verified valid CPFs (`529.982.247-25`, `088.945.771-68`, etc.).
- **Assuming `count` is always `itemsPerPage`** — Wicket's `iterator(first, count)` shrinks `count` on the last page.
  Always use stored `itemsPerPage` for `PageRequest`.
