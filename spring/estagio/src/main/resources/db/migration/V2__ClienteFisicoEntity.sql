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