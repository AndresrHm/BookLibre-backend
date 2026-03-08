package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.controller.LibroPrestadoDTO
import ar.edu.unsam.phm.controller.PageResponse
import ar.edu.unsam.phm.controller.TipoReserva
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import java.time.LocalDate

class ReservaService(
    var reservaRepository: ReservaRepository,
     var libroRepository: LibroRepository,
//     var repositorioUsuario: UsuarioRepository
) {

    fun getReservas(){}

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