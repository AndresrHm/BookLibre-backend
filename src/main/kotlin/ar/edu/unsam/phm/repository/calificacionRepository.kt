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
            OrdenCalificacion.DATE_ASC -> getElementos().filter { it.reserva.libro.id == libro.id }
                .sortedBy { it.fecha }

            OrdenCalificacion.DATE_DESC -> getElementos().filter { it.reserva.libro.id == libro.id }
                .sortedByDescending { it.fecha }

            OrdenCalificacion.RATING_ASC -> getElementos().filter { it.reserva.libro.id == libro.id }
                .sortedBy { it.puntuacion }

            OrdenCalificacion.RATING_DESC -> getElementos().filter { it.reserva.libro.id == libro.id }
                .sortedByDescending { it.puntuacion }
        }

        return paginate(page, size, calificacionesOrdenadas)
    }

    fun obtenerUltimasCalificaciones(libro: Libro): List<Calificacion> {
        val calificaciones = getElementos().filter { it.reserva.libro.id == libro.id }.sortedBy { it.fecha }.take(2)

        return calificaciones
    }

    fun agregarCalificacion(calificacion: Calificacion) {
        val existente = getElementos().find {
            (it.reserva.libro.id == calificacion.reserva.libro.id) && (it.reserva.usuarioLector.id == calificacion.reserva.usuarioLector.id)
        }

        if (existente == null) {
            create(calificacion)
        } else {
            calificacion.id = existente.id
            update(calificacion)
        }
    }

    fun obtenerPromedioCalificaciones(libroID: Int): Double {
        return getElementos()
            .filter { it.reserva.libro.id == libroID }
            .map { it.puntuacion }
            .average()
            .takeIf { !it.isNaN() } ?: 0.0
    }

    fun obtenerCantidadCalificaciones(libroID: Int): Int {
        return getElementos().filter { it.reserva.libro.id == libroID }.size
    }
}