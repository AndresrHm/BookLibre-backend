package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Reserva
import org.springframework.stereotype.Service
import java.time.LocalDate

@Service
class ReservaService(
    var reservaRepository: ReservaRepository,
    //var libroRepository: LibroRepository
) {

    fun getReservas(): List<Reserva> = reservaRepository.getElementos()

    fun getLibrosReservadosEntreFechas(fechaInicio: LocalDate, fechaFin: LocalDate): List<Libro> {
        require(!fechaInicio.isAfter(fechaFin)) { "fechaInicio no puede ser mayor a fechaFin" }

        return reservaRepository.getElementos()
            .filter { reserva ->
                // Verificamos que la reserva este entre el rango de fechas a buscar
                !reserva.fechaRecogida.isAfter(fechaFin) && !reserva.fechaDevolucion.isBefore(fechaInicio)
            }
            .map { it.libro }
    }
    fun createReserva(reserva: Reserva){
        //Creacion simple, mejorar dependiendo los requerimientos y validaciones necesarias
        reservaRepository.create(reserva)
    }
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