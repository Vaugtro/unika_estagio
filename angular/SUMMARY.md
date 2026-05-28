# Wicket vs Angular — Feature Gap Analysis

**Generated:** 2026-05-28
**Scope:** Wicket backend (`spring/estagio/`) vs Angular SPA (`angular/`)
**Goal:** Identify all Wicket UI features not yet implemented in the Angular frontend, ordered by priority.

---

## 1. Feature Comparison Matrix

| Feature | Wicket | Angular | Gap |
|---------|--------|---------|-----|
| Client listing (paginated) | ✅ | ✅ | — |
| Debounced search (300ms) | ✅ | ✅ | — |
| Inline row editing | ✅ | ✅ | — |
| Status toggle (activate/inactivate) | ✅ | ✅ | — |
| Client detail page | ✅ | ✅ | — |
| Client create (modal form) | ✅ | ✅ | — |
| Client export (PDF + XLSX) | ✅ | ✅ | — |
| Client import template download | ✅ | ✅ | — |
| ViaCEP auto-fill | ✅ | ✅ | — |
| Set address as principal | ✅ | ✅ | — |
| Delete address | ✅ | ✅ | — |
| Real-time validation | ✅ | ✅ | — |
| Toast notifications | ✅ | ✅ | — |
| **Client hard-delete** | ✅ | ❌ | Endpoint exists, no UI |
| **Edit client via modal** | ✅ | ❌ | Inline editing only |
| **Address edit** | ✅ | ❌ | Create + delete only |
| **Address export (PDF + XLSX)** | ✅ | ❌ | "Not available" |
| **Address import (XLSX + template)** | ✅ | ❌ | "Not available" |
| **Client import execution** | ✅ | ⚠️ | Stub: `// TODO` |

✅ = implemented | ❌ = missing | ⚠️ = partial (UI exists, backend not connected)

---

## 2. Detailed Gap Descriptions

### P0 — Client Hard-Delete

**Wicket:** `ClienteFisicoDetalhePage` / `ClienteJuridicoDetalhePage` show a red "Excluir Cliente" button when the client is inactive (`estaAtivo = false`). Calls `hardDelete()` on the service layer and redirects to `HomePage`. Handles `BusinessException`.

**Angular:** Detail pages (`FisicoDetailComponent`, `JuridicoDetailComponent`) are read-only. No delete button.

**API available:** `DELETE /v1/clientes/fisicos/{id}/permanent` and `DELETE /v1/clientes/juridicos/{id}/permanent` — already in generated API client, not consumed.

**Files involved:**
- `angular/src/app/cliente-fisico/pages/fisico-detail.component.*`
- `angular/src/app/cliente-juridico/pages/juridico-detail.component.*`

---

### P0 — Client Import Execution

**Wicket:** `ImportModal` is fully functional — file upload, submit via `AjaxButton`, reads `FileUpload` InputStream, calls `importData()`, shows toast with success count, auto-reloads after 3s.

**Angular:** `ImportDialogComponent` has the file picker UI and template download working, but the `import()` method is a stub:

```typescript
// TODO: implement when backend endpoint is ready
```

**API endponts:**
- `POST /v1/export/clientes/fisicos/template` (template download — working)
- `POST /v1/export/clientes/juridicos/template` (template download — working)
- Import endpoints exist in Wicket controllers but need to be verified for REST availability

**Files involved:**
- `angular/src/app/shared/components/import-dialog/import-dialog.component.ts`

---

### P1 — Edit Client Via Modal

**Wicket:** `ClienteFisicoEditModal` / `ClienteJuridicoEditModal` open a Bootstrap modal with editable fields (Nome, Email for fisico; Razao Social, Email for juridico). Uses `LoadableDetachableModel` for fresh data. On save, calls `update()` service, shows toast, removes modal from DOM.

**Angular:** Only has inline row editing (`FisicoRowFormComponent`, `JuridicoRowFormComponent`). No dedicated modal for editing all editable client fields.

**API available:** `PUT /v1/clientes/fisicos/{id}` and `PUT /v1/clientes/juridicos/{id}` — already in generated API client.

**Files involved:**
- `angular/src/app/cliente-fisico/components/fisico-create-dialog/` (reuse as base)
- `angular/src/app/cliente-juridico/components/juridico-create-dialog/` (reuse as base)

---

### P1 — Address Edit

**Wicket:** `EnderecoListViewPanel` has an edit button per row that opens a modal pre-populated with address data via `EnderecoCreateTablePanel`. Calls `enderecoService.update(endId, formModel)`.

**Angular:** `EnderecoTableComponent` has create and delete but no edit button.

**API available:** `PUT /v1/enderecos/{id}` — already in generated API client, not consumed.

