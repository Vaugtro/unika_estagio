# Angular Client Manager — Specification-Driven Plan

## Overview

Angular counterpart of the Wicket-based client management application.
Mirrors the full feature set, page structure, and business logic from the
Spring Boot + Wicket reference, but implemented with Angular + a modern UI
library (Angular Material recommended).

## Wicket → Angular Mapping (Conceptual)

| Wicket Concept        | Angular Equivalent              |
|-----------------------|----------------------------------|
| `WebPage`             | `Component` (routed page)        |
| `Panel`               | `Component` (child/reusable)     |
| `WebMarkupContainer`  | `<ng-container>` + `ChangeDetection` |
| `ListView` / `DataView` | `MatTable` / `*ngFor`           |
| `CompoundPropertyModel` | Reactive `FormGroup`            |
| `FormComponent` + `Validator` | Angular Validators        |
| `AjaxLink` / `AjaxButton` | `(click)` + HTTP call          |
| `FeedbackCollector` + `showToast()` | `MatSnackBar` / custom toast |
| `@SpringBean`         | Angular `@Injectable()` service  |
| `LoadableDetachableModel` | `BehaviorSubject` + `AsyncPipe` |
| `IDataProvider`       | Angular `DataSource` class       |
| `AjaxPagingNavigator` | `MatPaginator`                   |
| `mountPage()` routing | Angular `RouterModule` routes    |
| `BasePage`            | `AppLayoutComponent` (navbar shell) |
| `WicketApplication.init()` | `AppModule` / `main.ts` bootstrap |

## Stack Choices (Recommended)

- **Angular**: v18+ (standalone components, signals)
- **UI Library**: Angular Material (MatTable, MatDialog, MatSnackBar, MatPaginator, MatCard, MatFormField)
- **State**: NgRx SignalStore or plain `signal()` / `computed()` (KISS)
- **HTTP**: `HttpClient` with typed services
- **Forms**: Reactive forms with `FormBuilder`
- **Routing**: Lazy-loaded feature modules
- **Export**: Client-side PDF generation via jsPDF or server blob download
- **Icons**: Lucide Angular or Angular Material Icons
- **Validation**: Custom validators mirroring `ValidationConstants.java`
- **Masks**: `ngx-mask` for CPF, CNPJ, CEP, Telefone

## Directory Structure (Generated Skeleton)

