# Ajax Behaviors e Interatividade

## Catálogo de Ajax Behaviors

```mermaid
graph TB
    subgraph "AjaxButton (submit)"
        AB1[ClienteFisicoCreateModal.submit] -->|onSubmit| S1[Service.create]
        AB2[ClienteJuridicoCreateModal.submit] -->|onSubmit| S2[Service.create]
        AB3[ClienteFisicoRowUpdateForm.editarBtn] -->|onSubmit| S3[Service.update]
        AB4[ClienteJuridicoRowUpdateForm.editarBtn] -->|onSubmit| S4[Service.update]
        AB5[EnderecoListViewPanel.salvarEnderecoBtn] -->|onSubmit| S5[Service.create/update]
    end

    subgraph "AjaxLink (navegação/ação)"
        AL1[HomePage.btnFisicos] -->|onClick| P1[showFisicosPanel]
        AL2[HomePage.btnJuridicos] -->|onClick| P2[showJuridicosPanel]
        AL3[ClienteFisicoRowUpdateForm.statusBtn] -->|onClick| T1[toggle active/inactive]
        AL4[ClienteJuridicoRowUpdateForm.statusBtn] -->|onClick| T2[toggle active/inactive]
        AL5[EnderecoCreateTablePanel.removeBtn] -->|onClick| R1[remove address row]
        AL6[EnderecoCreateTablePanel.addEndereco] -->|onClick| A1[add address row]
        AL7[EnderecoListViewPanel.editarBtn] -->|onClick| E1[open edit modal]
        AL8[EnderecoListViewPanel.excluirBtn] -->|onClick| D1[Service.delete]
        AL9[EnderecoListViewPanel.adicionarEnderecoBtn] -->|onClick| O1[open create modal]
    end

    subgraph "AjaxFormComponentUpdatingBehavior (validação)"
        AF1[ValidationFeedback.attachRealTimeValidation] -->|on blur| V1[target.add field + feedback]
    end

    subgraph "AjaxPagingNavigator"
        APN[navigator] -->|on click| PAG[dataView.setPage]
    end

    subgraph "Link (download)"
        LD1[exportPdfBtn] -->|onClick| EXP1[ExportService → ResourceStreamRequestHandler]
        LD2[exportXlsxBtn] -->|onClick| EXP2[ExportService → ResourceStreamRequestHandler]
    end
```

## Fluxo Ajax: Criação de Cliente (Modal)

```mermaid
sequenceDiagram
    participant U as User
    participant MODAL as Cliente*CreateModal
    participant FORM as Wicket Form
    participant AJAX as AjaxButton (submit)
    participant SRV as Service
    participant FEED as ValidationFeedback

    U->>MODAL: Preenche campos
    U->>MODAL: Clica "Salvar"

    MODAL->>AJAX: onSubmit(target, form)

    AJAX->>FORM: form.getModelObject()
    FORM-->>AJAX: Cliente*CreateFormModel

    AJAX->>AJAX: Converte FormModel → CreateRequest DTO
    Note over AJAX: Limpa CPF/CNPJ<br/>Parse data<br/>Converte endereços

    AJAX->>SRV: create(dto)

    alt Sucesso
        SRV-->>AJAX: Response DTO
        AJAX->>AJAX: Limpa formulário
        AJAX->>FORM: model.setCpf(null) ...
        AJAX->>U: showToast("success", "Cliente criado!")
        AJAX->>U: target.add(form)
        AJAX->>U: appendJS: lucide.createIcons()
    else Erro de Negócio
        SRV-->>AJAX: BusinessException
        AJAX->>U: showToast("error", e.getMessage())
    else Erro de Validação
        FORM-->>AJAX: onError(target, form)
        AJAX->>FEED: handleFormError(target, form)
        FEED->>U: highlightJS: marca campos inválidos
        FEED->>U: showToast("error", "Erro: ...")
    end
```

## Fluxo Ajax: Edição Inline (RowUpdateForm)

```mermaid
sequenceDiagram
    participant U as User
    participant FORM as Cliente*RowUpdateForm
    participant LDM as LoadableDetachableModel
    participant AJAX as AjaxButton (editarBtn)
    participant SRV as Service
    participant FEED as ValidationFeedback

    Note over U,FEED: Cenário: usuário edita nome + email na linha

    U->>FORM: Altera campo "nome"
    U->>FORM: Altera campo "email"
    U->>FORM: Clica "Editar"

    FORM->>AJAX: onSubmit(target, form)
    AJAX->>FORM: getModelObject()
    FORM-->>AJAX: Cliente*UpdateFormModel

    AJAX->>AJAX: Converte para UpdateRequest DTO
    AJAX->>SRV: update(id, updateRequest)

    alt Sucesso
        SRV-->>AJAX: Response DTO
        AJAX->>AJAX: Atualiza model com dados frescos
        AJAX->>FORM: setDefaultModelObject(new FormModel(response))
        AJAX->>U: showToast("success", "Atualizado!")
        AJAX->>U: target.add(form)
    else Erro
        SRV-->>AJAX: BusinessException
        AJAX->>U: showToast("error", e.getMessage())
    end

    alt Usuário clica "Ativar/Inativar"
        U->>FORM: Clica toggleBtn (AjaxLink)
        FORM->>AJAX: onClick(target)
        AJAX->>SRV: activate(id) ou inactivate(id)
        SRV-->>AJAX: void (204)
        AJAX->>AJAX: model.setEstaAtivo(newStatus)
        AJAX->>U: target.add(form)
        AJAX->>U: target.appendJS(lucide.createIcons())
    end
```

