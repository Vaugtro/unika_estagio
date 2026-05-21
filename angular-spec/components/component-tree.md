# Component Tree & Responsibilities

## AppLayoutComponent (Shell)

```
AppLayoutComponent
├── <nav> (gradient navbar with brand logo)
├── <router-outlet> (child page content)
└── <app-toast> (global toast container)
```

**Wicket Ref**: `BasePage` + `BasePage.html`

**Responsibilities:**
- Fixed-position gradient navbar with app title
- Toast notification container (singleton, listens to `ToastService`)
- `<router-outlet>` where routed pages render
- Debug bar in dev mode (`environment.production`)

---

## HomeComponent (Toggle Page)

```
HomeComponent
├── Toggle buttons (Físicos / Jurídicos) — pill-style btn-group
└── <ng-container> — swaps between:
    ├── <app-fisico-table>
    └── <app-juridico-table>
```

**Wicket Ref**: `HomePage` + `HomePage.html`

**Responsibilities:**
- Active tab management (initially shows fisico)
- Buttons call `swapPanel('fisico' | 'juridico')` which updates a signal
- Signal drives which table component renders via `@if` / `@switch`

---

## FisicoTableComponent (Paginated Table + Inline Edit)

```
FisicoTableComponent
├── Card header: title + PDF / XLSX / Novo buttons
├── <mat-table> (clientes)
│   └── <mat-row> with inline form fields (nome, email)
│       ├── ID column (read-only)
│       ├── Nome column: <input>
│       ├── CPF column (read-only)
│       ├── Email column: <input>
│       ├── Status column: toggle button (activate/inactivate)
│       └── Actions column: Detalhes link + Salvar button
├── <mat-paginator> (server-side pagination)
└── <app-fisico-create-dialog> (hidden, opened on "Novo")
```

**Wicket Ref**: `ClientesFisicosTablePanel` + `ClienteFisicoRowUpdateForm`

**Responsibilities:**
- Load paginated data from `FisicoService.findAll(page, size)`
- Inline edit: each row has a reactive `FormGroup` for editable fields
- "Salvar" button calls `FisicoService.update(id, form.value)`
- "Status" toggle button calls `FisicoService.activate/inactivate(id)`
- "Detalhes" button navigates to `/fisico/:id`
- Export buttons trigger `ExportService` for PDF/XLSX
- "Novo" button opens the create dialog

---

## JuridicoTableComponent (Paginated Table + Inline Edit)

```
JuridicoTableComponent
├── Card header: title + PDF / XLSX / Novo buttons
├── <mat-table>
│   └── <mat-row> with inline form
│       ├── ID (read-only)
│       ├── Razão Social: <input>
│       ├── CNPJ (read-only)
│       ├── Email: <input>
│       ├── Status toggle
│       └── Actions: Detalhes + Salvar
├── <mat-paginator>
└── <app-juridico-create-dialog>
```

**Wicket Ref**: `ClientesJuridicosTablePanel` + `ClienteJuridicoRowUpdateForm`

**Responsibilities:** Same pattern as Fisico, but for Juridico entities.

---

## FisicoDetailComponent (Full Detail Page)

```
FisicoDetailComponent
├── Header: Voltar button + "Detalhes do Cliente" title
├── <app-fisico-info-card> (cliente info in card with status badges)
└── <app-endereco-list> (endereço CRUD panel)
```

**Wicket Ref**: `ClienteDetalhePage` + `ClienteDetalhePage.html`

---

## JuridicoDetailComponent

```
JuridicoDetailComponent
├── Header: Voltar button + "Detalhes do Cliente Jurídico" title
├── <app-juridico-info-card>
└── <app-endereco-list>
```

**Wicket Ref**: `ClienteJuridicoDetalhePage` + `ClienteJuridicoDetalhePage.html`

---

## FisicoInfoCardComponent / JuridicoInfoCardComponent

```
FisicoInfoCardComponent
└── <mat-card>
    ├── Card header: "Informações do Cliente"
    └── Card body: grid of fields
        ├── ID, Nome, CPF, RG, Email, Data Nasc., Status
```

Display-only card. Data loaded from `FisicoService.findById(id)` as a `signal`.

---

## FisicoCreateDialogComponent / JuridicoCreateDialogComponent

```
FisicoCreateDialogComponent
└── <mat-dialog>
    ├── Title: "Novo Cliente Físico"
    ├── Form fields (reactive form, two-column grid):
    │   ├── CPF (required, CPF validation, mask)
    │   ├── Nome (required, 3-150 chars)
    │   ├── RG (required, 7-9 chars)
    │   ├── Email (optional, max 150 chars)
    │   └── Data Nascimento (required, date input)
    ├── <app-endereco-form> (one or more endereços)
    └── Actions: Cancelar + Salvar
```

**Wicket Ref**: `ClienteFisicoCreateModal`

On submit: builds `ClienteFisicoCreateRequest` + calls `FisicoService.create()`.
On success: closes dialog, refreshes parent table.

---

## EnderecoFormComponent (Multi-row Table Form)

```
EnderecoFormComponent
├── <table> of endereço rows (reactive FormArray)
│   └── Per row:
│       ├── Logradouro (required, 3-150 chars)
│       ├── Nº (required, number)
│       ├── Bairro (required, 3-100 chars)
│       ├── CEP (required, CEP mask, ViaCEP auto-fill)
│       ├── Cidade (required, 3-100 chars)
│       ├── UF (required, exact 2 chars)
│       ├── Telefone (required, telefone mask)
│       ├── Complemento (optional, max 150 chars)
│       ├── Principal (checkbox)
│       └── Remover button (disabled if only 1 row)
└── + Adicionar Endereço button
```

**Wicket Ref**: `EnderecoCreateTablePanel` + `EnderecoCreateTablePanel.html`

**ViaCEP auto-fill:** When CEP field loses focus with 8 digits, fetches from ViaCEP API and fills logradouro, bairro, cidade, UF.

---

## EnderecoListComponent (Read + CRUD Panel)

```
EnderecoListComponent
├── Card header: "Endereços" + PDF / XLSX / Adicionar buttons
├── <mat-table> of endereços
│   ├── Columns: Logradouro, Nº, Bairro, CEP, Cidade, UF, Telefone, Principal
│   ├── Edit button → opens edit dialog (reuses EnderecoFormComponent in a MatDialog)
│   └── Delete button → confirmation dialog → delete
└── Endereço Modal (MatDialog with EnderecoFormComponent for add/edit)
```

**Wicket Ref**: `EnderecoListViewPanel` + `EnderecoListViewPanel.html`

**Responsibilities:**
- Loads endereços via `EnderecoService.findAllByClienteId(clienteId)`
- Add: opens dialog with empty EnderecoFormComponent
- Edit: opens dialog pre-populated with endereço data
- Delete: confirmation → `EnderecoService.delete(id)` → refresh list
- Principal constraint enforced client-side: warn if unchecking sole principal
- Export: PDF/XLSX via `ExportService`

---

## ToastComponent + ToastService

```typescript
// toast.service.ts
@Injectable({ providedIn: 'root' })
export class ToastService {
  private toasts = signal<Toast[]>([]);

  show(type: 'success' | 'error' | 'warning' | 'info', message: string) { ... }
  dismiss(id: string) { ... }
}
```

**Wicket Ref**: `ValidationFeedback.java` + `BasePage.html` `window.showToast()`

**Implementation:** Uses `MatSnackBar` or a custom stacked toast container similar to Bootstrap toast pattern.
