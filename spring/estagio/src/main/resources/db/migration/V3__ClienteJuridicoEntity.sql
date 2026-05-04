CREATE TABLE cliente_juridico
(
    pk                   INT UNSIGNED NOT NULL,
    cnpj                 VARCHAR(14)  NOT NULL,
    razao_social         VARCHAR(255) NOT NULL,
    inscricao_estadual   VARCHAR(12)  NOT NULL,
    data_criacao_empresa date         NOT NULL,
    CONSTRAINT pk_clientejuridico PRIMARY KEY (pk)
);

ALTER TABLE cliente_juridico
    ADD CONSTRAINT uc_clientejuridico_cnpj UNIQUE (cnpj);

ALTER TABLE cliente_juridico
    ADD CONSTRAINT FK_CLIENTEJURIDICO_ON_PK FOREIGN KEY (pk) REFERENCES cliente (pk);