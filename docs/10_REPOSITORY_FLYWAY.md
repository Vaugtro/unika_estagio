# Repositories e Flyway Migrations

## Camada de Repositórios

```mermaid
classDiagram
    class ClienteRepository~T~ {
        <<interface>>
        +findByEstaAtivoTrue(Pageable) Page~T~
        +findByEstaAtivoFalse(Pageable) Page~T~
        +countByEstaAtivoTrue() long
        +countByEstaAtivoFalse() long
    }

    class ClienteFisicoRepository {
        +findByCpf(String) Optional~ClienteFisico~
        +existsByCpf(String) boolean
        +findByCpfAndEstaAtivoTrue(String) Optional~ClienteFisico~
        +findByEstaAtivoTrue(Pageable) Page~ClienteFisico~
        +findByEstaAtivoFalse(Pageable) Page~ClienteFisico~
        +countByEstaAtivoTrue() long
        +countByEstaAtivoFalse() long
        +searchByCpf(String, Pageable) Page~ClienteFisico~
        +findByNomeContainingIgnoreCase(String, Pageable) Page~ClienteFisico~
        +findByEmail(String) Optional~ClienteFisico~
        +findByRg(String) Optional~ClienteFisico~
        +existsByRg(String) boolean
        +findByDataNascimentoBetween(LocalDate, LocalDate, Pageable) Page
        +findByDataNascimentoAfter(LocalDate, Pageable) Page
        +inactivateAllByIds(List~Long~)
        +activateAllByIds(List~Long~)
    }

    class ClienteJuridicoRepository {
        +findByCnpj(String) Optional~ClienteJuridico~
        +existsByCnpj(String) boolean
        +findByCnpjAndEstaAtivoTrue(String) Optional~ClienteJuridico~
        +findByEstaAtivoTrue(Pageable) Page~ClienteJuridico~
        +findByEstaAtivoFalse(Pageable) Page~ClienteJuridico~
        +countByEstaAtivoTrue() long
        +countByEstaAtivoFalse() long
        +searchByCnpj(String, Pageable) Page~ClienteJuridico~
        +findByRazaoSocialContainingIgnoreCase(String, Pageable) Page
        +findByEmail(String) Optional~ClienteJuridico~
        +findByDataCriacaoEmpresaAfter(LocalDate, Pageable) Page
        +findByDataCriacaoEmpresaBetween(LocalDate, LocalDate, Pageable) Page
        +inactivateAllByIds(List~Long~)
        +activateAllByIds(List~Long~)
    }

    class EnderecoRepository {
        +findByClienteId(Long, Pageable) Page~Endereco~
        +findByClienteId(Long) List~Endereco~
        +findByClienteIdAndPrincipalTrue(Long) Optional~Endereco~
        +countByClienteId(Long) long
        +existsByClienteIdAndPrincipalTrue(Long) boolean
        +deleteByClienteId(Long) long
    }

    JpaRepository~ClienteFisico, Long~ <|-- ClienteFisicoRepository
    JpaRepository~ClienteJuridico, Long~ <|-- ClienteJuridicoRepository
    JpaRepository~Endereco, Long~ <|-- EnderecoRepository
    JpaRepository~T, Long~ <|-- ClienteRepository
```

## Padrões de Query

```mermaid
flowchart LR
    subgraph "Derived Query (Spring Data)"
        DQ1["findByCpf(String)"] --> JPQL1["WHERE cpf = ?1"]
        DQ2["findByEstaAtivoTrue(Pageable)"] --> JPQL2["WHERE estaAtivo = true"]
        DQ3["findByNomeContainingIgnoreCase"] --> JPQL3["WHERE UPPER(nome) LIKE UPPER(CONCAT('%',?1,'%'))"]
        DQ4["countByClienteId(Long)"] --> JPQL4["SELECT COUNT(*) WHERE cliente_id = ?1"]
    end

    subgraph "Custom @Query"
        Q1["searchByCpf(String cpf)"] --> JPQL5["WHERE cpf LIKE %:cpf%"]
        Q2["inactivateAllByIds(List ids)"] --> JPQL6["UPDATE ClienteFisico SET estaAtivo=false WHERE id IN :ids"]
        Q3["activateAllByIds(List ids)"] --> JPQL7["UPDATE ClienteFisico SET estaAtivo=true WHERE id IN :ids"]
    end

    subgraph "Endereco Queries"
        EQ1["findByClienteId(Long, Pageable)"] --> JPQL8["WHERE cliente_id = ?1"]
        EQ2["findByClienteIdAndPrincipalTrue"] --> JPQL9["WHERE cliente_id = ?1 AND endereco_principal = true"]
    end
```

## Módulo de Migrations

