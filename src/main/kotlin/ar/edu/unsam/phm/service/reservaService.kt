package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.domain.PageResponse
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.repository.ReservaRepository
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ReservaService(
    var reservaRepository: ReservaRepository
) {
    fun getReservasRealizadas(
        userID: Int,
        page: Int,
        size: Int
    ): PageResponse<Reserva> {

        // validar que exista usuario

        val pageResponse = reservaRepository.findByLectorID(userID, page, size)

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
        size: Int
    ): PageResponse<Reserva> {

        val pageResponse = reservaRepository.findByPrestadorID(userID, page, size)

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
}