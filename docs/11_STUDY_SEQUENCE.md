# Estudo: Ajax ↔ DTO ↔ Service ↔ CompoundPropertyModel ↔ DetachableModel

## Mapa Conceitual

```mermaid
mindmap
  root(("Wicket + Spring"))
    Ajax
      AjaxButton
      AjaxLink
      AjaxFormComponentUpdatingBehavior
      AjaxPagingNavigator
    CompoundPropertyModel
        ("bind: Componente a Propriedade")
        ("getObject → getters")
        ("setObject → setters")
        ("Requer POJO mutavel")
    DTO
        ("record imutavel")
      CreateRequest
      UpdateRequest
      ListResponse
      ReportResponse
    Service
        ("Transactional")
        ("QueryService (read)")
        ("LifecycleService (write)")
    LoadableDetachableModel
        ("load -> Service")
        ("detach -> libera")
        ("lazy loading")
```

## O Problema que estas 5 Peças Resolvem

```mermaid
flowchart LR
    subgraph "Browser"
        HTML[Formulário HTML]
        AJAX[Ajax Request]
    end

    subgraph "Wicket (Server)"
        CPM[CompoundPropertyModel<br/>bind automático<br/>nome → getNome/setNome]
        LDM[LoadableDetachableModel<br/>lazy-load entre requests<br/>detach após render]

        FM[FormModel<br/>POJO mutável<br/>serializável]
        DTO[DTO record<br/>imutável<br/>com validação]
    end

    subgraph "Spring"
        SRV[Service<br/>@Transactional<br/>regras de negócio]
        MAP[MapStruct Mapper<br/>Entity ↔ DTO]
    end

    subgraph "Database"
        DB[(MariaDB)]
    end

    HTML -->|submit| AJAX
    AJAX -->|onSubmit| CPM
    CPM -->|getObject| LDM
    LDM -->|load| SRV
    SRV -->|retorna| DTO
    DTO -->|constrói| FM
    CPM -->|bind| FM

    FM -->|manual convert| DTO
    DTO -->|create/update| SRV
    SRV -->|save| DB
```

## Diagrama Central: Lifecycle Completo (Render → Interação → Submit → Resposta)

