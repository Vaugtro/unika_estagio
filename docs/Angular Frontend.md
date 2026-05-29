---
name: Angular Frontend
repo: unika_estagio/angular
stack: Angular 14.2, TypeScript 4.7, Angular Material 14, Tailwind CSS 3, RxJS 7.5
version: 0.0.0
status: development
aliases: [angular-spa, frontend]
---

# Angular Frontend

## Executive Summary

Single-page application for client (PF/PJ) and address management. Built with Angular 14.2 and Angular Material, it consumes a Spring Boot REST API via an OpenAPI-generated client. Provides CRUD operations, fuzzy search, inline editing, PDF/XLSX export, XLSX import, and CEP auto-fill via ViaCEP.

## Architecture Diagrams

### System Context

```mermaid
graph TB
    subgraph Browser
        ANG[Angular SPA<br/>localhost:4200]
    end
    subgraph Server
        API[Spring Boot REST API<br/>localhost:8080/v1]
        DB[(MariaDB<br/>dummy_db)]
    end
    EXT[ViaCEP API<br/>viacep.com.br]

    ANG -- HTTP/JSON --> API
    ANG -- HTTP/JSON --> EXT
    API -- JDBC --> DB
    style ANG fill:#dd0031,stroke:#333,stroke-width:2px,color:#fff
    style API fill:#6db33f,stroke:#333,stroke-width:2px,color:#fff
```

### Module Dependency Graph

```mermaid
graph TB
    APP[AppModule]
    API[ApiModule]
    SHARED[SharedModule]
    HOME[HomeComponent]
    FISICO[ClienteFisico<br/>Components]
    JURIDICO[ClienteJuridico<br/>Components]
    ENDERECO[Endereco<br/>Components]
    SHARED_COMP[Shared<br/>Components]

    APP --> SHARED
    APP --> API
    SHARED --> HOME
    SHARED --> FISICO
    SHARED --> JURIDICO
    SHARED --> ENDERECO
    SHARED --> SHARED_COMP
    style APP fill:#dd0031,stroke:#333,color:#fff
    style API fill:#6db33f,stroke:#333,color:#fff
```

## Folder Structure

```
📁 angular/
├── 📁 src/
│   ├── 📁 app/
│   │   ├── 📁 api/                        # OpenAPI-generated client
│   │   │   ├── 📁 api/                    # REST service classes (6 files)
│   │   │   ├── 📁 model/                  # TS interfaces (~30 files)
│   │   │   ├── 📄 api.base.service.ts     # Base HTTP service
│   │   │   ├── 📄 api.module.ts           # Singleton ApiModule
│   │   │   └── 📄 configuration.ts        # API client config
│   │   ├── 📁 shared/                     # Reusable module
│   │   │   ├── 📁 components/             # 6 shared components
│   │   │   │   ├── 📁 confirm-dialog/     # Generic confirmation
│   │   │   │   ├── 📁 endereco-form/      # Multi-entry address FormArray
│   │   │   │   ├── 📁 endereco-list/      # Address display list
│   │   │   │   ├── 📁 export-dialog/      # PDF/XLSX export
│   │   │   │   ├── 📁 import-dialog/      # XLSX import
│   │   │   │   └── 📁 toast/              # Toast notifications
│   │   │   ├── 📁 services/               # ToastService, ViaCepService
│   │   │   ├── 📁 validators/             # CPF, CNPJ, CEP, phone validators
│   │   │   └── 📄 shared.module.ts
│   │   ├── 📁 home/                       # Dashboard with PF/PJ tabs
│   │   ├── 📁 cliente-fisico/             # PF components + detail page
│   │   ├── 📁 cliente-juridico/           # PJ components + detail page
│   │   └── 📁 endereco/                   # Address table + create dialog
│   ├── 📁 environments/                   # environment.ts, environment.prod.ts
│   └── 📁 templates/                      # Mustache templates for OpenAPI gen
├── 📄 package.json
├── 📄 angular.json
├── 📄 tailwind.config.js
├── 📄 proxy.conf.json
├── 📄 nx.json
├── 📄 openapitools.json
└── 📄 tsconfig.json
```

## Module Breakdown

