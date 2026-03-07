package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.repository.ReservaRepository

class ReservaService(
    var repositorioReserva: ReservaRepository
) {
    fun crearReserva(reserva: Reserva) {
        // 1. verificamos fechas
        //try {repositorioReserva.reservar(reserva)}
        //catch {}
        
        // if error . . .

        // 2. agregar reserva
        repositorioReserva.add(reserva)

        // 3. dar karma
        reserva.darKarma(reserva.darKarma())
    }
}