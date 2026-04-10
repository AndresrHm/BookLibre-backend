package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Libro
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

@Repository
interface LibroRepository : CrudRepository<Libro, Long> {

    @Modifying
    @Query("""
            UPDATE Libro l
            SET l.activo = false
            where l.idPropietario = :idPropietario""")
    fun eliminarPorIDPropietario(idPropietario: Long)
    fun findLibrosByActivo(activo : Boolean) : List<Libro> // transformar las query posibles asi
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

    @Query(
        value = """
        SELECT l
        FROM Libro l
        WHERE l.activo = true
          AND (:searchPattern = '' OR LOWER(l.titulo) LIKE :searchPattern)
          AND (:isbn13 IS NULL OR l.isbn13 = :isbn13)
          AND (:paginasMin IS NULL OR l.cantidadPaginas >= :paginasMin)
          AND (:paginasMax IS NULL OR l.cantidadPaginas <= :paginasMax)
          AND (:filtrarGeneros = false OR l.genero IN :generos)
          AND (:usuarioPrestadorId IS NULL OR l.idPropietario = :usuarioPrestadorId)
          AND (:usuarioPrestadorId IS NOT NULL OR l.idPropietario <> :userId)
          AND (
                :aplicarFiltroFecha = false
                OR NOT EXISTS (
                    SELECT 1
                    FROM Reserva r
                    WHERE r.libro.id = l.id
                      AND r.fechaRecogida <= :fechaHasta
                      AND r.fechaDevolucion >= :fechaDesde
                )
              )
        """,
    )
    fun buscarHomePorFiltros(
        @Param("searchPattern") searchPattern: String,
        @Param("isbn13") isbn13: Long?,
        @Param("paginasMin") paginasMin: Int?,
        @Param("paginasMax") paginasMax: Int?,
        @Param("filtrarGeneros") filtrarGeneros: Boolean,
        @Param("generos") generos: List<Genero>,
        @Param("usuarioPrestadorId") usuarioPrestadorId: Long?,
        @Param("userId") userId: Long,
        @Param("aplicarFiltroFecha") aplicarFiltroFecha: Boolean,
        @Param("fechaDesde") fechaDesde: LocalDate,
        @Param("fechaHasta") fechaHasta: LocalDate,
    ): List<Libro>
}
