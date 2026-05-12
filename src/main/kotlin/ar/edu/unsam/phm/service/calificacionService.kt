package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.errors.BusinessException
import ar.edu.unsam.phm.errors.NotFoundException
import ar.edu.unsam.phm.domain.Calificacion
import ar.edu.unsam.phm.domain.EstadoReserva
import ar.edu.unsam.phm.domain.PageResponse
import ar.edu.unsam.phm.dto.CrearCalificacionDTO
import ar.edu.unsam.phm.dto.OrdenCalificacion
import ar.edu.unsam.phm.errors.ErrorStrings
import ar.edu.unsam.phm.repository.CalificacionRepository
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class CalificacionService(
    private val calificacionRepository: CalificacionRepository,
    private val usuarioRepository: UsuarioRepository,
    private val libroRepository: LibroRepository,
    private val reservaRepository: ReservaRepository,
) {

    @Transactional
    fun calificarLibro(
        usuarioId: Long,
        calificacionDTO: CrearCalificacionDTO
    ) {
        // Usuario
        val usuario = usuarioRepository.findByIdOrNull(usuarioId)
            ?: throw NotFoundException(ErrorStrings.usuarioNoEncontradoParaCalificacion(usuarioId))

        if (!usuario.esLector()) throw BusinessException(ErrorStrings.SOLO_LECTOR_PUEDE_CALIFICAR)

        // Reserva
        val reserva = reservaRepository.findByIdOrNull(calificacionDTO.reservaId)
            ?: throw NotFoundException(ErrorStrings.reservaNoExistePorId(calificacionDTO.reservaId))

        if (usuarioId != reserva.usuarioLector.id)
            throw BusinessException(ErrorStrings.USUARIO_NO_PUEDE_CALIFICAR_LIBRO)

        if (reserva.estado() != EstadoReserva.DEVUELTO)
            throw BusinessException(ErrorStrings.PODRA_CALIFICAR_AL_FINALIZAR_RESERVA)

        // Libro
        val libroId = reserva.libroId
        val libro = libroRepository.findByIdOrNull(libroId)
            ?: throw NotFoundException(ErrorStrings.libroNoEncontradoPorId(libroId))

        // Save
        val existente = calificacionRepository
            .findByLibroAndUsuarioId(libroId, usuario.id!!)

        if (existente != null) {
            existente.puntuacion = calificacionDTO.puntuacion
            existente.comentario = calificacionDTO.comentario
            existente.fecha = LocalDate.now()

            calificacionRepository.save(existente)
        } else {
            val nueva = Calificacion(
                usuario = reserva.usuarioLector,
                libro = libroId,
                puntuacion = calificacionDTO.puntuacion,
                comentario = calificacionDTO.comentario,
                fecha = LocalDate.now()
            )
            calificacionRepository.save(nueva)
        }

        val nuevoPromedio = calificacionRepository.obtenerPromedioCalificaciones(libroId)
        libro.actualizarCalificacion(nuevoPromedio!!)

        val ultimasCalificaciones = ultimasCalificaciones(libroId)
        libro.actualizarUltimasCalificaciones(ultimasCalificaciones)
        libroRepository.save(libro)
    }

    fun obtenerCalificaciones(
        libroID: Long,
        orden: OrdenCalificacion,
        page: Int,
        size: Int,
    ): PageResponse<Calificacion> {
        val libro = libroRepository.findByIdOrNull(libroID)
            ?: throw NotFoundException(ErrorStrings.libroNoExistePorId(libroID))

        val sort = when (orden) {
            OrdenCalificacion.DATE_ASC -> Sort.by("fecha").ascending()
            OrdenCalificacion.DATE_DESC -> Sort.by("fecha").descending()
            OrdenCalificacion.RATING_ASC -> Sort.by("puntuacion").ascending()
            OrdenCalificacion.RATING_DESC -> Sort.by("puntuacion").descending()
        }

        val pageable = PageRequest.of(page, size, sort)

        val result = calificacionRepository.findByLibro(libro.id!!, pageable)

        return PageResponse(
            content = result.content,
            page = result.number,
            size = result.size,
            totalElements = result.totalElements.toInt(),
            totalPages = result.totalPages,
        )
    }

    fun ultimasCalificaciones(
        libroID: Long,
    ): List<Calificacion> {
        val libro = libroRepository.findByIdOrNull(libroID)
            ?: throw NotFoundException(ErrorStrings.libroNoExistePorId(libroID))

        val calificaciones = calificacionRepository.findTop2ByLibroOrderByFechaDesc(libro.id!!)

        return calificaciones
    }

    fun cantidadCalificaciones(
        libroID: Long,
    ): Long {
        return calificacionRepository.countByLibro(libroID)
    }
}
