# Feature Breakdown — Implementation Order

Each feature below corresponds to one or more Wicket files. Implementation
order is designed for incremental testability.

---

## Feature 1: App Shell + Routing

**Files**: `layout/app-layout.component.ts`, `layout/app-layout.component.html`,
`layout/toast.service.ts`, `shared/components/toast/`

**Wicket Ref**: `BasePage` + `BasePage.html` + `ValidationFeedback`

**What to build:**
- `AppLayoutComponent` with gradient navbar, `<router-outlet>`, toast zone
- `ToastService` using `signal<Toast[]>` with `show()` / `dismiss()`
- `ToastComponent` rendering stacked toasts with auto-dismiss (5s)
- Root routes with lazy-loaded feature modules

**Acceptance:**
- Navigating to `/` shows navbar with "Desafio Estágio" title
- Toast `service.show('success', 'Hello')` renders a green toast that disappears

---

## Feature 2: Home Page with Client Toggle

**Files**: `home/home.component.ts`, `home/home.component.html`

**Wicket Ref**: `HomePage` + `HomePage.html`

**What to build:**
- Pill-style button group (Físicos / Jurídicos)
- Signal `activeTab: 'fisico' | 'juridico'`
- `@if (activeTab() === 'fisico')` renders `FisicoTableComponent`
- `@if (activeTab() === 'juridico')` renders `JuridicoTableComponent`
- Default: fisico active on load

**Acceptance:**
- Page loads showing "Clientes Físicos" table
- Click "Jurídicos" → table swaps (data loaded via HTTP)
- Active button has highlighted style

---

## Feature 3: Fisico List Table (Paginated + Inline Edit)

**Files**: `cliente-fisico/pages/fisico-list/`, `cliente-fisico/components/fisico-table/`,
`cliente-fisico/components/fisico-row-form/`, `cliente-fisico/services/fisico.service.ts`,
`cliente-fisico/models/`

**Wicket Ref**: `ClientesFisicosTablePanel`, `ClienteFisicoRowUpdateForm`,
`ClienteFisicoDataView`, `ClienteFisicoDataProvider`, `AbstractClienteDataView`

**What to build:**
- `FisicoService` with all HTTP methods (see `services/api-contracts.md`)
- `FisicoTableComponent` with `MatTable` + `MatPaginator`
- Server-side pagination: `(page) => service.findAll(page.pageIndex, page.pageSize)`
- Inline edit: each row has a reactive `FormGroup` for `nome` and `email`
- "Salvar" row button calls `service.update(id, form)`
- "Status" toggle: green/red badge button, calls `service.activate/inactivate(id)`
- "Detalhes" link navigates to `/fisico/:id`
- PDF/XLSX export buttons

**Acceptance:**
- Table loads page 0 with 10 items
- Paginator shows total count from `Page.totalElements`
- Inline edit: type in nome, click Salvar → HTTP PUT → success toast
- Status toggle: click to inactivate → badge turns red → success toast
- Export PDF: downloads a PDF file

---

## Feature 4: Juridico List Table

**Files**: `cliente-juridico/pages/juridico-list/`, `cliente-juridico/components/juridico-table/`,
`cliente-juridico/components/juridico-row-form/`, `cliente-juridico/services/juridico.service.ts`

**Wicket Ref**: `ClientesJuridicosTablePanel`, `ClienteJuridicoRowUpdateForm`

**What to build:** Same pattern as Fisico, but for Juridico entities.

**Fields**: razaoSocial (editable), cnpj (read-only), email (editable)

**Acceptance:** Same as Feature 3, but for juridico entities.

---

## Feature 5: Fisico Create Dialog

**Files**: `cliente-fisico/components/fisico-create-dialog/`

**Wicket Ref**: `ClienteFisicoCreateModal`

**What to build:**
- `MatDialog` triggered by "Novo" button in FisicoTableComponent
- Reactive form with: CPF, Nome, RG, Email, Data Nascimento
- Embedded `EnderecoFormComponent` (see Feature 9)
- Submit: builds `FisicoCreateRequest` → `service.create(dto)` → close dialog → refresh table
- Error handling: toast for server errors (e.g. CPF already exists)
- Success: toast + table refresh

**Acceptance:**
- Open dialog → fill form → submit → POST call → table has new row
- Empty required fields show red validation errors
- Invalid CPF shows "CPF inválido"

---

