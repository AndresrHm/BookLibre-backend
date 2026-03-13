package ar.edu.unsam.phm.repository

import ar.edu.unsam.phm.domain.Calificacion
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.PageResult
import ar.edu.unsam.phm.domain.paginate
import ar.edu.unsam.phm.dto.OrdenCalificacion
import org.springframework.stereotype.Repository

@Repository
class CalificacionRepository : Repositorio<Calificacion>() {
    fun obtenerCalificaciones(
        libro: Libro, orden: OrdenCalificacion, page: Int, size: Int
    ): PageResult<Calificacion> {

        val calificacionesOrdenadas = when (orden) {
            OrdenCalificacion.DATE_ASC -> getElementos().filter { it.libroID == libro.id }.sortedBy { it.fecha }

            OrdenCalificacion.DATE_DESC -> getElementos().filter { it.libroID == libro.id }.sortedByDescending { it.fecha }

            OrdenCalificacion.RATING_ASC -> getElementos().filter { it.libroID == libro.id }.sortedBy { it.puntuacion }

            OrdenCalificacion.RATING_DESC -> getElementos().filter { it.libroID == libro.id }.sortedByDescending { it.puntuacion }
        }

        return paginate(page, size, calificacionesOrdenadas)
    }

    fun agregarCalificacion(calificacion: Calificacion) {
        val existente = getElementos().find {
            it.libroID == calificacion.libroID && it.usuarioID == calificacion.usuarioID
        }

        if (existente == null) {
            create(calificacion)
        } else {
            calificacion.id = existente.id
            update(calificacion)
        }
    }
}