# Arquitetura Geral

## Diagrama de Componentes

```mermaid
graph TB
    subgraph "Presentation Layer (Wicket)"
        HP[HomePage]
        CDP[ClienteDetalhePage]
        TP[TablePanels]
        MD[CreateModals]
        RF[RowUpdateForms]
        EP[EnderecoPanels]
    end

    subgraph "REST API Layer"
        CFC[ClienteFisicoController]
        CJC[ClienteJuridicoController]
        EC[EnderecoController]
        EXC[ExportController]
    end

    subgraph "Service Layer"
        ACS[AbstractClienteService]
        CFS[ClienteFisicoService]
        CJS[ClienteJuridicoService]
        ES[EnderecoService]
        EXS[ExportService]
        JRS[JasperReportService]
    end

    subgraph "Mapper Layer (MapStruct)"
        CFM[ClienteFisicoMapper]
        CJM[ClienteJuridicoMapper]
        EM[EnderecoMapper]
    end

    subgraph "Data Access Layer"
        CFR[ClienteFisicoRepository]
        CJR[ClienteJuridicoRepository]
        ER[EnderecoRepository]
    end

    subgraph "Database"
        DB[(MariaDB)]
    end

    subgraph "Domain Model"
        C[Cliente - abstract]
        CF[ClienteFisico]
        CJ[ClienteJuridico]
        E[Endereco]
    end

    %% Wicket → Service
    TP -->|@SpringBean| CFS
    TP -->|@SpringBean| CJS
    TP -->|@SpringBean| EXS
    MD -->|@SpringBean| CFS
    MD -->|@SpringBean| CJS
    RF -->|@SpringBean| CFS
    RF -->|@SpringBean| CJS
    EP -->|@SpringBean| ES
    EP -->|@SpringBean| EXS
    CDP -->|@SpringBean| CFS
    CDP -->|@SpringBean| CJS

    %% Controller → Service
    CFC --> CFS
    CJC --> CJS
    EC --> ES
    EXC --> EXS

    %% Service → Mapper
    CFS --> CFM
    CJS --> CJM
    ES --> EM

    %% Service → Repository
    CFS --> CFR
    CJS --> CJR
    ES --> ER

    %% Repository → Entity
    CFR --> CF
    CJR --> CJ
    ER --> E

    %% Inheritance
    CF -.->|extends| C
    CJ -.->|extends| C
    E -.->|ManyToOne| C

    %% Export
    EXS --> JRS
    JRS -->|JasperReports| DB

    %% Mapper → Entity / DTO
    CFM -.-> CF
    CJM -.-> CJ
    EM -.-> E
```

## Fluxo de Requisições

```mermaid
flowchart LR
    U[User] -->|HTTP| RC[REST Controller]
    U -->|Browser| WP[Wicket Page]

    RC -->|@RequestBody DTO| S[Service]
    WP -->|@SpringBean| S

    S -->|DTO Request| M[MapStruct Mapper]
    M -->|Entity| R[Repository]
    R -->|JPA| DB[(Database)]

    DB -->|Result| R
    R -->|Entity| M
    M -->|DTO Response| S
    S -->|Response| RC
    S -->|Response| WP

    RC -->|JSON| U
    WP -->|HTML/JS| U
```

## Separação de Responsabilidades

```mermaid
flowchart TD
    subgraph "REST API"
        direction LR
        DTO_IN[CreateRequest/UpdateRequest\nrecords imutáveis]
        CTRL[Controller\nvalida @Valid]
        DTO_OUT[Response/ListResponse\nrecords imutáveis]
        DTO_IN -->|@Valid| CTRL
        CTRL -->|Service call| DTO_OUT
    end

    subgraph "Wicket Frontend"
        direction LR
        FM[FormModel\nbeans mutáveis]
        WFORM[Wicket Form\nCompoundPropertyModel]
        DTO_W_OUT[DTO\ncriado no onSubmit]
        WFORM -->|bind| FM
        WFORM -->|onSubmit| DTO_W_OUT
    end

    subgraph "Service"
        MAP[MapStruct Mapper\nEntity ↔ DTO]
        BUS[Business Logic\nvalidações + regras]
        REP[Repository\nSpring Data JPA]
        MAP --> BUS
        BUS --> REP
    end
```

## Camadas e Padrões

| Camada | Padrão | Descrição |
|--------|--------|-----------|
| REST API | Controller → Service | Endpoints REST com Swagger/OpenAPI |
| Wicket | Page → Panel → Form | Componentes Wicket com modelos próprios |
| Service | Interface segregada | Query (read) + Lifecycle (write) separados |
| Mapper | MapStruct | Conversão Entity ↔ DTO em compile-time |
| DTO | `record` imutável | Sem lógica de negócio, apenas dados |
| FormModel | POJO mutável | Adaptador Wicket para CompoundPropertyModel |
| Repository | Spring Data JPA | Acesso a dados com paginação |