## Feature 6: Juridico Create Dialog

**Files**: `cliente-juridico/components/juridico-create-dialog/`

**Wicket Ref**: `ClienteJuridicoCreateModal`

**What to build:** Same pattern as Feature 5, fields: CNPJ, Razão Social, Inscrição Estadual, Email, Data Criação

---

## Feature 7: Endereço Form Component (Multi-row Table)

**Files**: `shared/components/endereco-form/`

**Wicket Ref**: `EnderecoCreateTablePanel` + `EnderecoCreateTablePanel.html`

**What to build:**
- Reactive `FormArray` of `FormGroup`, each with controls for all endereço fields
- Columns: Logradouro, Nº, Bairro, CEP, Cidade, UF, Telefone, Complemento, Principal (checkbox), Remover
- CEP mask + ViaCEP auto-fill (`blur` on CEP → fetch → populate logradouro/bairro/cidade/UF)
- Min 1 row enforced (Remover disabled if only 1)
- "+ Adicionar Endereço" button pushes a new empty `FormGroup`
- Used inside both create dialogs and the endereço CRUD panel

**Acceptance:**
- Renders with 1 empty row
- Type CEP, blur → fields auto-populate from ViaCEP
- Click "+" → new row appears
- Click "Remover" on single row → disabled / hidden
- All fields have proper `maxlength` from ValidationConstants

---

## Feature 8: Endereço CRUD Panel (Detail Page)

**Files**: `shared/components/endereco-list/` + `shared/services/endereco.service.ts`

**Wicket Ref**: `EnderecoListViewPanel` + `EnderecoListViewPanel.html`

**What to build:**
- `EnderecoService` with all HTTP CRUD methods
- Card with header (title + PDF/XLSX/Adicionar buttons) + table of endereços
- "Adicionar" → opens `MatDialog` with `EnderecoFormComponent` (empty, add mode)
- "Editar" (per row) → opens `MatDialog` with `EnderecoFormComponent` (pre-populated, edit mode)
- "Excluir" (per row) → confirmation dialog → `service.delete(id)` → refresh
- `setAsPrincipal` constraint: if user tries to make a second endereço principal, show dialog informing them to uncheck the current one first
- used in both `FisicoDetailComponent` and `JuridicoDetailComponent`

**Acceptance:**
- Loads endereços for given clienteId
- Add: opens dialog → fill → submit → list has new row
- Edit: opens dialog pre-filled → modify → submit → list shows changes
- Delete: confirm → row removed
- Principal constraint: trying to set two principals shows friendly toast

---

## Feature 9: Fisico Detail Page

**Files**: `cliente-fisico/pages/fisico-detail/`, `cliente-fisico/components/fisico-info-card/`

**Wicket Ref**: `ClienteDetalhePage` + `ClienteDetalhePage.html`

**What to build:**
- Route: `/fisico/:id`
- Back button → `/`
- `FisicoInfoCardComponent`: display-only card with ID, Nome, CPF, RG, Email, Data Nasc., Status (badge)
- Embedded `<app-endereco-list>` for endereço CRUD
- Data loaded via `FisicoService.findById(id)` → `signal`

**Acceptance:**
- Navigate to `/fisico/1` → shows cliente info + endereço table
- Endereço CRUD works (add/edit/delete)
- Back button returns to home

---

## Feature 10: Juridico Detail Page

**Files**: `cliente-juridico/pages/juridico-detail/`, `cliente-juridico/components/juridico-info-card/`

**Wicket Ref**: `ClienteJuridicoDetalhePage` + `ClienteJuridicoDetalhePage.html`

Same as Feature 9, but for Juridico: ID, CNPJ, Razão Social, Insc. Estadual, Email, Data Criação, Status

---

## Feature 11: Export Service

**Files**: `shared/services/export.service.ts`, `shared/services/download.util.ts`

**Wicket Ref**: `ExportService` interface + `ByteArrayResourceStream`

**What to build:**
- `ExportService` with methods that call HTTP GET with `responseType: 'blob'`
- `downloadBlob(blob, filename)` utility function
- Three exportable groups:
  - Fisicos: PDF + XLSX (all records)
  - Juridicos: PDF + XLSX (all records)
  - Enderecos: PDF + XLSX (by clienteId)

**Acceptance:**
- Each export endpoint triggers a file download
- Downloaded PDF opens correctly
- Downloaded XLSX opens correctly
