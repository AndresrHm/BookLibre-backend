package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Genero
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface LibroRepository : CrudRepository<Libro, Long> {

    @Modifying
    @Query("""
            UPDATE Libro l
            SET l.activo = false
            where l.idPropietario = :idPropietario""")
    fun eliminarPorIDPropietario(idPropietario: Long)

    // Filtrar libros activos
    @Query("SELECT l FROM Libro l WHERE l.activo = true")
    fun filtrarLibrosActivos(): List<Libro>

    // Buscar libros activos por propietario
    @Query("SELECT l FROM Libro l WHERE l.activo = true AND l.idPropietario = :idPropietario")
    fun buscarPorIdPropietario(idPropietario: Long): List<Libro>

    // Buscar libros disponibles del propietario
    @Query("""
        SELECT l FROM Libro l 
        WHERE l.activo = true 
        AND l.idPropietario = :idPropietario
        AND l.id NOT IN (
            SELECT r.libro.id FROM Reserva r 
            WHERE r.fechaRecogida <= CURRENT_DATE 
            AND r.fechaDevolucion >= CURRENT_DATE
        )
    """)
    fun buscarLibrosDisponiblesPorPropietario(idPropietario: Long): List<Libro>

    // Buscar libros prestados (actualmente reservados)
    @Query("""
        SELECT DISTINCT l FROM Libro l 
        WHERE l.activo = true 
        AND l.id IN (
            SELECT r.libro.id FROM Reserva r 
            WHERE r.fechaRecogida <= CURRENT_DATE 
            AND r.fechaDevolucion >= CURRENT_DATE
        )
    """)
    fun buscarLibrosPrestados(): List<Libro>

    // Buscar libros prestados del propietario
    @Query("""
        SELECT DISTINCT l FROM Libro l 
        WHERE l.activo = true 
        AND l.idPropietario = :idPropietario
        AND l.id IN (
            SELECT r.libro.id FROM Reserva r 
            WHERE r.fechaRecogida <= CURRENT_DATE 
            AND r.fechaDevolucion >= CURRENT_DATE
        )
    """)
    fun buscarLibrosPrestadosPorPropietario(idPropietario: Long): List<Libro>
}
