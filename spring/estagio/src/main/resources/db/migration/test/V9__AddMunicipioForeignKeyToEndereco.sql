ALTER TABLE endereco ADD COLUMN municipio_id INT UNSIGNED;

UPDATE endereco e
JOIN municipio m ON m.nome = e.cidade
JOIN unidade_federativa uf ON uf.id = m.uf_id AND uf.sigla = e.estado
SET e.municipio_id = m.id;

ALTER TABLE endereco
    ADD CONSTRAINT fk_endereco_municipio
    FOREIGN KEY (municipio_id) REFERENCES municipio(id);

ALTER TABLE endereco DROP COLUMN cidade, DROP COLUMN estado;