```mermaid
sequenceDiagram
    box rgb(200, 220, 240) Wicket Page
        participant WPage as Wicket Page
        participant CPM as Compound<br/>PropertyModel
        participant LDM as Loadable<br/>DetachableModel
        participant FORM as Wicket Form
        participant AJX as AjaxButton
    end
    box rgb(220, 240, 200) Data Layer
        participant FM as FormModel<br/>(mutable POJO)
        participant DTO_IN as Request DTO<br/>(imutable record)
        participant DTO_OUT as Response DTO<br/>(imutable record)
    end
    box rgb(240, 220, 200) Spring Service
        participant SRV as Service<br/>@Transactional
    end

    Note over WPage,SRV: ════════════ FASE 1: RENDER (Page Load) ════════════

    WPage->>CPM: new CompoundPropertyModel<>(detachedModel)
    WPage->>FORM: add(form with CPM model)

    Note over WPage,LDM: O DetachableModel é apenas instanciado, NÃO carregado

    CPM->>LDM: getObject() ← Wicket precisa renderizar
    activate LDM
    Note over LDM: LoadableDetachableModel.load()
    LDM->>SRV: findById(id)
    SRV->>DTO_OUT: new Cliente*Response(id, nome, ...)
    DTO_OUT-->>SRV: record (imutável)
    SRV-->>LDM: Response DTO
    LDM->>LDM: new Cliente*UpdateFormModel(response)
    Note over LDM: Converte Response record → FormModel POJO
    LDM-->>CPM: Cliente*UpdateFormModel (mutável!)
    deactivate LDM

    Note over CPM,FM: CompoundPropertyModel agora tem o objeto

    CPM->>FM: getNome() ← Wicket chama para renderizar TextField
    FM-->>CPM: "João Silva"

    CPM->>FM: getEmail()
    FM-->>CPM: "joao@email.com"

    CPM->>FM: getEstaAtivo()
    FM-->>CPM: true

    Note over LDM: Fim do request → LDM.detach()
    Note over LDM: Modelo marcado como DETACHED

    Note over WPage,SRV: ════════════ FASE 2: USER INTERACTION ════════════

    Note over FORM: Usuário edita campos no navegador
    Note over FM: CompoundPropertyModel chama setters automaticamente
    CPM->>FM: setNome("João S.")
    CPM->>FM: setEmail("joao@novo.com")
    Note over FM: Wicket atualiza o FormModel EM MEMÓRIA<br/>(não chama LDM.load() ainda)

    Note over WPage,SRV: ════════════ FASE 3: AJAX SUBMIT ════════════

    FORM->>AJX: onSubmit(target, form)
    activate AJX

    AJX->>CPM: form.getModelObject()
    CPM->>LDM: getObject()
    Note over CPM,LDM: Retorna objeto ATTACHED (já em memória)<br/>com as alterações do usuário
    LDM-->>AJX: FormModel (mutado)

    Note over AJX,DTO_IN: ═══ CONVERSÃO MANUAL: FormModel → Request DTO ═══

    AJX->>DTO_IN: new ClienteFisicoUpdateRequest(
    Note over DTO_IN:     model.getNome(),    ← "João S."
    Note over DTO_IN:     model.getEmail(),   ← "joao@novo.com"
    Note over DTO_IN:     model.getEstaAtivo() ← true
    Note over DTO_IN: )
    Note over AJX,DTO_IN: FormModel (POJO mutável) → Request DTO (record imutável)<br/>A conversão é EXPLÍCITA no onSubmit()

    deactivate AJX

    AJX->>SRV: update(id, request DTO)
    activate SRV

    Note over SRV: ═══ SERVIÇO: valida + persiste ═══

    SRV->>SRV: findModelById(id) ← managed entity
    SRV->>SRV: mapper.updateEntity(request, entity)
    Note over SRV: Apenas campos não-nulos são copiados
    SRV->>SRV: repository.save(entity)
    SRV->>DTO_OUT: mapper.toResponse(entity)
    DTO_OUT-->>SRV: Response DTO (record)
    SRV-->>AJX: Response DTO
    deactivate SRV

    Note over AJX,FM: ═══ ATUALIZA FormModel com dados frescos ═══

    AJX->>FM: new Cliente*UpdateFormModel(response)
    Note over FM: Cria NOVO FormModel a partir do Response DTO
    AJX->>CPM: setDefaultModelObject(novoFM)
    Note over CPM,FM: CompoundPropertyModel agora aponta para<br/>o FormModel ATUALIZADO

    AJX->>FORM: target.add(form) ← re-renderiza o form no browser
    AJX->>FORM: showToast("success", "Cliente atualizado!")
```

## O Ciclo de Vida do LoadableDetachableModel

```mermaid
stateDiagram-v2
    state "DETACHED" as DET
    state "LOADING" as LD
    state "ATTACHED" as ATT

    [*] --> DET : instanciação

    DET --> LD : getObject() chamado
    Note right of DET: Fim de um request<br/>ou início de outro

    LD --> ATT : load() retorna
    Note right of LD: Service.findById()<br/>Converte Response → FormModel

    ATT --> DET : detach() (fim do request)
    Note right of ATT: FormModel fica em memória<br/>durante todo o request

    DET --> LD : próximo request → load() novamente

    state ATT {
        [*] --> EM_MEMORIA
        EM_MEMORIA --> [*]
    }
    Note left of ATT : getObject() retorna<br/>objeto em cache<br/>SEM chamar service
```

### Onde o DTO entra no ciclo?

```mermaid
flowchart LR
    subgraph "LOAD() — DETACHED → ATTACHED"
        LDM[LoadableDetachableModel.load]
        SRV[Service.findById]
        RESP[Response DTO<br/>record]
        FM[FormModel<br/>POJO]
        LDM --> SRV
        SRV --> RESP
        RESP -->|"new FormModel(response)"| FM
        FM -->|"setModelObject"| CPM1[CompoundPropertyModel]
    end

    subgraph "Durante o Request — ATTACHED"
        CPM2[CompoundPropertyModel]
        USR[User edits fields]
        USR -->|setNome| CPM2
        CPM2 -->|getObject| FM2[FormModel cached]
    end

    subgraph "onSubmit — Ajax"
        FM3[FormModel<br/>com edits]
        REQ[Request DTO<br/>record]
        SRV2[Service.update]
        RESP2[Response DTO]
        FM4[Novo FormModel]
        FM3 -->|conversão manual| REQ
        REQ --> SRV2
        SRV2 --> RESP2
        RESP2 -->|new FormModel| FM4
    end
```

