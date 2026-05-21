-- RG: Relax constraint from 8-9 digits to 7-9 digits
-- (some states issue 7-digit RGs)
ALTER TABLE cliente_fisico DROP CONSTRAINT chf_rg_length;
ALTER TABLE cliente_fisico
    ADD CONSTRAINT chf_rg_length CHECK (LENGTH(rg) >= 7 AND LENGTH(rg) <= 9);
