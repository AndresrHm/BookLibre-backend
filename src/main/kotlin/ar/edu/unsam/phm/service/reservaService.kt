package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.NotFoundException
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.domain.PageResponse
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.domain.TipoUsuario
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
    fun getReservasRealizadas(
        userID: Int,
        page: Int,
        size: Int,
        string: String?,
    ): PageResponse<Reserva> {

        if (usuarioRepository.getById(userID) == null) throw BusinessException("El usuario logueado no existe")

        val pageResponse = reservaRepository.findByLectorID(userID, page, size, string)

        return PageResponse(
            content = pageResponse.content,
            page = page,
            size = size,
            totalElements = pageResponse.content.size,
            totalPages = (pageResponse.totalElements + size - 1) / size,
        )
    }

    fun getReservasRecibidas(
        userID: Int,
        page: Int,
        size: Int,
        string: String?,
    ): PageResponse<Reserva> {

        if (usuarioRepository.getById(userID) == null) throw BusinessException("El usuario logueado no existe")

        val pageResponse = reservaRepository.findByPrestadorID(userID, page, size, string)

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
            throw BusinessException("Solo un Lector puede reservar libros.")
        }

        if (fechaRecogida.isAfter(fechaDevolucion))
            throw BusinessException("La fecha de devolución no puede ser anterior a la fecha de recogida")

        if (fechasAnterioresAHoy(fechaRecogida, fechaDevolucion))
            throw BusinessException("No se puede realizar una reserva para una fecha anterior a la actual")

        if (!libroPerteneceAUsuario(usuarioPrestador, libro))
            throw BusinessException("El libro no pertenece al usuario prestador")

        if (libroPerteneceAUsuario(usuarioLector, libro))
            throw BusinessException("Un usuario no puede reservar un libro que le pertenece")


        val fechasDeReservas = obtenerFechasDeReservasDeLibro(libro.id)

        if (haySolapamiento(fechaRecogida, fechaDevolucion, fechasDeReservas)
        ) {
            throw BusinessException("Existe una reserva en el rango de fechas estipulado")
        }

        val reserva = Reserva(libro, usuarioPrestador, usuarioLector, fechaRecogida, fechaDevolucion)

        usuarioLector.agregarLibroLeido() // Esto desaparece cuando saquemos el contador de libros leídos
        usuarioLector.sumarKarma(reserva.darKarma(cantidadReservas))

        reservaRepository.create(reserva)
        usuarioRepository.update(usuarioLector) // Esto desaparece cuando saquemos el contador de libros leídos (y menos mal)

        return reserva
    }

    fun obtenerFechasDeReservasDeLibro(libroId: Int): List<Pair<LocalDate, LocalDate>> {
        if (libroRepository.getById(libroId) != null) {
            val reservas = reservaRepository.findByLibroId(libroId)
            return reservas.map {
                it.fechaRecogida to it.fechaDevolucion
            }
        } else {
            throw NotFoundException("No existe un libro con el id $libroId")
        }
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

    fun cantidadReservas(libroID: Int): Int {
        return reservaRepository.cantidadReservas(libroID)
    }
}
