SELECT
    u.id          AS user_id,
    u.nombre,
    u.apellido,
    u.email,
    COUNT(r.id)   AS reservas
FROM usuario u
INNER JOIN reserva r
    ON r.lector_id = u.id
GROUP BY
    u.id,
    u.email
HAVING COUNT(r.id) > :n
ORDER BY reservas DESC;