### AppModule
| Responsibility | Key Details |
|---|---|
| Root module, bootstrap | `AppComponent` only in `declarations` |
| Imports | `BrowserModule`, `HttpClientModule`, `MatToolbarModule`, `AppRoutingModule`, `SharedModule`, `ApiModule.forRoot()` |
| Providers | `LOCALE_ID: 'pt-BR'` |
| Lazy loading | None — all components eagerly declared via SharedModule |

### SharedModule
| Responsibility | Key Details |
|---|---|
| Central declaration module | All feature + shared components declared here |
| Material imports | 14 modules: `MatCard`, `MatTable`, `MatPaginator`, `MatButton`, `MatDialog`, `MatSnackBar`, etc. |
| Exports | Material modules + shared components only (not feature components) |
| Third-party | `LucideAngularModule`, `NgxMaskModule.forRoot()` |

### ApiModule
| Responsibility | Key Details |
|---|---|
| OpenAPI client singleton | `forRoot()` guard prevents double import |
| Base path | Empty string (defaults to `http://localhost:8080`) |

## API Surface

### Generated API Services

All services extend `BaseService` and are `providedIn: 'root'`.

| Service | Base Path | Key Methods |
|---|---|---|
| `ClientesFisicosService` | `/v1/clientes/fisicos` | `getAll`, `getById`, `getByCpf`, `create`, `update`, `search`, `activate`, `inactivate`, `hardDelete`, `softDelete`, `getReport`, `existsByCpf` |
| `ClientesJuridicosService` | `/v1/clientes/juridicos` | `getAll`, `getById`, `getByCnpj`, `create`, `update`, `search`, `activate`, `inactivate`, `hardDelete`, `getReport`, `existsByCnpj` |
| `EnderecosService` | `/v1/enderecos` | `findAllByClienteId`, `create`, `createForCliente`, `update`, `delete`, `setAsPrincipal`, `search`, `countByClienteId`, `hasPrincipalAddress` |
| `ArquivoService` | `/v1/export` | PDF/XLSX export, XLSX import, template download |
| `MunicipiosService` | `/v1/municipios` | `findByUf` |
| `UnidadesFederativasService` | `/v1/unidades-federativas` | `findAll` |

### Frontend Routes

| Path | Component | Description |
|---|---|---|
| `""` | — | Redirects to `/home` |
| `home` | `HomeComponent` | Dashboard with PF/PJ tab switching |
| `fisico/:id` | `FisicoDetailComponent` | PF client detail page |
| `juridico/:id` | `JuridicoDetailComponent` | PJ client detail page |

## Data Flow

### CRUD Operation Flow

```mermaid
sequenceDiagram
    participant User
    participant Component
    participant APIService
    participant Backend
    participant DB

    User->>Component: Click action
    Component->>Component: Open dialog / toggle inline edit
    User->>Component: Fill form / confirm
    Component->>APIService: HTTP call (POST/PUT/PATCH/DELETE)
    APIService->>Backend: REST request
    Backend->>DB: SQL operation
    DB-->>Backend: Result
    Backend-->>APIService: Response JSON
    APIService-->>Component: Observable<T>
    Component->>ToastService: Show success/error toast
    Component->>Component: Reload data
    Component-->>User: Updated view
```

### Search Flow (Debounced)

```mermaid
sequenceDiagram
    participant User
    participant Component
    participant APIService
    participant Backend

    User->>Component: Type in search field
    Component->>Component: 300ms debounce
    Component->>APIService: search(query, pageable)
    APIService->>Backend: GET /search?q=...
    Backend-->>APIService: Page<T>
    APIService-->>Component: Results
    Component-->>User: Updated table
```

## Component Tree

### Home Page

