package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.NotFoundException
import ar.edu.unsam.phm.domain.Calificacion
import ar.edu.unsam.phm.domain.PageResponse
import ar.edu.unsam.phm.dto.OrdenCalificacion
import ar.edu.unsam.phm.repository.CalificacionRepository
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class CalificacionService(
    var libroRepository: LibroRepository,
    var reservaRepository: ReservaRepository,
    var calificacionRepository: CalificacionRepository,
) {
    fun calificarLibro(
        usuarioID: Int,
        libroID: Int,
        calificacion: Calificacion
    ) {
        libroRepository.getById(libroID) ?: throw NotFoundException("No existe un libro con el id $libroID.")

        val reserva = reservaRepository.obtenerUltimaReserva(usuarioID, libroID) ?: throw NotFoundException("El usuario no puede calificar este libro.")
        if (reserva.fechaDevolucion > LocalDate.now()) throw BusinessException("Podrá calificar el libro cuando termine su reserva.")

        calificacionRepository.agregarCalificacion(calificacion)

        // mantenemos el rating actualizado y guardado en el libro por una cuestión de simplicidad
        libroRepository.actualizarRating(libroID, calificacionRepository.obtenerPromedio(libroID))
    }

    fun obtenerCalificaciones(
        libroID: Int,
        orden: OrdenCalificacion,
        page: Int,
        size: Int,
    ): PageResponse<Calificacion> {
        val libro = libroRepository.getById(libroID)
            ?: throw NotFoundException("No existe libro con el id $libroID.")

        val pageResult = calificacionRepository.obtenerCalificaciones(libro, orden, page, size)
        val totalPages = (pageResult.totalElements / size - 1) / size

        return PageResponse(
            content = pageResult.content,
            page = page,
            size = size,
            totalElements = pageResult.totalElements,
            totalPages = totalPages
        )
    }
}