CREATE TABLE cliente_fisico
(
    pk              INT UNSIGNED NOT NULL,
    cpf             VARCHAR(11)  NOT NULL,
    nome            VARCHAR(255) NOT NULL,
    rg              VARCHAR(9)   NOT NULL,
    data_nascimento date         NOT NULL,
    CONSTRAINT pk_clientefisico PRIMARY KEY (pk)
);

ALTER TABLE cliente_fisico
    ADD CONSTRAINT uc_clientefisico_cpf UNIQUE (cpf);

ALTER TABLE cliente_fisico
    ADD CONSTRAINT FK_CLIENTEFISICO_ON_PK FOREIGN KEY (pk) REFERENCES cliente (pk);

-- CPF: Must be exactly 11 digits
ALTER TABLE cliente_fisico
    ADD CONSTRAINT chf_cpf_length CHECK (LENGTH(cpf) = 11);

-- CPF: Must contain only digits (MySQL)
ALTER TABLE cliente_fisico
    ADD CONSTRAINT chf_cpf_digits CHECK (cpf REGEXP '^[0-9]+$');

-- RG: Must be 8 or 9 digits
ALTER TABLE cliente_fisico
    ADD CONSTRAINT chf_rg_length CHECK (LENGTH(rg) = 8 OR LENGTH(rg) = 9);

-- RG: Must contain only digits
ALTER TABLE cliente_fisico
    ADD CONSTRAINT chf_rg_digits CHECK (rg REGEXP '^[0-9]+$');