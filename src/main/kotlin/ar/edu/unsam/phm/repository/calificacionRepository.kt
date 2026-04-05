package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Calificacion
import ar.edu.unsam.phm.domain.Libro
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CalificacionRepository : CrudRepository<Calificacion, Long> {

    fun findByReservaLibroIdAndReservaUsuarioLectorId(
        libroId: Long,
        usuarioId: Long
    ): Calificacion?

    fun findByLibroId(
        libroId: Long,
        pageable: Pageable
    ): Page<Calificacion>

    fun findTop2ByLibroIdOrderByFechaDesc(libroId: Long): List<Calificacion>

    @Query("""
        SELECT AVG(c.puntuacion)
        FROM Calificacion c
        WHERE c.libro.id = :libroId
    """)
    fun obtenerPromedioCalificaciones(@Param("libroId") libroId: Long): Double?

    fun countByLibroId(libroId: Long): Long
}