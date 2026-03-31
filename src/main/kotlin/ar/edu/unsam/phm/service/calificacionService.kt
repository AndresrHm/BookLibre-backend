package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.NotFoundException
import ar.edu.unsam.phm.domain.Calificacion
import ar.edu.unsam.phm.domain.PageResponse
import ar.edu.unsam.phm.domain.TipoUsuario
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.CrearCalificacionDTO
import ar.edu.unsam.phm.dto.EstadoReserva
import ar.edu.unsam.phm.dto.OrdenCalificacion
import ar.edu.unsam.phm.errors.ErrorStrings
import ar.edu.unsam.phm.repository.CalificacionRepository
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
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
        val usuario = usuarioRepository.getById(calificacion.usuarioId)
            ?: throw NotFoundException(ErrorStrings.usuarioNoEncontradoParaCalificacion(calificacion.usuarioId))

        if (esPublicador(usuario)) {
            throw BusinessException(ErrorStrings.SOLO_LECTOR_PUEDE_CALIFICAR)
        }

        val reserva = reservaRepository.getById(calificacion.reservaId)
            ?: throw NotFoundException(ErrorStrings.reservaNoExistePorId(calificacion.reservaId))

        if (calificacion.usuarioId != reserva.usuarioLector.id) throw BusinessException(ErrorStrings.USUARIO_NO_PUEDE_CALIFICAR_LIBRO)

        if (reserva.estado() != EstadoReserva.DEVUELTO) throw BusinessException(ErrorStrings.PODRA_CALIFICAR_AL_FINALIZAR_RESERVA)

        val nuevaCalificacion = Calificacion(
            reserva = reserva,
            puntuacion = calificacion.puntuacion,
            comentario = calificacion.comentario,
            fecha = LocalDate.now()
        )

        calificacionRepository.agregarCalificacion(nuevaCalificacion)
    }

    private fun esPublicador(usuario: Usuario): Boolean = usuario.tipoUsuario == TipoUsuario.PUBLICADOR

    fun obtenerCalificaciones(
        libroID: Int,
        orden: OrdenCalificacion,
        page: Int,
        size: Int,
    ): PageResponse<Calificacion> {
        val libro = libroRepository.getById(libroID)
            ?: throw NotFoundException(ErrorStrings.libroNoExistePorId(libroID))

        val sort = when (orden) {
            OrdenCalificacion.DATE_ASC -> Sort.by("fecha").ascending()
            OrdenCalificacion.DATE_DESC -> Sort.by("fecha").descending()
            OrdenCalificacion.RATING_ASC -> Sort.by("puntuacion").ascending()
            OrdenCalificacion.RATING_DESC -> Sort.by("puntuacion").descending()
        }

        val pageable = PageRequest.of(page, size, sort)

        val result = calificacionRepository.findByReservaLibroId(libro.id!!, pageable)

        return PageResponse(
            content = result.content,
            page = result.number,
            size = result.size,
            totalElements = result.totalElements.toInt(),
            totalPages = result.totalPages,
        )
    }

    fun ultimasCalificaciones(
        libroID: Int,
    ): List<Calificacion> {
        val libro = libroRepository.getById(libroID)
            ?: throw NotFoundException(ErrorStrings.libroNoExistePorId(libroID))

        val calificaciones = calificacionRepository.obtenerUltimasCalificaciones(libro)

        return calificaciones
    }

    fun promedioCalificaciones(
        libroID: Int,
    ): Double {
        return calificacionRepository.obtenerPromedioCalificaciones(libroID)
    }

    fun cantidadCalificaciones(
        libroID: Int,
    ): Int {
        return calificacionRepository.obtenerCantidadCalificaciones(libroID)
    }
}