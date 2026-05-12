package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Calificacion
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface CalificacionRepository : CrudRepository<Calificacion, Long> {

    fun findByLibroAndUsuarioId(
        libroId: Long,
        usuarioId: Long
    ): Calificacion?

    fun findByLibro(
        libroId: Long,
        pageable: Pageable
    ): Page<Calificacion>

    fun findTop2ByLibroOrderByFechaDesc(libroId: Long): List<Calificacion>

    @Query(
        """
        SELECT AVG(c.puntuacion)
        FROM Calificacion c
        WHERE c.libro = :libroId
    """
    )
    fun obtenerPromedioCalificaciones(@Param("libro") libroId: Long): Double?

    fun countByLibro(libroId: Long): Long

    @Query("SELECT AVG(c.puntuacion) FROM Calificacion c WHERE c.libro = :libroId")
    fun promedioPorLibro(@Param("libroId") libroId: Long): Double?
}