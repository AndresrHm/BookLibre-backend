package ar.edu.unsam.phm.service

import FechasReserva
import ar.edu.unsam.phm.errors.BusinessException
import ar.edu.unsam.phm.errors.NotFoundException
import ar.edu.unsam.phm.controller.ReservaType
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.LibroReservaCount
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.domain.PageResponse
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.errors.ErrorStrings
import ar.edu.unsam.phm.predicate.reservaPorTipoSpecifications
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import ar.edu.unsam.phm.utils.sanitizarSearch
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import org.springframework.data.domain.Page

@Service
class ReservaService(
    var reservaRepository: ReservaRepository,
    val usuarioRepository: UsuarioRepository,
    val libroRepository: LibroRepository,
) {
    fun getReservas(
        userID: Long,
        type: ReservaType,
        page: Int,
        size: Int,
        nombreFiltro: String?,
    ): PageResponse<Reserva> {
        val nombreSanitizado = sanitizarSearch(nombreFiltro)
        if (!usuarioRepository.existsById(userID)) {
            throw BusinessException(ErrorStrings.USUARIO_LOGUEADO_NO_EXISTE)
        }

        val pedidoPaginas = PageRequest.of(page, size)

        val pageResponse = buscarReservasPorTipo(userID, type.name, nombreSanitizado, pedidoPaginas)

        return PageResponse(
            content = pageResponse.content,
            page = pageResponse.number,
            size = pageResponse.size,
            totalElements = pageResponse.totalElements.toInt(),
            totalPages = pageResponse.totalPages,
        )
    }

    @Transactional
    fun crearReserva(
        libroId: String,
        usuarioLector: Usuario,
        usuarioPrestador: Usuario,
        fechaRecogida: LocalDate,
        fechaDevolucion: LocalDate
    ): Reserva {
        val libro = libroRepository.findById(libroId)
            .orElseThrow { BusinessException(ErrorStrings.libroNoEncontradoPorId(libroId)) }

        if (!usuarioLector.esLector()) {
            throw BusinessException(ErrorStrings.SOLO_LECTOR_PUEDE_RESERVAR)
        }

        if (fechaRecogida.isAfter(fechaDevolucion))
            throw BusinessException(ErrorStrings.FECHA_DEVOLUCION_ANTERIOR_A_RECOGIDA)

        if (fechasAnterioresAHoy(fechaRecogida, fechaDevolucion))
            throw BusinessException(ErrorStrings.FECHA_RESERVA_ANTERIOR_A_HOY)

        if (!libroPerteneceAUsuario(usuarioPrestador, libro))
            throw BusinessException(ErrorStrings.LIBRO_NO_PERTENECE_USUARIO_PRESTADOR)

        if (libroPerteneceAUsuario(usuarioLector, libro))
            throw BusinessException(ErrorStrings.USUARIO_NO_PUEDE_RESERVAR_LIBRO_PROPIO)
        val fechasDeReservas = obtenerFechasDeReservasDeLibro(libro.id)
        val cantidadReservas = reservaRepository.countByLibroId(libro.id)

        if (haySolapamiento(fechaRecogida, fechaDevolucion, fechasDeReservas)
        ) {
            throw BusinessException(ErrorStrings.EXISTE_RESERVA_EN_RANGO)
        }

        val reserva = Reserva(libro.id!!, usuarioPrestador, usuarioLector, fechaRecogida, fechaDevolucion)
        usuarioLector.sumarKarma(reserva.darKarma(libro, cantidadReservas))

        usuarioRepository.save(usuarioLector)
        reservaRepository.save(reserva)

        return reserva
    }

    fun obtenerFechasDeReservasDeLibro(libroId: String): List<Pair<LocalDate, LocalDate>> {
        if (!libroRepository.existsById(libroId)) {
            throw NotFoundException(ErrorStrings.libroNoEncontradoPorId(libroId))
        }

        val reservas: List<FechasReserva> = reservaRepository.findFechasByLibroId(libroId)
        return reservas.map {
            it.fechaRecogida to it.fechaDevolucion
        }
    }

    fun cantidadReservas(libroID: String): Int {
        return reservaRepository.countByLibroId(libroID)
    }

    private fun buscarReservasPorTipo(
        userId: Long,
        tipoReserva: String,
        search: String?,
        pageRequest: PageRequest,
    ): Page<Reserva> {
        val especificacion = reservaPorTipoSpecifications(tipoReserva, userId, search)

        return reservaRepository.findAll(especificacion, pageRequest)
    }

    fun countReservasByLibroIds(libroIds: List<String>): List<LibroReservaCount> {
        return reservaRepository.countReservasByLibroIds(libroIds)
    }

    private fun fechasAnterioresAHoy(fechaRecogida: LocalDate, fechaDevolucion: LocalDate): Boolean =
        fechaRecogida < LocalDate.now() || fechaDevolucion <= LocalDate.now()

    private fun libroPerteneceAUsuario(usuario: Usuario, libro: Libro): Boolean =
        usuario.id == libro.idPropietario

    private fun haySolapamiento(
        fechaRecogida: LocalDate,
        fechaDevolucion: LocalDate,
        fechasDeReservas: List<Pair<LocalDate, LocalDate>>
    ): Boolean {
        return fechasDeReservas.any {
            // first: Inicio de reserva existente
            // second: Fin de reserva existente
            fechaRecogida <= it.second && fechaDevolucion >= it.first
        }
    }
}