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
        libroId: String,
        usuarioId: Long
    ): Calificacion?

    fun findByLibro(
        libroId: String,
        pageable: Pageable
    ): Page<Calificacion>

    fun findTop2ByLibroOrderByFechaDesc(libroId: String): List<Calificacion>

    @Query(
        """
        SELECT AVG(c.puntuacion)
        FROM Calificacion c
        WHERE c.libro = :libro
    """
    )
    fun obtenerPromedioCalificaciones(@Param("libro") libroId: String): Double?

    fun countByLibro(libroId: String): Long

    @Query("SELECT AVG(c.puntuacion) FROM Calificacion c WHERE c.libro = :libro")
    fun promedioPorLibro(@Param("libro") libroId: String): Double?
}