## Por que a Separação FormModel ↔ DTO Existe?

```mermaid
flowchart LR
    subgraph "Wicket Precisa (CompoundPropertyModel)"
        CPMP[CompoundPropertyModel]
        CPMP -->|"chama"| GET[getNome()]
        CPMP -->|"chama"| SET[setNome("João")]
        GET -->|"precisa de"| GETTER[getter real<br/>não .nome()]
        SET -->|"precisa de"| SETTER[setter real<br/>não .nome = x]
    end

    subgraph "DTO record (imutável)"
        REC[record ClienteFisicoResponse]
        REC -.->|"❌"| GETTER
        REC -.->|"❌"| SETTER
        REC --> APENAS[.nome() accessor<br/>sem setter]
    end

    subgraph "FormModel POJO (mutável)"
        POJO[class ClienteFisicoUpdateFormModel]
        POJO -->|"✅"| GETTER
        POJO -->|"✅"| SETTER
        POJO --> PROP[propriedade editável]
    end

    DTO[DTO record] -.->|fonte de dados| FORM[FormModel]
    FORM -->|bind| CPM[CompoundPropertyModel<br/>funciona!]

    note[DTOs são para TRANSPORTE<br/>FormModels são para EDIÇÃO<br/>Wicket CompoundPropertyModel<br/>exige setters mutáveis]
```

## Mapa: Instância vs Tipo (exemplo ClienteFisico)

```mermaid
flowchart TB
    subgraph "1. Render"
        SRV1[Service.findById(1)]
        SRV1 -->|"retorna"| RESP1["ClienteFisicoResponse<br/>id=1, nome='João',<br/>cpf='123.456.789-01',<br/>email='joao@email.com',<br/>estaAtivo=true"]
        RESP1 -->|"constrói"| FM1["ClienteFisicoUpdateFormModel<br/>id=1, nome='João',<br/>cpf='123.456.789-01',<br/>email='joao@email.com',<br/>estaAtivo=true"]
        FM1 -->|"bind via CPM"| TXT1["TextField nome = 'João'"]
        FM1 -->|"bind via CPM"| TXT2["TextField email = 'joao@email.com'"]
    end

    subgraph "2. Edição"
        TXT1 -->|"usuário edita"| TXT1E["TextField nome = 'João S.'"]
        TXT2 -->|"usuário edita"| TXT2E["TextField email = 'joao@novo.com'"]
        TXT1E -->|"CPM.setNome()"| FM2["FormModel<br/>nome='João S.'"]
        TXT2E -->|"CPM.setEmail()"| FM2
    end

    subgraph "3. Submit Ajax"
        FM2 -->|"getModelObject()"| AJAXIN["AjaxButton.onSubmit"]
        AJAXIN -->|"converte"| UPDREQ["ClienteFisicoUpdateRequest<br/>nome='João S.'<br/>email='joao@novo.com'<br/>estaAtivo=true"]
        UPDREQ -->|"Service.update()"| SRV2[ClienteFisicoServiceImpl]
        SRV2 -->|"atualiza"| DB[(Database)]
        DB --> SRV2
        SRV2 -->|"retorna"| RESP2["Novo ClienteFisicoResponse<br/>nome='João S.',<br/>email='joao@novo.com'"]
        RESP2 -->|"novo FormModel"| FM3["Novo ClienteFisicoUpdateFormModel<br/>nome='João S.'<br/>email='joao@novo.com'"]
        FM3 -->|"CPM.setDefaultModelObject"| TXT1R["TextField re-renderizado"]
    end

    subgraph "4. Fim Request"
        FM3 -->|"LDM.detach()"| DET["LoadableDetachableModel<br/>→ DETACHED"]
        DET -->|"próximo request"| SRV1
    end
```

