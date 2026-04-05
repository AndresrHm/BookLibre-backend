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
import org.springframework.stereotype.Service
import java.time.LocalDate
import kotlin.Pair

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
        string: String?,
    ): PageResponse<Reserva> {
        if (usuarioRepository.getById(userID) == null) throw BusinessException(ErrorStrings.USUARIO_LOGUEADO_NO_EXISTE)

        val pageResponse = reservaRepository.findReservasByType(userID, type, page, size, string)

        return PageResponse(
            content = pageResponse.content,
            page = page,
            size = size,
            totalElements = pageResponse.content.size,
            totalPages = (pageResponse.totalElements + size - 1) / size,
        )
    }

    fun crearReserva(
        libro: Libro,
        cantidadReservas: Int,
        usuarioLector: Usuario,
        usuarioPrestador: Usuario,
        fechaRecogida: LocalDate,
        fechaDevolucion: LocalDate
    ): Reserva {
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


        val fechasDeReservas = obtenerFechasDeReservasDeLibro(libro.id)

        if (haySolapamiento(fechaRecogida, fechaDevolucion, fechasDeReservas)
        ) {
            throw BusinessException(ErrorStrings.EXISTE_RESERVA_EN_RANGO)
        }

        val reserva = Reserva(libro, usuarioPrestador, usuarioLector, fechaRecogida, fechaDevolucion)
        usuarioLector.sumarKarma(reserva.darKarma(cantidadReservas))

        reservaRepository.create(reserva)
        usuarioRepository.update(usuarioLector) // Esto desaparece cuando saquemos el contador de libros leídos (y menos mal)

        return reserva
    }

    fun obtenerFechasDeReservasDeLibro(libroId: Long?): List<Pair<LocalDate, LocalDate>> {
        if (libroRepository.getById(libroId) != null) {
            val reservas: List<Reserva> = reservaRepository.findByLibroId(libroId)
            return reservas.map {
                it.fechaRecogida to it.fechaDevolucion
            }
        } else {
            throw NotFoundException(ErrorStrings.libroNoEncontradoPorId(libroId))
        }
    }

    fun cantidadReservas(libroID: Long?): Int {
        return reservaRepository.cantidadReservas(libroID)
    }

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
