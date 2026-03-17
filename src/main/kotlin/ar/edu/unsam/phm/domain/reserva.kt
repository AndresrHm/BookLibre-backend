package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.dto.EstadoReserva
import ar.edu.unsam.phm.requireBusiness
import ar.edu.unsam.phm.repository.Identificable
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class Reserva(
    val libro: Libro,
    val usuarioPrestador: Usuario,
    val usuarioLector: Usuario,
    val fechaRecogida: LocalDate,
    val fechaDevolucion: LocalDate,
) : Identificable {
    override var id: Int = -1

    init {
        requireBusiness(fechaDevolucion.isAfter(fechaRecogida), "fechaDevolucion debe ser posterior a fechaRecogida")
    }

    fun estado(hoy: LocalDate = LocalDate.now()): EstadoReserva {
        return when {
            hoy.isAfter(fechaDevolucion) -> EstadoReserva.DEVUELTO
            ChronoUnit.DAYS.between(hoy, fechaDevolucion) <= 3 -> EstadoReserva.PROXIMO_A_VENCER
            else -> EstadoReserva.ACTIVO
        }
    }
}