## Tabela Resumo: Responsabilidades

| Componente | Tipo | Mutável? | Função | Onde é criado |
|-----------|------|----------|--------|---------------|
| `LoadableDetachableModel` | abstract class | — | Lazy-load entre requests | `RowUpdateForm`, `AbstractClienteDataProvider` |
| `CompoundPropertyModel` | Wicket Model | — | Bind automático campo ↔ propriedade | `Form` constructor |
| `FormModel` | POJO | ✅ Sim | Adaptador para CPM | convertido de `Response DTO` |
| `Request DTO` | `record` | ❌ Não | Transporte de dados p/ service | convertido de `FormModel` no `onSubmit` |
| `Response DTO` | `record` | ❌ Não | Dados de saída do service | `Service.findById/update/create` |
| `Service` | `@Service` | — | Lógica de negócio + persistência | Spring container |
| `AjaxButton` | Wicket Behavior | — | Submissão assíncrona | `Form.add(new AjaxButton(...))` |

## Ordem dos Eventos (Render + Submit)

```mermaid
flowchart TD
    subgraph "RENDER (primeiro request)"
        R1[Page instancia Form] --> R2[CRIA LoadableDetachableModel]
        R2 --> R3[CRIA CompoundPropertyModel wrapping LDM]
        R3 --> R4[Wicket chama getObject]
        R4 --> R5[LDM.load → Service.findById → Response DTO]
        R5 --> R6[CONVERTE Response → FormModel]
        R6 --> R7[CPM bind: TextField → FormModel.getNome]
    end

    subgraph "AJAX SUBMIT"
        S1[Usuário clica AjaxButton] --> S2[CPM.getObject → LDM retorna FormModel cached]
        S2 --> S3[CONVERTE FormModel → Request DTO]
        S3 --> S4[Service.update(Request DTO)]
        S4 --> S5[Service retorna Response DTO]
        S5 --> S6[CONVERTE Response → Novo FormModel]
        S6 --> S7[CPM.setDefaultModelObject(novo FormModel)]
        S7 --> S8[target.add(form) → re-renderiza]
    end

    subgraph "DETACH (fim do request)"
        D1[LDM.detach] --> D2[Referência liberada]
        D2 --> D3[Próximo request: load novamente]
    end

    R7 --> S1
    S8 --> D1
```

## Padrão: O Fluxo Circular dos Dados

```
                 ┌─────────────────────────────────────────────────────┐
                 │                                                     │
                 │    Service (Spring)                                 │
                 │    ┌──────────────────────┐                        │
                 │    │  create/dto → Entity │                        │
                 │    │  Entity → Response   │                        │
                 │    └──────┬───────────────┘                        │
                 │           │                                        │
                 │           ▼                                        │
                 │    ┌──────────────────────┐                        │
                 │    │   Response DTO       │                        │
                 │    │   (record imutável)   │                        │
                 │    └──────┬───────────────┘                        │
                 │           │                                        │
                 │           ▼                                        │
    Ajax ──────► │    ┌──────────────────────┐                        │
    Button       │    │   FormModel (POJO)   │◄──── LoadableDetachable ────┐
    onSubmit     │    │   mutável para CPM   │       Model.load()     │    │
    │            │    └──────┬───────────────┘                        │    │
    │            │           │                                        │    │
    │            │           ▼                                        │    │
    │            │    ┌──────────────────────┐                        │    │
    └───────────────►│   Request DTO         │                        │    │
                     │   (record imutável)   │                        │    │
                     └──────┬───────────────┘                        │    │
                            │                                        │    │
                            ▼                                        │    │
                     ┌──────────────────────┐                       │    │
                     │   Service.create/    │───────────────────────┘    │
                     │   update(RequestDTO) │                            │
                     └──────────────────────┘                            │
                                                                         │
                    ┌────────────────────────────────────────────┐        │
                    │  LoadableDetachableModel lifecycle          │────────┘
                    │  DETACHED → LOAD (next request)             │
                    │          ↓                                 │
                    │  ATTACHED (durante request)                 │
                    │          ↓                                 │
                    │  DETACHED (fim do request)                  │
                    └────────────────────────────────────────────┘
```