**Files involved:**
- `angular/src/app/endereco/components/endereco-table/`
- `angular/src/app/endereco/components/endereco-create-dialog/` (reuse as edit dialog)
- `angular/src/app/shared/components/endereco-form/` (reuse address form)

---

### P2 — Address Export (PDF + XLSX)

**Wicket:** `EnderecoListViewPanel` has PDF and XLSX export buttons in the header. Calls `fileService.pdfEnderecos(clienteId)` and `fileService.xlsxEnderecos(clienteId)`.

**Angular:** `ExportDialogComponent` explicitly skips addresses:

```typescript
if (this.data.clienteType === 'endereco') {
  this.toastService.show('info', 'Exportação de PDF não disponível para endereços');
  return;
}
```

**API available:**
- `GET /v1/export/clientes/{clienteId}/enderecos/pdf`
- `GET /v1/export/clientes/{clienteId}/enderecos/xlsx`

Both already in generated API client (`ArquivoService`), not consumed.

**Files involved:**
- `angular/src/app/shared/components/export-dialog/export-dialog.component.ts`

---

### P2 — Address Import (XLSX + Template)

**Wicket:** `EnderecoListViewPanel` has a template download button and an import file upload form. Calls `fileService.templateEnderecosImport()` and `fileService.importEnderecos(clienteId, is)`.

**Angular:** No address import UI exists. The `ImportDialogComponent` only supports client types.

**API available:**
- `GET /v1/export/enderecos/template`
- Import endpoint exists in Wicket controllers

Already in generated API client (`ArquivoService`), not consumed.

**Files involved:**
- `angular/src/app/shared/components/import-dialog/import-dialog.component.ts`

---

## 3. API Endpoints Available But Not Consumed by Angular

| Endpoint | Service | Gap |
|----------|---------|-----|
| `DELETE /v1/clientes/fisicos/{id}` | `clientesFisicosSoftDelete` | No soft-delete UI |
| `DELETE /v1/clientes/fisicos/{id}/permanent` | `clientesFisicosHardDelete` | No hard-delete UI (P0) |
| `DELETE /v1/clientes/juridicos/{id}` | `clientesJuridicosHardDelete` | No delete UI |
| `PUT /v1/clientes/fisicos/{id}` | `clientesFisicosUpdate` | Only used inline (P1) |
| `PUT /v1/clientes/juridicos/{id}` | `clientesJuridicosUpdate` | Only used inline (P1) |
| `PUT /v1/enderecos/{id}` | `enderecosUpdate` | No edit UI (P1) |
| `GET /v1/export/clientes/{clienteId}/enderecos/pdf` | `arquivoExportEnderecosToPdf` | No UI (P2) |
| `GET /v1/export/clientes/{clienteId}/enderecos/xlsx` | `arquivoExportEnderecosToXlsx` | No UI (P2) |
| `GET /v1/export/enderecos/template` | `arquivoTemplateEnderecos` | No UI (P2) |
| `GET /v1/clientes/fisicos/ativos` | `clientesFisicosGetAllActive` | No UI |
| `GET /v1/clientes/juridicos/ativos` | `clientesJuridicosGetAllActive` | No UI |
| `GET /v1/clientes/fisicos/relatorio` | `clientesFisicosGetReport` | No UI |
| `GET /v1/clientes/juridicos/relatorio` | `clientesJuridicosGetReport` | No UI |
| `GET /v1/enderecos/clientes/{clienteId}/search` | `enderecosSearchByClienteId` | No UI |
| `GET /v1/enderecos/clientes/{clienteId}/count` | `enderecosCountByClienteId` | No UI |
| `GET /v1/enderecos/clientes/{clienteId}/has-addresses` | `enderecosHasAtLeastOneAddress` | No UI |
| `GET /v1/enderecos/clientes/{clienteId}/has-principal` | `enderecosHasPrincipalAddress` | No UI |

---

## 4. Implementation Priority Recommendations

| Priority | Task | Effort | Reason |
|----------|------|--------|--------|
| **P0** | Client hard-delete on detail page | Small | Reuse existing `ConfirmDialogComponent`, single API call |
| **P0** | Connect client import to backend | Small | UI exists, just call endpoint + handle response |
| **P1** | Edit client via modal | Medium | Reuse create dialog patterns, add pre-population |
| **P1** | Address edit in detail page | Medium | Reuse `EnderecoCreateDialogComponent` with pre-population |
| **P2** | Address export (PDF + XLSX) | Small | Enable in `ExportDialogComponent`, use existing `ArquivoService` |
| **P2** | Address import (XLSX + template) | Medium | Add address support to `ImportDialogComponent` |
