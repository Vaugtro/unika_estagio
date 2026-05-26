-- TELEFONE: Make nullable since telefone is now optional
-- The existing CHECK constraints (che_telefone_length, che_telefone_digits)
-- handle NULL correctly (NULL evaluates to unknown, not FALSE), so they
-- remain unchanged.
ALTER TABLE endereco
    MODIFY COLUMN telefone VARCHAR(11) NULL;
