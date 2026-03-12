package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.domain.PageResponse
import ar.edu.unsam.phm.dto.ReservaDTO
import ar.edu.unsam.phm.mapper.toReservaDTO
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
    ): PageResponse<ReservaDTO> {

        val pageResult = reservaRepository.findByLectorID(userID, page, size)

//        val pageResult = reservas.content.map { it.toLibroPrestadoDTO(userID) }
        val totalPages = ((pageResult.content.size + size - 1) / size)

        return PageResponse(
            content = pageResult.content.map { it.toReservaDTO(userID) },
            page = page,
            size = size,
            totalElements = pageResult.totalElements,
            totalPages = totalPages,
        )
    }

    fun getReservasRecibidas(
        userID: Int,
        page: Int,
        size: Int
    ): PageResponse<ReservaDTO> {

        val reservas = reservaRepository.findByPrestadorID(userID, page, size)

        val reservasDTO = reservas.content.map { it.toReservaDTO(userID) }

        val totalPages = ((reservas.totalElements + size - 1) / size)

        return PageResponse(
            content = reservasDTO,
            page = page,
            size = size,
            totalElements = reservas.totalElements,
            totalPages = totalPages,
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

    fun getLibrosPrestadosPorUsuario(usuarioPrestador: Usuario): List<Libro>{
        return reservaRepository.getElementos()
            .filter { it.usuarioPrestador.id == usuarioPrestador.id }
            .map { it.libro }
    }
}