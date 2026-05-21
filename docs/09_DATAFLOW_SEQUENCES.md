# Diagramas de Sequência — Fluxos Completos

## Fluxo 1: Criar ClienteFisico (Wicket → Service → DB)

```mermaid
sequenceDiagram
    actor User
    participant HP as HomePage
    participant CT as ClientesFisicosTablePanel
    participant MD as ClienteFisicoCreateModal
    participant FM as ClienteFisicoCreateFormModel
    participant AJX as AjaxButton (submit)
    participant SRV as ClienteFisicoServiceImpl
    participant MAP as ClienteFisicoMapper
    participant REPO as ClienteFisicoRepository
    participant END_SRV as EnderecoServiceImpl
    participant END_REPO as EnderecoRepository
    participant DB as MariaDB
    participant TOAST as Toast (JS)

    User->>HP: Clica "Físicos"
    HP->>CT: showFisicosPanel(null)
    CT->>MD: add(createModal)

    User->>MD: Preenche form (CPF, nome, rg, data)
    User->>MD: Preenche endereço (logradouro, cep, etc.)
    User->>MD: Clica "Salvar"

    MD->>AJX: onSubmit(target, form)

    AJX->>FM: form.getModelObject()
    FM-->>AJX: ClienteFisicoCreateFormModel

    AJX->>AJX: Converte endereços: EnderecoCreateFormModel → EnderecoWithinClienteCreateRequest
    Note over AJX: Limpa CPF: replaceAll("\\D","")
    Note over AJX: Parse dataNascimento: LocalDate.parse()

    AJX->>AJX: Cria ClienteFisicoCreateRequest

    AJX->>SRV: create(request)

    SRV->>SRV: validateCpfUniqueness(cpf)
    SRV->>SRV: Valida endereços (≥1, algum principal)
    SRV->>MAP: toEntity(sanitizedRequest)
    MAP-->>SRV: ClienteFisico entity

    SRV->>REPO: save(entity)
    REPO->>DB: INSERT INTO cliente (tipo, email, ...)
    DB-->>REPO: id gerado
    REPO->>DB: INSERT INTO cliente_fisico (id, cpf, nome, ...)
    DB-->>REPO: saved entity
    REPO-->>SRV: ClienteFisico (com ID)

    loop each endereço
        SRV->>END_SRV: createForCliente(savedModel.id, enderecoDTO)
        END_SRV->>END_SRV: Mapper → Endereco entity
        END_SRV->>END_SRV: entity.setCliente(savedModel)
        END_SRV->>END_REPO: save(endereco)
        END_REPO->>DB: INSERT INTO endereco (...)
        DB-->>END_REPO: saved endereco
        END_REPO-->>END_SRV: Endereco
        END_SRV-->>SRV: EnderecoResponse
    end

    SRV->>MAP: toResponse(savedModel)
    MAP-->>SRV: ClienteFisicoResponse

    SRV-->>AJX: ClienteFisicoResponse

    AJX->>AJX: Limpa form model
    Note over AJX: model.setCpf(null)<br/>model.setNome(null)<br/>...<br/>enderecos.clear()

    AJX->>TOAST: showToast("success", "Cliente criado com sucesso!")
    AJX->>MD: target.add(form)
    AJX->>MD: target.appendJavaScript("lucide.createIcons()")
```

## Fluxo 2: Editar Cliente Inline (RowUpdateForm)

