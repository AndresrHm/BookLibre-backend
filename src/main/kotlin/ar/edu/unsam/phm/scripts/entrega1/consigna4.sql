-- Setear karma en 0 para evitar errores
UPDATE usuario
SET biblio_karma = 0
WHERE biblio_karma IS NULL;

-- Setear constraint: biblioKarma no puede ser nulo
ALTER TABLE usuario
    ADD CONSTRAINT chk_biblio_karma_not_null
    CHECK (biblio_karma IS NOT NULL);