## Fluxo Ajax: Endereços (CRUD completo)

```mermaid
sequenceDiagram
    participant U as User
    participant ELV as EnderecoListViewPanel
    participant MODAL as Modal Form
    participant AJAX as AjaxButton
    participant SRV as EnderecoService
    participant CONT as enderecosContainer

    Note over U,CONT: --- CRIAR ---

    U->>ELV: Clica "Adicionar Endereço"
    ELV->>MODAL: modalEnderecos.clear()
    ELV->>MODAL: add(new EnderecoCreateFormModel())
    ELV->>U: target.add(modalForm)
    ELV->>U: appendJS: abrirModalEndereco()

    U->>MODAL: Preenche endereço
    U->>MODAL: Clica "Salvar"
    MODAL->>AJAX: onSubmit(target, form)
    AJAX->>AJAX: Converte formModel → DTO
    AJAX->>SRV: create(dto)  (ou update se tem ID)
    SRV-->>AJAX: EnderecoResponse
    AJAX->>ELV: modalEnderecos.clear()
    AJAX->>U: target.add(enderecosContainer) ← recarrega lista
    AJAX->>U: target.add(modalForm) ← limpa form
    AJAX->>U: appendJS: fecharModalEndereco()

    Note over U,CONT: --- EDITAR ---

    U->>ELV: Clica "Editar" em um endereço
    ELV->>MODAL: Carrega dados no formModel
    ELV->>MODAL: setModelObject(endereco)
    ELV->>U: target.add(modalForm)
    ELV->>U: appendJS: abrirModalEndereco()

    Note over U,CONT: --- EXCLUIR ---

    U->>ELV: Clica "Excluir" em um endereço
    ELV->>SRV: enderecoService.delete(endId)
    SRV-->>ELV: void
    ELV->>U: target.add(enderecosContainer) ← recarrega
    ELV->>U: showToast("success", "Endereço excluído!")
```

## Validação em Tempo Real (AjaxFormComponentUpdatingBehavior)

```mermaid
sequenceDiagram
    participant U as User
    participant FIELD as TextField
    participant BEHAVIOR as AjaxFormComponentUpdatingBehavior
    participant FEED as Feedback Label

    U->>FIELD: Preenche campo
    U->>FIELD: Campo perde foco (blur)

    FIELD->>BEHAVIOR: event "blur"
    BEHAVIOR->>BEHAVIOR: Valida campo
    BEHAVIOR->>U: target.add(field)
    BEHAVIOR->>U: target.add(feedback)

    alt Campo inválido
        FIELD->>FIELD: feedbackMessages.add(error)
        FIELD->>FIELD: class += "is-invalid"
        FEED->>FEED: Mostra mensagem de erro
    else Campo válido
        FIELD->>FIELD: feedbackMessages.clear()
        FIELD->>FIELD: class = "is-valid" (se aplicável)
        FEED->>FEED: Mostra vazio
    end
```

## Validação no Submit (handleFormError)

```mermaid
flowchart LR
    AJAX[AjaxButton.onError]

    AJAX --> COL[FeedbackCollector.collect(form)]
    COL --> FILTER[Filtra: level == ERROR]

    FILTER --> ERR_BUILD[Concatena mensagens]
    FILTER --> JS_BUILD[Gera JS para highlight]

    ERR_BUILD --> TOAST[showToast error]
    JS_BUILD --> HIGHLIGHT[appendJS: marca is-invalid]
    TOAST --> TARGET[target.add(form)]
    HIGHLIGHT --> TARGET
```

## AjaxPagingNavigator

```mermaid
sequenceDiagram
    participant U as User
    participant NAV as AjaxPagingNavigator
    participant DV as DataView
    participant DP as DataProvider
    participant SRV as Service

    U->>NAV: Clica página 3
    NAV->>DV: setCurrentPage(2)  ← 0-indexed
    DV->>DP: iterator(20, 10)    ← third page
    DP->>SRV: findAll(PageRequest.of(2, 10))
    SRV-->>DP: Page (items da página 3)
    DP-->>DV: Iterator
    DV->>DV: populateItem() para cada item
    DV->>U: target.add(dataView)
```