```mermaid
sequenceDiagram
    actor User
    participant RF as ClienteFisicoRowUpdateForm
    participant LDM as LoadableDetachableModel
    participant CPM as CompoundPropertyModel
    participant AJX as AjaxButton (editarBtn)
    participant SRV as ClienteFisicoServiceImpl
    participant MAP as ClienteFisicoMapper
    participant REPO as ClienteFisicoRepository
    participant DB as MariaDB

    Note over User,DB: --- RENDER: Carregamento inicial ---

    RF->>LDM: new LoadableDetachableModel<>()
    Note over RF,LDM: Apenas cria o modelo (não carrega)

    CPM->>CPM: setModel(detachedModel)

    Note over User,DB: --- RENDER: getObject() é chamado ---

    CPM->>LDM: getObject() (= load())
    LDM->>SRV: findById(clienteId)
    SRV->>MAP: toResponse(entity)
    MAP-->>SRV: ClienteFisicoResponse
    SRV-->>LDM: ClienteFisicoResponse
    LDM->>LDM: new ClienteFisicoUpdateFormModel(response)
    LDM-->>CPM: ClienteFisicoUpdateFormModel

    CPM->>CPM: PropertyResolver: "nome" → getNome()

    Note over User,DB: --- INTERAÇÃO DO USUÁRIO ---

    User->>RF: Altera campo "nome" para "João S."
    User->>RF: Altera campo "email" para "joao@novo.com"
    User->>RF: Clica "Editar" (AjaxButton)

    RF->>AJX: onSubmit(target, form)

    AJX->>CPM: getModelObject()
    CPM->>LDM: getObject()
    Note over CPM,LDM: Retorna objeto já em memória (ATTACHED)
    LDM-->>AJX: ClienteFisicoUpdateFormModel (atualizado)

    AJX->>AJX: new ClienteFisicoUpdateRequest(model.getNome(), model.getEmail(), model.getEstaAtivo())

    AJX->>SRV: update(id, updateRequest)
    SRV->>SRV: findModelById(id) → entity managed
    SRV->>MAP: updateEntity(request, @MappingTarget entity)
    MAP-->>SRV: entity (parcialmente atualizado)
    SRV->>REPO: save(entity)
    REPO->>DB: UPDATE cliente SET nome=?, email=? WHERE id=?
    DB-->>REPO: OK
    REPO-->>SRV: updated entity
    SRV->>MAP: toResponse(updated)
    MAP-->>SRV: ClienteFisicoResponse

    SRV-->>AJX: ClienteFisicoResponse

    AJX->>AJX: new ClienteFisicoUpdateFormModel(response)
    AJX->>CPM: setDefaultModelObject(novoModel)
    AJX->>RF: target.add(form)
    AJX->>User: showToast("success", "Cliente atualizado!")
```

## Fluxo 3: Toggle Ativar/Inativar

```mermaid
sequenceDiagram
    actor User
    participant RF as RowUpdateForm
    participant TB as AjaxLink (statusBtn)
    participant LDM as LoadableDetachableModel
    participant SRV as AbstractClienteService
    participant DB as Database

    Note over User,DB: Estado atual: estaAtivo = true (Ativo)

    User->>TB: Clica botão "Ativo" (verde)

    TB->>LDM: getObject()
    LDM-->>TB: UpdateFormModel

    TB->>TB: model.getEstaAtivo() = true

    alt is Ativo → Inativar
        TB->>SRV: inactivate(id)
        SRV->>SRV: findModelById(id)
        SRV->>SRV: model.setEstaAtivo(false)
        SRV->>DB: UPDATE cliente SET esta_ativo=0
        DB-->>SRV: OK
        SRV-->>TB: void
        TB->>TB: model.setEstaAtivo(false)
    else is Inativo → Ativar
        TB->>SRV: activate(id)
        SRV->>SRV: findModelById(id)
        SRV->>SRV: model.setEstaAtivo(true)
        SRV->>DB: UPDATE cliente SET esta_ativo=1
        DB-->>SRV: OK
        SRV-->>TB: void
        TB->>TB: model.setEstaAtivo(true)
    end

    TB->>RF: target.add(form)
    Note over RF: O botão muda de cor:<br/>verde → "Ativo" (btn-success)<br/>vermelho → "Inativo" (btn-danger)

    TB->>User: appendJS: lucide.createIcons()
```

## Fluxo 4: Navegação entre Páginas Wicket

```mermaid
sequenceDiagram
    actor User
    participant HP as HomePage
    participant TP as TablePanel
    participant DP as DataProvider
    participant SRV as Service
    participant NAV as AjaxPagingNavigator

    User->>HP: Abre / (HomePage)

    HP->>TP: new ClientesFisicosTablePanel("currentPanel")
    TP->>DP: new ClienteFisicoDataProvider(service)
    TP->>TP: new ClienteFisicoDataView("rows", provider, 10)

    Note over HP,SRV: Renderiza página 1

    User->>NAV: Clica página 2
    NAV->>TP: onPageChanged
    TP->>DP: iterator(10, 10)  -- second page
    DP->>SRV: findAll(PageRequest.of(1, 10))
    SRV-->>DP: Page (page 2)
    DP-->>TP: Iterator<ClienteFisicoListResponse>
    TP->>User: target.add(tableContainer)
    TP->>User: target.add(navigator)

    User->>HP: Clica "Pessoas Jurídicas"
    HP->>TP: remove tableContainer
    HP->>TP: addOrReplace(ClientesJuridicosTablePanel)
    TP->>User: target.add(panelContainer)

    User->>TP: Clica "Detalhes" em um cliente
    TP->>User: BookmarkablePageLink → /clientes/detalhe/123
    User->>User: Navega para Cliente*DetalhePage

    User->>DP: Clica "← Voltar"
    DP->>HP: BookmarkablePageLink → /
```

