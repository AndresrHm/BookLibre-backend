DROP VIEW IF EXISTS usuarios_con_mas_de_dos_reservas_devueltas;

CREATE VIEW usuarios_con_mas_de_dos_reservas_devueltas AS
SELECT lector_id, nombre, apellido, COUNT(r.id) cantidad_reservas
    FROM reserva r
    INNER JOIN usuario u ON u.id = r.lector_id
        WHERE r.fecha_devolucion < CURRENT_DATE
        GROUP BY lector_id, nombre, apellido
        HAVING COUNT(r.id) > 2
        ORDER BY lector_id ASC;

SELECT * FROM usuarios_con_mas_de_dos_reservas_devueltas;