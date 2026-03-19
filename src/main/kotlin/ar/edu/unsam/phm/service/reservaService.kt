package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.NotFoundException
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.domain.PageResponse
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

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

        if (usuarioRepository.getById(userID) == null) throw BusinessException("El usuario no existe")

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

        if (usuarioRepository.getById(userID) == null) throw BusinessException("El usuario no existe")

        val pageResponse = reservaRepository.findByPrestadorID(userID, page, size, string)

        return PageResponse(
            content = pageResponse.content,
            page = page,
            size = size,
            totalElements = pageResponse.content.size,
            totalPages = (pageResponse.totalElements + size - 1) / size,
        )
    }

    fun getLibrosReservadosEntreFechas(fechaInicio: LocalDate, fechaFin: LocalDate): List<Libro> {
        require(!fechaInicio.isAfter(fechaFin)) { "fechaInicio no puede ser mayor a fechaFin" }

        return reservaRepository.getElementos()
            .filter { reserva ->
                // Verificamos que la reserva este entre el rango de fechas a buscar
                !reserva.fechaRecogida.isAfter(fechaFin) && !reserva.fechaDevolucion.isBefore(fechaInicio)
            }
            .map { it.libro }
    }

    fun getLibrosPrestadosPorUsuario(usuarioPrestador: Usuario): List<Libro> {
        return reservaRepository.getElementos()
            .filter { it.usuarioPrestador.id == usuarioPrestador.id }
            .map { it.libro }
    }

    fun crearReserva(
        libro: Libro,
        usuarioLector: Usuario,
        usuarioPrestador: Usuario,
        fechaRecogida: LocalDate,
        fechaDevolucion: LocalDate
    ): Reserva {
        if (fechaRecogida.isAfter(fechaDevolucion))
            throw BusinessException("La fecha de devolución no puede ser anterior a la fecha de recogida")

        if (fechaRecogida < LocalDate.now() || fechaDevolucion <= LocalDate.now())
            throw BusinessException("No se puede realizar una reserva para una fecha anterior a la actual")

        if (libro.idPropietario != usuarioPrestador.id)
            throw BusinessException("El libro no pertenece al usuario prestador")

        if (usuarioLector.id == libro.id)
            throw BusinessException("Un usuario no puede reservar un libro que le pertenece")

        val fechasDeReservas = obtenerFechasDeReservasDeLibro(libro.id)

        val reserva = usuarioLector.crearReserva(libro, usuarioPrestador, fechaRecogida, fechaDevolucion, fechasDeReservas)

        reservaRepository.create(reserva)
        usuarioRepository.update(usuarioLector)

        return reserva
    }

    fun obtenerFechasDeReservasDeLibro(libroId: Int): List<Pair<LocalDate,LocalDate>> {
        if (libroRepository.getById(libroId) != null) {
            val reservas = reservaRepository.findByLibroId(libroId)
            return reservas.map{
                it.fechaRecogida to it.fechaDevolucion
            }
        } else {
            throw NotFoundException("No existe un libro con el id $libroId")
        }
    }
}