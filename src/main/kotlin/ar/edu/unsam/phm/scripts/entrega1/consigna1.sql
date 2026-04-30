-- CONSIGNA 1: Vista para conocer los Libros que reservó un usuario en el corriente año
CREATE VIEW v_libros_reservados_ano_actual AS
SELECT
    u.id    AS usuario_id,
    u.nombre,
    u.apellido,
    u.email,
    l.id    AS libro_id,
    l.titulo,
    l.autor,
    r.id    AS reserva_id,
    r.fecha_recogida,
    r.fecha_devolucion,
    DATEDIFF(DAY, r.fecha_recogida, r.fecha_devolucion) AS dias_reserva,
    YEAR(r.fecha_recogida) AS ano_reserva
FROM usuario u
INNER JOIN reserva r ON r.lector_id = u.id
INNER JOIN libro l ON l.id = r.libro_id
WHERE YEAR(r.fecha_recogida) = YEAR(GETDATE());


