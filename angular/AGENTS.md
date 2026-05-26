# angular/ — Angular 14 SPA Frontend

**Parent:** [root AGENTS.md](../AGENTS.md) for monorepo-level conventions.

## OVERVIEW

Angular 14 SPA consuming REST APIs from the Spring backend. OpenAPI-generated API client. Client management CRUD with dialogs, inline row editing, export/import.

## STRUCTURE

```
src/app/
├── api/                    # OpenAPI-generated client (model/ + api/)
│   ├── model/              # Generated TS interfaces
│   └── api/                # Generated REST client services
├── cliente-fisico/         # PF client feature module
│   ├── components/         # (fisico-create-dialog, fisico-info-card, fisico-row-form, fisico-table)
│   └── pages/              # Feature pages
├── cliente-juridico/       # PJ client feature module
│   ├── components/         # (juridico-create-dialog, juridico-info-card, juridico-row-form, juridico-table)
│   └── pages/              # Feature pages
├── endereco/               # Address feature module
│   ├── components/         # (endereco-create-dialog, endereco-table)
│   └── pages/              # Feature pages
├── home/                   # Dashboard/home page
└── shared/                 # Cross-cutting
    ├── components/         # (confirm-dialog, endereco-form, endereco-list, export-dialog, import-dialog, toast)
    ├── models/             # Shared TS interfaces
    ├── services/           # Shared services
    └── validators/         # Custom validators
```

## WHERE TO LOOK

| Task | Location | Notes |
|------|----------|-------|
| Add client feature | `src/app/cliente-*/` | Mirror existing module structure |
| Add shared component | `src/app/shared/components/` | Reusable dialogs, forms |
| Regenerate API client | `src/app/api/` | OpenAPI Generator output |
| Modify REST integration | `src/app/api/api/` | Generated services |
| Add validator | `src/app/shared/validators/` | Custom Angular validators |

## CONVENTIONS

- **API client**: Auto-generated via OpenAPI Generator. Do not manually edit `api/` files — regenerate from backend spec.
- **Feature modules**: Each domain (cliente-fisico, cliente-juridico, endereco) gets its own module with `components/` + `pages/` directories.
- **Shared components**: Reusable UI (toast, confirm-dialog, export-dialog, import-dialog) lives in `shared/components/`.
- **Naming**: Files use kebab-case. Components use PascalCase classes with `Component` suffix.
- **Environment config**: API base URLs in `src/environments/`.

## COMMANDS

```bash
npm start      # dev server
npm run build  # production build
```
