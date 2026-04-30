-- Consigna 2:
-- Llevar un control de las veces que un libro actualizó su puntaje, de manera de saber:
-- a) la fecha en la que se actualizó.
-- b) el nuevo valor y el anterior.
-- Se pueden usar vistas, triggers, functions o constraints.

CREATE TABLE historial_puntajes (
    id SERIAL PRIMARY KEY,
    libro_id INT,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    valor_anterior DECIMAL,
    valor_nuevo DECIMAL
);

CREATE FUNCTION cambio_puntaje()
RETURNS TRIGGER AS $$
BEGIN
    IF (OLD.calificacion IS DISTINCT FROM NEW.calificacion) THEN
       INSERT INTO historial_puntajes(libro_id, valor_anterior, valor_nuevo)
        VALUES (OLD.id, OLD.calificacion, NEW.calificacion);
END IF;
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER historial_puntajes_trigger
    AFTER UPDATE ON libro
    FOR EACH ROW
    EXECUTE FUNCTION cambio_puntaje();