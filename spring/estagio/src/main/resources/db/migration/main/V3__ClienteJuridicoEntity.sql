CREATE TABLE cliente_juridico
(
    pk                   INT UNSIGNED NOT NULL,
    cnpj                 VARCHAR(14)  NOT NULL,
    razao_social         VARCHAR(255) NOT NULL,
    inscricao_estadual   VARCHAR(14)  NOT NULL,
    data_criacao_empresa date         NOT NULL,
    CONSTRAINT pk_clientejuridico PRIMARY KEY (pk)
);

ALTER TABLE cliente_juridico
    ADD CONSTRAINT uc_clientejuridico_cnpj UNIQUE (cnpj);

ALTER TABLE cliente_juridico
    ADD CONSTRAINT FK_CLIENTEJURIDICO_ON_PK FOREIGN KEY (pk) REFERENCES cliente (pk);

-- CNPJ: Must be exactly 14 digits
ALTER TABLE cliente_juridico
    ADD CONSTRAINT chj_cnpj_length CHECK (LENGTH(cnpj) = 14);

-- CNPJ: Must contain only digits
ALTER TABLE cliente_juridico
    ADD CONSTRAINT chj_cnpj_digits CHECK (cnpj REGEXP '^[0-9]+$');

-- Inscrição Estadual: Must be between 8 and 14 digits
ALTER TABLE cliente_juridico
    ADD CONSTRAINT chj_ie_length CHECK (LENGTH(inscricao_estadual) BETWEEN 8 AND 14);

-- Inscrição Estadual: Must contain only digits
ALTER TABLE cliente_juridico
    ADD CONSTRAINT chj_ie_digits CHECK (inscricao_estadual REGEXP '^[0-9]+$');
