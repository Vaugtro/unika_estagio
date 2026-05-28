# spring/estagio — Spring Boot + Wicket Backend

**Parent:** [root AGENTS.md](../../AGENTS.md) for monorepo-level conventions.

## OVERVIEW

Spring Boot backend (JAR/WAR) serving REST endpoints + Apache Wicket server-rendered UI. Client management with CRUD,
validation, file export/import, and JasperReports.

## STRUCTURE

```
com.desafio.estagio/
├── config/              # Spring @Configuration classes
├── controller/          # REST endpoints (ClienteFisico, ClienteJuridico, Endereco)
├── dto/                 # Immutable records (clientefisico/, clientejuridico/, endereco/)
├── exceptions/          # Custom exceptions + global handlers
├── factory/             # Object factories
├── mapper/              # MapStruct mappers (ClienteFisicoMapper, ClienteJuridicoMapper, EnderecoMapper)
├── model/               # JPA entities (Cliente, Endereco, enums/, formatters/)
├── repository/          # Spring Data JPA repositories
├── service/             # Business logic (impl/, lifecycle/, query/)
├── validation/          # Constraints (annotation/, internal/)
└── wicket/              # Wicket UI layer
    ├── application/     # WicketApplication setup
    ├── component/       # Reusable components (form/, modal/, shared/, table/, dataview/)
    ├── model/           # Wicket-specific models
    ├── page/            # Pages (base/, clientes/, home/)
    ├── provider/        # Data providers
    └── util/            # Utilities
```

## WHERE TO LOOK

| Task                   | Package                                 | Notes                                              |
|------------------------|-----------------------------------------|----------------------------------------------------|
| Add REST endpoint      | `controller/`                           | Spring REST controllers                            |
| Modify entity          | `model/`                                | JPA entities + enums                               |
| Add service logic      | `service/impl/`                         | Concrete service implementations                   |
| Add Wicket page        | `wicket/page/`                          | HTML template alongside Java                       |
| Add Wicket component   | `wicket/component/`                     | Reusable panels, forms, modals                     |
| Add/enforce validation | `validation/`                           | Use `ValidationConstants`                          |
| Add DTO                | `dto/`                                  | Immutable `record`, create `*FormModel` for Wicket |
| Add migration          | `src/main/resources/db/migration/main/` | `V{next}__description.sql`                         |
| Run tests              | `src/test/java/`                        | Mirrors main package structure                     |

## CONVENTIONS

- **Service layer**: `@Transactional` only at service. `AbstractClienteService` defines `final activate/inactivate` — do
  not override.
- **DTO → FormModel pattern**: Every DTO needs a mutable `*FormModel` for Wicket form binding. Map DTO ↔ FormModel at
  submit time only.
- **Validation constants**: `ValidationConstants.java` centralizes field length limits. Apply in DTO annotations AND
  Wicket validators.
- **Wicket HTML placement**: `.html` files beside their `.java` component in `src/main/java/`. Build script handles
  packaging.
- **Form bindings**: Never nest `<form>` inside `<table>/<tbody>/<tr>`. Bind `Form` component directly to
  `<tr wicket:id="...">`.
- **Code organization**: Shared Wicket components (e.g., `EnderecoCreateTablePanel`) extract cross-cutting logic but
  keep type-specific `wicket:id` markup.
- **Tests**: JUnit 5 + Mockito. Test classes mirror production package structure. Heavy use of parameterized tests for
  validators.

## ANTI-PATTERNS

- Direct `EntityManager` injection — use Spring-managed persistence context
- Suppressing warnings (`@SuppressWarnings`) — fix root cause
- Overriding `AbstractClienteService` final methods
- Hardcoded field limits — always reference `ValidationConstants`