```
src/main/resources/db/migration/main/
└── V1__initial_schema.sql
```

## Estrutura do Schema (V1__initial_schema.sql)

```mermaid
flowchart TD
    V1[V1__initial_schema.sql]

    V1 --> CREATE_CLIENTE["CREATE TABLE cliente
    pk INT UNSIGNED AUTO_INCREMENT PRIMARY KEY
    tipo VARCHAR(8) NOT NULL
    email VARCHAR(255) NOT NULL
    esta_ativo BOOLEAN NOT NULL DEFAULT TRUE
    created_at DATETIME NOT NULL
    updated_at DATETIME NOT NULL"]

    V1 --> CREATE_FISICO["CREATE TABLE cliente_fisico
    pk INT UNSIGNED PRIMARY KEY (FK→cliente)
    cpf VARCHAR(11) NOT NULL UNIQUE
    nome VARCHAR(255) NOT NULL
    rg VARCHAR(9) NOT NULL
    data_nascimento DATE NOT NULL"]

    V1 --> CREATE_JURIDICO["CREATE TABLE cliente_juridico
    pk INT UNSIGNED PRIMARY KEY (FK→cliente)
    cnpj VARCHAR(14) NOT NULL UNIQUE
    razao_social VARCHAR(255) NOT NULL
    inscricao_estadual VARCHAR(14) NOT NULL
    data_criacao_empresa DATE NOT NULL"]

    V1 --> CREATE_ENDERECO["CREATE TABLE endereco
    pk INT UNSIGNED AUTO_INCREMENT PRIMARY KEY
    logradouro VARCHAR(255) NOT NULL
    numero INT UNSIGNED NOT NULL
    cep VARCHAR(8) NOT NULL
    bairro VARCHAR(255)
    telefone VARCHAR(11) NOT NULL
    cidade VARCHAR(255) NOT NULL
    estado VARCHAR(2) NOT NULL
    endereco_principal BOOLEAN NOT NULL DEFAULT FALSE
    complemento VARCHAR(255)
    cliente_id INT UNSIGNED NOT NULL (FK→cliente)
    created_at DATETIME NOT NULL
    updated_at DATETIME NOT NULL"]

    CREATE_CLIENTE --> FK_FISICO["FK: cliente_fisico.pk → cliente.pk"]
    CREATE_CLIENTE --> FK_JURIDICO["FK: cliente_juridico.pk → cliente.pk"]
    CREATE_CLIENTE --> FK_ENDERECO["FK: endereco.cliente_id → cliente.pk"]
```

## Relação: Repository → Service → Controller

```mermaid
flowchart TB
    subgraph "ClienteFisico Stack"
        CF_CTRL[ClienteFisicoController<br/>/v1/clientes/fisicos]
        CF_SRV[ClienteFisicoServiceImpl<br/>@Service @Transactional]
        CF_REPO[ClienteFisicoRepository<br/>@Repository]
        CF_REPO -->|extends| JPA1[JpaRepository~ClienteFisico, Long~]
        CF_CTRL --> CF_SRV
        CF_SRV --> CF_REPO
    end

    subgraph "ClienteJuridico Stack"
        CJ_CTRL[ClienteJuridicoController<br/>/v1/clientes/juridicos]
        CJ_SRV[ClienteJuridicoServiceImpl<br/>@Service @Transactional]
        CJ_REPO[ClienteJuridicoRepository<br/>@Repository]
        CJ_REPO -->|extends| JPA2[JpaRepository~ClienteJuridico, Long~]
        CJ_CTRL --> CJ_SRV
        CJ_SRV --> CJ_REPO
    end

    subgraph "Endereco Stack"
        E_CTRL[EnderecoController<br/>/v1/enderecos]
        E_SRV[EnderecoServiceImpl<br/>@Service @Transactional]
        E_REPO[EnderecoRepository<br/>@Repository]
        E_REPO -->|extends| JPA3[JpaRepository~Endereco, Long~]
        E_CTRL --> E_SRV
        E_SRV --> E_REPO
    end

    subgraph "Shared"
        CF_SRV --> E_SRV
        CJ_SRV --> E_SRV
    end
```

## Flyway — Como Adicionar Migrations

Para adicionar uma nova migration:

1. Crie o arquivo em `src/main/resources/db/migration/main/`
2. Nomeie como `V{numero}__{descricao}.sql` (e.g., `V2__add_telefone_celular.sql`)
3. Escreva o SQL da migration

```sql
-- V2__add_telefone_celular.sql
ALTER TABLE cliente ADD COLUMN telefone_celular VARCHAR(11) AFTER email;
```

> **Nota:** Nunca altere migrations já executadas em produção. Crie uma nova migration.
