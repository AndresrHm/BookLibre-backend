package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.NotFoundException
import ar.edu.unsam.phm.controller.ReservaType
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.domain.PageResponse
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.domain.TipoUsuario
import ar.edu.unsam.phm.errors.ErrorStrings
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
class ReservaService(
    @Autowired
    var reservaRepository: ReservaRepository,
    @Autowired
    val usuarioRepository: UsuarioRepository,
    @Autowired
    val libroRepository: LibroRepository,
) {
    @Transactional(readOnly = true)
    fun getReservas(
        userID: Long,
        type: ReservaType,
        page: Int,
        size: Int,
        nombreFiltro: String?,
    ): PageResponse<Reserva> {
        if (!usuarioRepository.existsById(userID)) {
            throw BusinessException(ErrorStrings.USUARIO_LOGUEADO_NO_EXISTE)
        }

        val pedidoPaginas = PageRequest.of(page, size)

        val pageResponse = reservaRepository.findReservasByType(userID, type.name, nombreFiltro, pedidoPaginas)

        return PageResponse(
            content = pageResponse.content,
            page = pageResponse.number,
            size = pageResponse.size,
            totalElements = pageResponse.totalElements.toInt(),
            totalPages = pageResponse.totalPages,
        )
    }

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun crearReserva(
        libroId: Long,
        usuarioLector: Usuario,
        usuarioPrestador: Usuario,
        fechaRecogida: LocalDate,
        fechaDevolucion: LocalDate
    ): Reserva {
        // lectura
        val libro = libroRepository.findById(libroId) // Lo traigo en el service para que no moleste con los tipos nulos
            .orElseThrow { BusinessException(ErrorStrings.libroNoEncontradoPorId(libroId)) }

        if (esTipoPublicador(usuarioLector)) {
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
        // lectura
        val fechasDeReservas = obtenerFechasDeReservasDeLibro(libro.id!!) //Esto luego vendrá de algún método del repo
        // lectura
        val cantidadReservas = reservaRepository.countByLibroId(libro.id!!)

        if (haySolapamiento(fechaRecogida, fechaDevolucion, fechasDeReservas)
        ) {
            throw BusinessException(ErrorStrings.EXISTE_RESERVA_EN_RANGO)
        }
        // crea el objeto
        val reserva = Reserva(libro, usuarioPrestador, usuarioLector, fechaRecogida, fechaDevolucion)
        // escritura
        usuarioLector.sumarKarma(reserva.darKarma(cantidadReservas))
        // escritura
        reservaRepository.save(reserva)
        return reserva
    }

    @Transactional(readOnly = true)
    fun obtenerFechasDeReservasDeLibro(libroId: Long): List<Pair<LocalDate, LocalDate>> {
        if (!libroRepository.existsById(libroId)) {
            throw NotFoundException(ErrorStrings.libroNoEncontradoPorId(libroId))
        }

        val reservas: List<Reserva> = reservaRepository.findAllByLibroId(libroId)
        return reservas.map {
            it.fechaRecogida to it.fechaDevolucion
        }
    }

    @Transactional(readOnly = true)
    fun cantidadReservas(libroID: Long): Int {
        return reservaRepository.countByLibroId(libroID)
    }

    @Transactional(readOnly = true)
    fun idLibrosPrestadosHoy(): Set<Long?> {
        return reservaRepository
            .getLibrosReservadosEntreFechas(LocalDate.now(), LocalDate.now())
            .map { it.id }
            .toSet()
    }

    private fun fechasAnterioresAHoy(fechaRecogida: LocalDate, fechaDevolucion: LocalDate): Boolean =
        fechaRecogida < LocalDate.now() || fechaDevolucion <= LocalDate.now()

    private fun esTipoPublicador(usuarioLector: Usuario): Boolean = usuarioLector.tipoUsuario == TipoUsuario.PUBLICADOR

    private fun libroPerteneceAUsuario(usuarioLector: Usuario, libro: Libro): Boolean =
        usuarioLector.id == libro.idPropietario

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
