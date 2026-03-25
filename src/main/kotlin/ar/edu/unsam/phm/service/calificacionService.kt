package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.NotFoundException
import ar.edu.unsam.phm.domain.Calificacion
import ar.edu.unsam.phm.domain.PageResponse
import ar.edu.unsam.phm.domain.TipoUsuario
import ar.edu.unsam.phm.dto.CalificacionDTO
import ar.edu.unsam.phm.dto.CrearCalificacionDTO
import ar.edu.unsam.phm.dto.EstadoReserva
import ar.edu.unsam.phm.dto.OrdenCalificacion
import ar.edu.unsam.phm.repository.CalificacionRepository
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class CalificacionService(
    var libroRepository: LibroRepository,
    var reservaRepository: ReservaRepository,
    var calificacionRepository: CalificacionRepository,
    var usuarioRepository: UsuarioRepository
) {
    fun calificarLibro(
        calificacion: CrearCalificacionDTO
    ) {
        val usuario = usuarioRepository.getById(calificacion.usuarioId) ?: throw NotFoundException("No se ha encontrado el usuario con id $calificacion.usuarioId")

        if (usuario.tipoUsuario == TipoUsuario.PUBLICADOR) {
            throw BusinessException("Solo un Lector puede calificar libros.")
        }

        val reserva = reservaRepository.getById(calificacion.reservaId)
            ?: throw NotFoundException("No existe la reserva con id ${calificacion.reservaId}")

        if (calificacion.usuarioId != reserva.usuarioLector.id) throw BusinessException("El usuario logueado no puede calificar este libro")

        if (reserva.estado() != EstadoReserva.DEVUELTO) throw BusinessException("Podrá calificar el libro cuando termine la reserva")

        val nuevaCalificacion = Calificacion(
            reserva = reserva,
            puntuacion = calificacion.puntuacion,
            comentario = calificacion.comentario,
            fecha = LocalDate.now()
        )

        calificacionRepository.agregarCalificacion(nuevaCalificacion)

        libroRepository.actualizarRating(reserva.libro.id, calificacionRepository.obtenerPromedio(reserva.libro.id))
    }

    fun obtenerCalificaciones(
        libroID: Int,
        orden: OrdenCalificacion,
        page: Int,
        size: Int,
    ): PageResponse<Calificacion> {
        val libro = libroRepository.getById(libroID)
            ?: throw NotFoundException("No existe libro con el id $libroID")

        val pageResult = calificacionRepository.obtenerCalificaciones(libro, orden, page, size)

        return PageResponse(
            content = pageResult.content,
            page = page,
            size = size,
            totalElements = pageResult.totalElements,
            totalPages = (pageResult.totalElements + size - 1) / size
        )
    }

    fun obtenerUltimasCalificaciones(
        libroID: Int,
    ): List<Calificacion> {
        val libro = libroRepository.getById(libroID)
            ?: throw NotFoundException("No existe libro con el id $libroID")

        val calificaciones = calificacionRepository.obtenerUltimasCalificaciones(libro)

        return calificaciones
    }
}