## Fluxo 5: Exportação (PDF/XLSX)

```mermaid
sequenceDiagram
    actor User
    participant TP as TablePanel
    participant LINK as Link (exportBtn)
    participant EX_SRV as ExportService
    participant JRSRV as JasperReportService
    participant REPO as Repository
    participant DB as Database
    participant RC as RequestCycle

    User->>LINK: Clica "Exportar PDF"
    LINK->>EX_SRV: pdfFisicos()
    EX_SRV->>REPO: findAll()
    REPO->>DB: SELECT * FROM cliente + cliente_fisico
    DB-->>REPO: List<ClienteFisico>
    REPO-->>EX_SRV: entities
    EX_SRV->>JRSRV: generatePdfReport(data, "clientes-fisicos.jrxml")
    JRSRV-->>EX_SRV: byte[] pdf
    EX_SRV-->>LINK: byte[]

    LINK->>LINK: new ByteArrayResourceStream(bytes, "application/pdf")
    LINK->>RC: scheduleRequestHandlerAfterCurrent(
    RC->>RC: ResourceStreamRequestHandler(stream)
    RC->>User: Content-Disposition: attachment; filename=ClientesFisicosReport.pdf
    RC->>User: Content-Type: application/pdf
    Note over User: Browser faz download do PDF
```

## Fluxo 6: Requisição REST (API externa)

```mermaid
sequenceDiagram
    participant CLIENT as HTTP Client (Postman/curl)
    participant CTRL as Controller
    participant SRV as Service
    participant MAP as Mapper
    participant REPO as Repository
    participant DB as MariaDB

    CLIENT->>CTRL: GET /v1/clientes/fisicos?page=0&size=20&sort=id,asc
    Note over CTRL: @PageableDefault(sort="id", direction=ASC)

    CTRL->>SRV: findAll(pageable)
    SRV->>REPO: findAll(pageable)
    REPO->>DB: SELECT c.*, cf.* FROM cliente c
    DB-->>REPO: JOIN cliente_fisico cf ON c.pk=cf.pk
    DB-->>REPO: ORDER BY c.pk ASC LIMIT 20 OFFSET 0
    REPO-->>SRV: Page<ClienteFisico>
    SRV->>MAP: entity → toListResponse()
    MAP-->>SRV: ClienteFisicoListResponse
    SRV-->>CTRL: Page<ClienteFisicoListResponse>
    CTRL-->>CLIENT: 200 OK + JSON
    Note over CLIENT: {
    Note over CLIENT:   "content": [...],
    Note over CLIENT:   "page": 0,
    Note over CLIENT:   "size": 20,
    Note over CLIENT:   "totalElements": 150,
    Note over CLIENT:   "totalPages": 8
    Note over CLIENT: }
```

## Fluxo 7: REST Update (via RowUpdateForm)

```mermaid
sequenceDiagram
    participant U as User (Browser)
    participant WK as Wicket RowUpdateForm
    participant SRV as Service
    participant MAP as Mapper
    participant REPO as Repository
    participant DB as MariaDB

    U->>WK: Edita nome + email + status
    U->>WK: Clica AjaxButton "Editar"

    WK->>WK: getModelObject() → UpdateFormModel
    WK->>WK: Cria UpdateRequest DTO

    WK->>SRV: update(id, updateRequest)
    SRV->>SRV: findModelById(id) → managed entity
    SRV->>MAP: updateEntity(request, @MappingTarget entity)
    Note over MAP: nullValuePropertyMappingStrategy = IGNORE
    Note over MAP: Apenas campos não-nulos do request são setados

    SRV->>REPO: save(entity)  -- merge
    REPO->>DB: UPDATE cliente SET nome=?, email=? WHERE pk=?
    DB-->>REPO: 1 row affected
    REPO-->>SRV: updated entity

    SRV->>MAP: toResponse(updated)
    MAP-->>SRV: ClienteFisicoResponse
    SRV-->>WK: Response DTO

    WK->>WK: Cria novo UpdateFormModel(response)
    WK->>WK: setDefaultModelObject(novoModel)
    WK->>U: target.add(form) + showToast("success")
```
