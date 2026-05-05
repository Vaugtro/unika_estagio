CREATE TABLE cliente
(
    pk         INT UNSIGNED AUTO_INCREMENT NOT NULL,
    tipo       VARCHAR(8)                  NOT NULL,
    email      VARCHAR(255)                NOT NULL,
    created_at datetime                    NOT NULL,
    updated_at datetime                    NOT NULL,
    ativo      BIT(1)                      NOT NULL,
    CONSTRAINT pk_cliente PRIMARY KEY (pk)
);