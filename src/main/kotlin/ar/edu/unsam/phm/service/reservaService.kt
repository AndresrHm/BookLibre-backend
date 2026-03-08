package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.dto.LibroPrestadoDTO
import ar.edu.unsam.phm.dto.PageResponse
import ar.edu.unsam.phm.dto.TipoReserva
import ar.edu.unsam.phm.mapper.toLibroPrestadoDTO
import ar.edu.unsam.phm.repository.ReservaRepository
import org.springframework.stereotype.Service

@Service
class ReservaService(
    var reservaRepository: ReservaRepository
) {

    fun getReservas(
        userID: Int,
        tipoReserva: TipoReserva,
        page: Int,
        size: Int
    ): PageResponse<LibroPrestadoDTO> {

        val reservas = when (tipoReserva) {
            TipoReserva.TO_ME -> reservaRepository.findByLectorID(userID, page, size)

            TipoReserva.BY_ME -> reservaRepository.findByPrestadorID(userID, page, size)
        }

        val dtos = reservas.content.map { it.toLibroPrestadoDTO(userID) }

        val totalPages = ((reservas.totalElements + size - 1) / size)

        return PageResponse(
            content = dtos,
            page = page,
            size = size,
            totalElements = reservas.totalElements,
            totalPages = totalPages,
        )
    }

//    fun getLibrosReservadosEntreFechas(fechaInicio: LocalDate, fechaFin: LocalDate): List<Libro> {
//        return repositorioReserva.allInstances()
//            .filter { reserva ->
//                // Verificamos que la reserva este entre el rango de fechas a buscar
//                reserva.fechaRecogida <= fechaFin && reserva.fechaDevolucion >= fechaInicio
//            }
//            .map { it. libro }
//    }
//
//    fun crearReserva(reserva: reserva) {
//        // 1. verificamos fechas
//        //try {repositorioReserva.reservar(reserva)}
//        //catch {}
//
//        // if error . . .
//
//        // 2. agregar reserva
////        repositorioReserva.add(reserva)
//
//        // 3. dar karma
////        reserva.darKarma(reserva.darKarma())
//    }
}