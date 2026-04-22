DROP FUNCTION IF EXISTS GetUsuariosConReservas;

CREATE OR REPLACE FUNCTION GetUsuariosConReservas(
    nroreservas INT
)
RETURNS TABLE (
    user_id     BIGINT,
    nombre      TEXT,
    apellido    TEXT,
    email       TEXT,
    reservas    BIGINT
)
LANGUAGE plpgsql
AS $$
BEGIN
RETURN QUERY
SELECT
    u.id AS user_id,
    u.nombre::TEXT,
    u.apellido::TEXT,
    u.email::TEXT,
    COUNT(r.id)   AS reservas
FROM usuario u
         INNER JOIN reserva r
                    ON r.lector_id = u.id
GROUP BY
    u.id,
    u.nombre,
    u.apellido,
    u.email
HAVING COUNT(r.id) > nroreservas
ORDER BY reservas DESC;
END;
$$;
