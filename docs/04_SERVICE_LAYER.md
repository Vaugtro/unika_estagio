# Service Layer

## Hierarquia de Interfaces e Classes

```mermaid
classDiagram
    class ClienteFisicoQueryService {
        <<interface>>
        +findById(Long) ClienteFisicoResponse
        +findByIdList(Long) ClienteFisicoListResponse
        +findAll(Pageable) Page~ClienteFisicoListResponse~
        +findAllActive(Pageable) Page~ClienteFisicoListResponse~
        +findAllForReport(Pageable) Page~ClienteFisicoReportResponse~
        +findByCpf(String) ClienteFisicoResponse
        +existsByCpf(String) boolean
        +count() long
    }

    class ClienteFisicoLifecycleService {
        <<interface>>
        +create(ClienteFisicoCreateRequest) ClienteFisicoResponse
        +update(Long, ClienteFisicoUpdateRequest) ClienteFisicoResponse
        +delete(Long)
        +hardDelete(Long)
        +activate(Long)
        +inactivate(Long)
    }

    class ClienteFisicoService {
        <<interface>>
    }

    class AbstractClienteService~T, R~ {
        <<abstract>>
        #R repository
        +findModelById(Long) T
        +delete(Long)
        +activate(Long)
        +inactivate(Long)
        +hardDelete(Long)
        +count() long
        #ensureIsActive(T)
        #getEntityName() String*
    }

    class ClienteFisicoServiceImpl {
        -ClienteFisicoMapper mapper
        -EnderecoService enderecoService
        +create(ClienteFisicoCreateRequest) ClienteFisicoResponse
        +update(Long, ClienteFisicoUpdateRequest) ClienteFisicoResponse
        +findById(Long) ClienteFisicoResponse
        +findAll(Pageable) Page~ClienteFisicoListResponse~
        +findAllActive(Pageable) Page~ClienteFisicoListResponse~
        +findAllForReport(Pageable) Page~
    }

    class EnderecoService {
        <<interface>>
        +create(EnderecoCreateRequest) EnderecoResponse
        +createForCliente(Long, EnderecoWithinClienteCreateRequest) EnderecoResponse
        +findById(Long) EnderecoResponse
        +findAllByClienteId(Long, Pageable) Page
        +findPrincipalByClienteId(Long) EnderecoResponse
        +update(Long, EnderecoUpdateRequest) EnderecoResponse
        +setAsPrincipal(Long) EnderecoResponse
        +delete(Long)
        +deleteAllByClienteId(Long)
    }

    ClienteFisicoService --|> ClienteFisicoQueryService : extends
    ClienteFisicoService --|> ClienteFisicoLifecycleService : extends
    ClienteFisicoServiceImpl --|> AbstractClienteService : extends
    ClienteFisicoServiceImpl ..|> ClienteFisicoService : implements
    AbstractClienteService --> ClienteFisicoServiceImpl : "T=ClienteFisico, R=ClienteFisicoRepository"
```

## CQRS no Service Layer

As interfaces de serviço seguem o padrão **CQRS lógico** (segregação de interfaces, não de bancos):

```mermaid
flowchart LR
    subgraph "Query (Read)"
        QI[Cliente*QueryService\nfindById, findAll, findAllActive\nfindByCpf, existsByCpf, count]
    end
    subgraph "Lifecycle (Write)"
        LI[Cliente*LifecycleService\ncreate, update, delete\nactivate, inactivate, hardDelete]
    end
    subgraph "Service Interface"
        SI[Cliente*Service]
    end
    subgraph "Implementation"
        IMPL[Cliente*ServiceImpl\n@Service @Transactional]
    end

    QI --> SI
    LI --> SI
    SI --> IMPL
    IMPL -->|mapper.toEntity| MAP[MapStruct Mapper]
    IMPL -->|repository.save| REPO[Repository]
    IMPL -->|mapper.toResponse| DTO
```

## Métodos do AbstractClienteService

```mermaid
stateDiagram-v2
    [*] --> ACTIVE : create()
    ACTIVE --> INACTIVE : inactivate(id)
    INACTIVE --> ACTIVE : activate(id)
    INACTIVE --> DELETED : hardDelete(id)
    ACTIVE --> DELETED : delete() → inactivate()

    note right of ACTIVE
        estaAtivo = true
        Pode ser editado
    end note

    note right of INACTIVE
        estaAtivo = false
        Soft delete: dados preservados
    end note
```

A classe `AbstractClienteService<T, R>` fornece:

| Método | Visibilidade | `@Transactional` | Descrição |
|--------|-------------|------------------|-----------|
| `findModelById(Long)` | `public` | readOnly | Busca entidade ou lança `ResourceNotFoundException` |
| `ensureIsActive(T)` | `protected` | — | Verifica se entidade está ativa, senão lança `BusinessException` |
| `delete(Long)` | `public` | sim | Chama `inactivate(id)` — soft delete |
| `activate(Long)` | `public` | sim | Ativa cliente, valida se já não está ativo |
| `inactivate(Long)` | `public` | sim | Inativa cliente, valida se já não está inativo |
| `hardDelete(Long)` | `public` | sim | Remove fisicamente do banco |
| `count()` | `public` | readOnly | Total de registros |

## Fluxo: Criação de ClienteFisico

```mermaid
sequenceDiagram
    participant CTRL as ClienteFisicoController
    participant SRV as ClienteFisicoServiceImpl
    participant MAP as ClienteFisicoMapper
    participant REPO as ClienteFisicoRepository
    participant END_SRV as EnderecoServiceImpl
    participant DB as Database

    CTRL->>SRV: create(request)
    Note over SRV: Valida CPF único (existsByCpf)
    Note over SRV: Valida endereços (pelo menos 1, 1 principal)

    SRV->>MAP: toEntity(sanitizedRequest)
    MAP-->>SRV: ClienteFisico entity (sem endereços)

    SRV->>REPO: save(entity)
    REPO->>DB: INSERT cliente + cliente_fisico
    DB-->>REPO: savedModel (com ID)

    loop cada endereço no request
        SRV->>END_SRV: createForCliente(savedModel.id, enderecoRequest)
        END_SRV->>END_SRV: Mapper → Endereco entity
        END_SRV->>END_SRV: setCliente(savedModel)
        END_SRV->>END_SRV: repository.save(endereco)
    end

    SRV->>MAP: toResponse(savedModel)
    MAP-->>SRV: ClienteFisicoResponse
    SRV-->>CTRL: Response DTO
```
