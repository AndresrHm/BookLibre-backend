package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Calificacion
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.PageResult
import ar.edu.unsam.phm.domain.paginate
import ar.edu.unsam.phm.dto.OrdenCalificacion
import org.springframework.stereotype.Repository

@Repository
class LibroRepository : Repositorio<Libro>() {
    fun findRatings(
        libro: Libro,
        orden: OrdenCalificacion,
        page: Int,
        size: Int
    ): PageResult<Calificacion> {

        val sorted = when (orden) {
            OrdenCalificacion.DATE_ASC ->
                libro.obtenerCalificaciones().sortedBy { it.fecha }

            OrdenCalificacion.DATE_DESC ->
                libro.obtenerCalificaciones().sortedByDescending { it.fecha }

            OrdenCalificacion.RATING_ASC ->
                libro.obtenerCalificaciones().sortedBy { it.puntuacion }

            OrdenCalificacion.RATING_DESC ->
                libro.obtenerCalificaciones().sortedByDescending { it.puntuacion }
        }

        return paginate(page, size, sorted)
    }
}