```mermaid
graph TB
    APP[AppComponent]
    HOME[HomeComponent]
    FISICO_TABLE[FisicoTableComponent]
    JURIDICO_TABLE[JuridicoTableComponent]
    FISICO_CREATE[FisicoCreateDialogComponent]
    FISICO_EDIT[FisicoEditDialogComponent]
    END_F[EnderecoFormComponent]
    FISICO_ROW[FisicoRowFormComponent]
    JURIDICO_CREATE[JuridicoCreateDialogComponent]
    JURIDICO_EDIT[JuridicoEditDialogComponent]
    JURIDICO_ROW[JuridicoRowFormComponent]
    EXPORT[ExportDialogComponent]
    IMPORT[ImportDialogComponent]
    CONFIRM[ConfirmDialogComponent]
    TOAST[ToastComponent]

    APP --> HOME
    APP --> TOAST
    HOME --> FISICO_TABLE
    HOME --> JURIDICO_TABLE
    FISICO_TABLE --> FISICO_CREATE
    FISICO_TABLE --> FISICO_EDIT
    FISICO_TABLE --> FISICO_ROW
    FISICO_CREATE --> END_F
    JURIDICO_TABLE --> JURIDICO_CREATE
    JURIDICO_TABLE --> JURIDICO_EDIT
    JURIDICO_CREATE --> END_F
    FISICO_TABLE --> EXPORT
    FISICO_TABLE --> IMPORT
    JURIDICO_TABLE --> EXPORT
    JURIDICO_TABLE --> IMPORT
    FISICO_EDIT --> CONFIRM
```

## Dependencies

### Module Dependency Graph

```mermaid
graph TB
    subgraph Internal
        APP[AppModule]
        SHARED[SharedModule]
        API[ApiModule]
    end
    subgraph External
        ANG_MAT[Angular Material 14]
        LUCIDE[Lucide Icons]
        NGX_MASK[ngx-mask 14]
        RXJS[RxJS 7.5]
        TAILWIND[Tailwind CSS 3]
    end

    APP --> SHARED
    APP --> API
    SHARED --> ANG_MAT
    SHARED --> LUCIDE
    SHARED --> NGX_MASK
    SHARED --> RXJS
    STYLE --> TAILWIND
```

### External Dependencies Table

| Package | Version | Purpose |
|---|---|---|
| `@angular/core` | ^14.2.0 | Framework |
| `@angular/material` | ^14.2.7 | UI component library |
| `@angular/cdk` | ^14.2.7 | Component dev kit |
| `rxjs` | ~7.5.0 | Reactive programming |
| `lucide-angular` | 1.0.0 | Open-source icons |
| `ngx-mask` | ^14.3.3 | Input masking (CPF, CNPJ, CEP, phone) |
| `tailwindcss` | ^3.4.19 | Utility CSS |
| `zone.js` | ~0.11.4 | Angular change detection |
| `@openapitools/openapi-generator-cli` | ^2.34.0 | API client codegen |
| `mustache` | ^4.2.0 | Template engine for codegen |
| `nx` | 15.9.7 | Monorepo tooling |

## Configuration

### Environment Variables

| Variable | Source | Default | Description |
|---|---|---|---|
| `apiUrl` | `window.__env` | `http://localhost:8080` | Backend API base URL |
| `production` | `environment.ts` / `environment.prod.ts` | `false` | Production mode flag |

### Build Configuration

| Setting | Value |
|---|---|
| Build target | ES2020 |
| Module | ES2020 |
| Strict mode | Enabled |
| Strict templates | Enabled |
| Style | SCSS |
| Package manager | pnpm |
| Output hashing | Production only |
| Budget (initial) | 2MB warning, 3MB error |

### Proxy Config (Development)

```json
{ "/v1": { "target": "http://localhost:8080", "secure": false } }
```

## Testing Strategy

```mermaid
graph TB
    subgraph Test Pyramid
        E2E[None]
        INT[None]
        UNIT[Jasmine + Karma]
    end
    UNIT -->|Current| ONE[app.component.spec.ts<br/>5 tests only]
    note[Only 1 spec file exists.<br/>Feature components have 0 coverage.]
```

**Framework:** Jasmine 4.3 + Karma 6.4  
**Runner:** Chrome (headless)  
**Coverage:** `./coverage/ng14-workspace`  
**Command:** `ng test`

## Troubleshooting

| Problem | Likely Cause | Solution |
|---|---|---|
| API calls fail with 404 | Backend not running | Start `docker-compose up mariadb` + backend |
| CORS errors | Backend not allowing origin | Check `WebConfig` allows `localhost:4200` |
| OpenAPI client outdated | Backend spec changed | Run `npm run generate:api` |
| Input masks not working | ngx-mask not configured | Ensure `NgxMaskModule.forRoot()` imported |
| Proxy not working | `proxy.conf.json` misconfigured | Check target matches backend port |

## Related Documents

- [[Spring Backend]] — REST API consumed by this frontend
- [[Wicket UI]] — Alternative UI layer for same backend
- [[Flyway Migrations]] — Database schema reference
