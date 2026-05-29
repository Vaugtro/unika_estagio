# Import REST Endpoints

Three new POST endpoints that accept an XLSX file and return the same structured
result as Wicket's internal import.

### Endpoints

| Method | Path | Description |
|--------|------|-------------|
| POST | `/v1/export/clientes/fisicos/import` | Import clientes físicos + endereços |
| POST | `/v1/export/clientes/juridicos/import` | Import clientes jurídicos + endereços |
| POST | `/v1/export/enderecos/import?clienteId={id}` | Import endereços for a specific cliente |

### Request

All endpoints accept `multipart/form-data` with a single field:

```
file: <binary .xlsx>
```

### Response (`ImportResult`)

```typescript
interface ImportResult {
  successCount: number;
  errors: string[];
}
```

Where `errors` contains messages like:
- `"Linha 5: CPF inválido"`
- `"Linha 5: O campo CEP é obrigatório"`
- `"Linha 12: CNPJ já cadastrado"`
- etc.

### UI Patterns (from Wicket)

| Scenario | Wicket behavior | Angular should |
|----------|----------------|----------------|
| All rows succeed | Toast "success": `"N registros importados com sucesso!"` | Show success message with count |
| Some rows fail | Toast "warning": `"N registros | Linhas com erro: <first>"` | Show warning, list all errors |
| successCount=0, errors non-empty | Toast "warning" | Show all errors, no success |
| HTTP error (network, etc.) | toast "error": "Erro inesperado. Tente novamente." | Handle HTTP error gracefully |

### Key Behaviors

1. **Partial import**: On success, some rows may have been imported even if `errors` is
   non-empty. Each row is processed in its own transaction — partial success is expected.
2. **Error messages** are the exact same strings as Wicket's import modal console errors
   (`console.error('Erros na importacao:')`).
3. **No page refresh** on errors — the page only refreshes on full success (Wicket calls
   `window.location.reload()` after 3s when `errors` is empty).
