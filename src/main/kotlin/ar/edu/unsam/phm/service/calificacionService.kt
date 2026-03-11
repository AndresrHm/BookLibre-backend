package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.NotFoundException
import ar.edu.unsam.phm.domain.Calificacion
import ar.edu.unsam.phm.dto.CalificacionDTO
import ar.edu.unsam.phm.dto.PageResponse
import ar.edu.unsam.phm.mapper.toCalificacion
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class CalificacionService(
    var libroRepository: LibroRepository,
    var reservaRepository: ReservaRepository,
) {
    fun calificarLibro(
        usuarioID: Int,
        libroID: Int,
        calificacionDTO: CalificacionDTO
    ) {
        // Validamos la calificación, que el libro exista y que el usuario pueda calificarlo.
        val calificacion = calificacionDTO.toCalificacion()

        val libro = libroRepository.getById(libroID) ?: throw NotFoundException("No existe un libro con el id $libroID.")

        val reserva = reservaRepository.obtenerUltimaReserva(usuarioID, libroID) ?: throw NotFoundException("El usuario no puede calificar este libro.")

        if (reserva.fechaDevolucion > LocalDate.now()) throw BusinessException("Podrá calificar el libro cuando termine su reserva.")

        libro.calificar(calificacion)
    }

    fun obtenerCalificaciones(
        libroID: Int,
        page: Int,
        size: Int,
    ): PageResponse<Calificacion> {

        val libro = libroRepository.getById(libroID) ?: throw NotFoundException("No existe libro con el id $libroID.")

        val calificaciones = libro.obtenerCalificaciones()

        val totalPages = (calificaciones.size + size - 1) / size

        return PageResponse(
            content = calificaciones,
            page = page,
            size = size,
            totalElements = calificaciones.size,
            totalPages = totalPages
        )
    }
}