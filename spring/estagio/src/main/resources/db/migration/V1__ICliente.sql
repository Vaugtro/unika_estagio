CREATE TABLE cliente
(
    pk                   INT UNSIGNED AUTO_INCREMENT NOT NULL,
    dtype                VARCHAR(31)                 NOT NULL,
    tipo                 VARCHAR(8)                  NOT NULL,
    email                VARCHAR(255)                NOT NULL,
    created_at           datetime                    NOT NULL,
    updated_at           datetime                    NOT NULL,
    ativo                BIT(1)                      NOT NULL,
    cpf                  VARCHAR(11)                 NOT NULL,
    nome                 VARCHAR(255)                NOT NULL,
    rg                   VARCHAR(9)                  NOT NULL,
    data_nascimento      date                        NOT NULL,
    cnpj                 VARCHAR(14)                 NOT NULL,
    razao_social         VARCHAR(255)                NOT NULL,
    inscricao_estadual   VARCHAR(12)                 NOT NULL,
    data_criacao_empresa date                        NOT NULL,
    CONSTRAINT pk_cliente PRIMARY KEY (pk)
);

ALTER TABLE cliente
    ADD CONSTRAINT uc_cliente_cnpj UNIQUE (cnpj);

ALTER TABLE cliente
    ADD CONSTRAINT uc_cliente_cpf UNIQUE (cpf);