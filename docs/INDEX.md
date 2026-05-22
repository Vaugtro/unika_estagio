# Sistema de Cadastro de Clientes — Documentação do Projeto

## Stack

| Camada       | Tecnologia                          |
|-------------|-------------------------------------|
| Backend     | Java 17 + Spring Boot 4.0.6        |
| Frontend    | Apache Wicket 7.17.0               |
| ORM         | JPA / Hibernate (Spring Data)      |
| Mapper      | MapStruct                           |
| Build       | Gradle                              |
| Database    | MariaDB + Flyway migrations        |
| Reports     | JasperReports (PDF) / XLSX         |
| Validation  | Bean Validation (Jakarta)          |

## Estrutura de Pacotes

```
com.desafio.estagio/
├── config/          → Configurações Spring (Web, Wicket, JasperReports)
├── controller/      → REST Controllers (API endpoints)
├── dto/             → Data Transfer Objects (records imutáveis)
│   ├── clientefisico/
│   ├── cliente juridico/
│   └── endereco/
├── exceptions/      → BusinessException, ResourceNotFoundException, ConflictException
├── factory/         → Fábricas para criação de entidades
├── mapper/          → MapStruct mappers (Entity ↔ DTO)
├── model/           → Entidades JPA + Enums + Formatters
├── repository/      → Spring Data JPA Repositories
├── service/         → Interfaces e implementações do service layer
│   ├── impl/
│   ├── lifecycle/   → Write operations interfaces
│   ├── query/       → Read-only operations interfaces
├── validation/      → ValidationConstants, anotações customizadas
└── wicket/          → Apache Wicket (frontend)
    ├── application/ → WicketApplication
    ├── component/   → Panels (table, form, modal, shared)
    ├── model/       → FormModels (mutable beans para Wicket)
    ├── page/        → Pages (HomePage, DetalhePages)
    ├── provider/    → DataProviders com LoadableDetachableModel
    └── util/        → ByteArrayResourceStream
```

## Índice da Documentação

| Documento | Conteúdo |
|-----------|----------|
| [01_ARCHITECTURE_OVERVIEW](01_ARCHITECTURE_OVERVIEW.md) | Visão geral da arquitetura com diagrama de componentes |
| [02_ENTITY_MODEL](02_ENTITY_MODEL.md) | Entidades JPA, hierarquia de herança, relacionamentos |
| [03_CONTROLLERS_API](03_CONTROLLERS_API.md) | Endpoints REST, controllers e OpenAPI/Swagger |
| [04_SERVICE_LAYER](04_SERVICE_LAYER.md) | Service layer, interfaces segregadas (CQRS pattern) |
| [05_DTO_MAPPER_LAYER](05_DTO_MAPPER_LAYER.md) | DTOs, MapStruct mappers e fluxo de transformação |
| [06_WICKET_PAGES](06_WICKET_PAGES.md) | Páginas Wicket, navegação e componentes de UI |
| [07_WICKET_MODELS](07_WICKET_MODELS.md) | CompoundPropertyModel, LoadableDetachableModel, FormModels |
| [08_AJAX_INTERACTIONS](08_AJAX_INTERACTIONS.md) | Ajax behaviors, validação em tempo real e feedback |
| [09_DATAFLOW_SEQUENCES](09_DATAFLOW_SEQUENCES.md) | Diagramas de sequência para fluxos completos |
| [10_REPOSITORY_FLYWAY](10_REPOSITORY_FLYWAY.md) | Repositories, Flyway migrations e SQL |
