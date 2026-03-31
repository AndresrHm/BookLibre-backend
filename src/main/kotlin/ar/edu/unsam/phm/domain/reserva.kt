package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.dto.EstadoReserva
import ar.edu.unsam.phm.requireBusiness
import ar.edu.unsam.phm.repository.Identificable
import ar.edu.unsam.phm.utils.diasEntre
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Entity
class Reserva(
    val libro: Libro,
    val usuarioPrestador: Usuario,
    val usuarioLector: Usuario,
    val fechaRecogida: LocalDate,
    val fechaDevolucion: LocalDate,
) : Identificable {
    @Id
    @GeneratedValue
    override var id: Int = -1

    fun darKarma(cantidadReservas: Int): Int {
        return diasEntre(fechaDevolucion, fechaRecogida) * 5 + libro.darKarma(this, cantidadReservas)
    }

    fun estado(hoy: LocalDate = LocalDate.now()): EstadoReserva {
        return when {
            hoy.isAfter(fechaDevolucion) -> EstadoReserva.DEVUELTO
            ChronoUnit.DAYS.between(hoy, fechaDevolucion) <= 3 -> EstadoReserva.PROXIMO_A_VENCER
            else -> EstadoReserva.ACTIVO
        }
    }
}
