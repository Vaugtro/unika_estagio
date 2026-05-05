CREATE TABLE endereco
(
    pk                 INT UNSIGNED AUTO_INCREMENT NOT NULL,
    logradouro         VARCHAR(255)                NOT NULL,
    numero             INT UNSIGNED                NOT NULL,
    cep                VARCHAR(8)                  NOT NULL,
    bairro             VARCHAR(255)                NULL,
    telefone           VARCHAR(11)                 NOT NULL,
    cidade             VARCHAR(255)                NOT NULL,
    estado             VARCHAR(255)                NOT NULL,
    endereco_principal BIT(1)                      NOT NULL,
    complemento        VARCHAR(255)                NULL,
    cliente_id         INT UNSIGNED                NOT NULL,
    created_at         datetime                    NOT NULL,
    updated_at         datetime                    NOT NULL,
    CONSTRAINT pk_endereco PRIMARY KEY (pk)
);

ALTER TABLE endereco
    ADD CONSTRAINT FK_ENDERECO_ON_CLIENTE FOREIGN KEY (cliente_id) REFERENCES cliente (pk),
    ADD COLUMN un_endereco_principal TINYINT(1)
        AS (CASE WHEN endereco_principal = 1 THEN 1 ELSE NULL END)
        PERSISTENT;

CREATE UNIQUE INDEX uk_cliente_endereco_principal_unico
    ON endereco (cliente_id, un_endereco_principal);

ALTER TABLE endereco
    ADD CONSTRAINT che_cep_length CHECK (LENGTH(cep) = 8);

-- CEP: Must contain only digits
ALTER TABLE endereco
    ADD CONSTRAINT che_cep_digits CHECK (cep REGEXP '^[0-9]+$');

-- TELEFONE: Must be 10 or 11 digits
ALTER TABLE endereco
    ADD CONSTRAINT che_telefone_length CHECK (LENGTH(telefone) = 10 OR LENGTH(telefone) = 11);

-- TELEFONE: Must contain only digits
ALTER TABLE endereco
    ADD CONSTRAINT che_telefone_digits CHECK (telefone REGEXP '^[0-9]+$');