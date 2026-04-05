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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class CalificacionService {
    @Autowired
    lateinit var libroRepository: LibroRepository

    @Autowired
    lateinit var reservaRepository: ReservaRepository

    @Autowired
    lateinit var calificacionRepository: CalificacionRepository

    @Autowired
    lateinit var usuarioRepository: UsuarioRepository

    @Transactional
    fun calificarLibro(
        calificacionDTO: CrearCalificacionDTO
    ) {
        val usuario = usuarioRepository.findByIdOrNull(calificacionDTO.usuarioId)
            ?: throw NotFoundException(ErrorStrings.usuarioNoEncontradoParaCalificacion(calificacionDTO.usuarioId))

        if (esPublicador(usuario)) throw BusinessException(ErrorStrings.SOLO_LECTOR_PUEDE_CALIFICAR)

        val reserva = reservaRepository.findByIdOrNull(calificacionDTO.reservaId)
            ?: throw NotFoundException(ErrorStrings.reservaNoExistePorId(calificacionDTO.reservaId))

        if (calificacionDTO.usuarioId != reserva.usuarioLector.id) throw BusinessException(ErrorStrings.USUARIO_NO_PUEDE_CALIFICAR_LIBRO)

        if (reserva.estado() != EstadoReserva.DEVUELTO) throw BusinessException(ErrorStrings.PODRA_CALIFICAR_AL_FINALIZAR_RESERVA)

        val existente = calificacionRepository
            .findByLibroIdAndUsuarioId(
                reserva.libro.id!!,
                usuario.id!!,
            )

        if (existente != null) {
            existente.puntuacion = calificacionDTO.puntuacion
            existente.comentario = calificacionDTO.comentario
            existente.fecha = LocalDate.now()

            calificacionRepository.save(existente)
        } else {
            val nueva = Calificacion(
                usuario = reserva.usuarioLector,
                libro = reserva.libro,
                puntuacion = calificacionDTO.puntuacion,
                comentario = calificacionDTO.comentario,
                fecha = LocalDate.now()
            )

            calificacionRepository.save(nueva)
        }
    }


    @Transactional(readOnly = true)
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

        val result = calificacionRepository.findByLibroId(libro.id!!, pageable)

        return PageResponse(
            content = result.content,
            page = result.number,
            size = result.size,
            totalElements = result.totalElements.toInt(),
            totalPages = result.totalPages,
        )
    }

    @Transactional(readOnly = true)
    fun ultimasCalificaciones(
        libroID: Long,
    ): List<Calificacion> {
        val libro = libroRepository.findByIdOrNull(libroID)
            ?: throw NotFoundException(ErrorStrings.libroNoExistePorId(libroID))

        val calificaciones = calificacionRepository.findTop2ByLibroIdOrderByFechaDesc(libro.id!!)

        return calificaciones
    }

    @Transactional(readOnly = true)
    fun promedioCalificaciones(
        libroID: Long?,
    ): Double {
        return calificacionRepository.obtenerPromedioCalificaciones(libroID!!) ?: 0.0
    }

    @Transactional(readOnly = true)
    fun cantidadCalificaciones(
        libroID: Long,
    ): Long {
        return calificacionRepository.countByLibroId(libroID)
    }

    private fun esPublicador(usuario: Usuario): Boolean = usuario.tipoUsuario == TipoUsuario.PUBLICADOR
}