```
src/
├── app/
│   ├── app.component.ts            # Root component
│   ├── app.config.ts                # App config (standalone)
│   ├── app.routes.ts                # Root routes (lazy-loaded features)
│   ├── layout/                      # Shell layout (navbar, toast, etc.)
│   │   ├── app-layout.component.ts
│   │   ├── app-layout.component.html
│   │   └── toast.service.ts
│   ├── home/                        # Main page with client type toggle
│   │   ├── home.component.ts
│   │   ├── home.component.html
│   │   └── home.routes.ts
│   ├── cliente-fisico/              # Fisico feature module
│   │   ├── pages/
│   │   │   ├── fisico-list/         # Table + inline edit + create
│   │   │   └── fisico-detail/       # Detail page + endereço CRUD
│   │   ├── components/
│   │   │   ├── fisico-table/        # Reusable table (list page)
│   │   │   ├── fisico-create-dialog/ # Create modal
│   │   │   ├── fisico-row-form/     # Inline edit form (table row)
│   │   │   └── fisico-info-card/    # Detail page info section
│   │   ├── services/
│   │   │   └── fisico.service.ts
│   │   ├── models/
│   │   │   ├── fisico-response.ts
│   │   │   ├── fisico-list-response.ts
│   │   │   ├── fisico-create-request.ts
│   │   │   └── fisico-update-request.ts
│   │   └── fisico.routes.ts
│   ├── cliente-juridico/            # Juridico feature module
│   │   ├── pages/
│   │   │   ├── juridico-list/
│   │   │   └── juridico-detail/
│   │   ├── components/
│   │   │   ├── juridico-table/
│   │   │   ├── juridico-create-dialog/
│   │   │   ├── juridico-row-form/
│   │   │   └── juridico-info-card/
│   │   ├── services/
│   │   │   └── juridico.service.ts
│   │   ├── models/
│   │   │   ├── juridico-response.ts
│   │   │   ├── juridico-list-response.ts
│   │   │   ├── juridico-create-request.ts
│   │   │   └── juridico-update-request.ts
│   │   └── juridico.routes.ts
│   ├── shared/                      # Shared cross-cutting code
│   │   ├── components/
│   │   │   ├── endereco-form/       # Endereço form table (create/edit)
│   │   │   ├── endereco-list/       # Endereço CRUD panel (detail pages)
│   │   │   ├── toast/               # Toast notification component
│   │   │   └── confirm-dialog/      # Delete confirmation
│   │   ├── services/
│   │   │   ├── endereco.service.ts
│   │   │   ├── export.service.ts
│   │   │   └── download.util.ts
│   │   ├── models/
│   │   │   ├── endereco-response.ts
│   │   │   ├── endereco-list-response.ts
│   │   │   ├── endereco-create-request.ts
│   │   │   ├── endereco-update-request.ts
│   │   │   └── endereco-form-model.ts
│   │   ├── validators/             # Custom validators (per ValidationConstants)
│   │   │   ├── validation-constants.ts
│   │   │   ├── cpf.validator.ts
│   │   │   ├── cnpj.validator.ts
│   │   │   ├── cep.validator.ts
│   │   │   └── telefone.validator.ts
│   │   └── pipes/
│   │       ├── cpf.pipe.ts
│   │       ├── cnpj.pipe.ts
│   │       ├── cep.pipe.ts
│   │       └── telefone.pipe.ts
│   └── core/                        # Core singleton services
│       ├── error-handler.service.ts  # Global HTTP error → toast
│       └── api.config.ts             # Base URL, interceptors
└── assets/                           # Static resources (icons if local)
```

## Feature Inventory (Complete)

| # | Feature | Wicket Source | Angular Owner |
|---|---------|---------------|---------------|
| 1 | Home page with fisico/juridico toggle | `HomePage` | `home/` |
| 2 | Fisico list table (paginated, inline edit) | `ClientesFisicosTablePanel` | `fisico-list/` |
| 3 | Juridico list table (paginated, inline edit) | `ClientesJuridicosTablePanel` | `juridico-list/` |
| 4 | Fisico create (modal with endereços) | `ClienteFisicoCreateModal` | `fisico-create-dialog/` |
| 5 | Juridico create (modal with endereços) | `ClienteJuridicoCreateModal` | `juridico-create-dialog/` |
| 6 | Fisico detail page (info + endereço CRUD) | `ClienteDetalhePage` | `fisico-detail/` |
| 7 | Juridico detail page (info + endereço CRUD) | `ClienteJuridicoDetalhePage` | `juridico-detail/` |
| 8 | Endereço CRUD (table, modal add/edit, delete) | `EnderecoListViewPanel` | `endereco-list/` |
| 9 | Endereço create form (multi-row table) | `EnderecoCreateTablePanel` | `endereco-form/` |
| 10 | Status toggle (activate/inactivate) | Row forms toggle btn | `fisico-row-form` / `juridico-row-form` |
| 11 | Export PDF (fisico, juridico, endereço) | Export links | `export.service.ts` |
| 12 | Export XLSX (fisico, juridico, endereço) | Export links | `export.service.ts` |
| 13 | Toast notifications | `ValidationFeedback` | `toast.service.ts` |
| 14 | Input masks (CPF, CNPJ, CEP, telefone) | `jquery.mask` | `ngx-mask` |
| 15 | ViaCEP auto-fill | `EnderecoCreateTablePanel.html` | `endereco-form/` |
| 16 | Form validation with constants | Wicket validators | Custom Angular validators |
| 17 | Endereço principal uniqueness | DB constraint + error catch | Front-end validation |
| 18 | Debug bar (dev only) | `DebugBar` in `BasePage` | `environment.production` toggle |

## Next Steps

1. Run `ng new client-manager --standalone --routing`
2. Generate each feature as lazy-loaded route module
3. Create shared components and services
4. Implement each feature per its spec file
5. Connect to same Spring Boot REST API
