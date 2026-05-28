CREATE TABLE municipio (
    id     INT UNSIGNED NOT NULL,
    nome   VARCHAR(100) NOT NULL,
    uf_id  INT UNSIGNED NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_municipio_uf FOREIGN KEY (uf_id) REFERENCES unidade_federativa(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
