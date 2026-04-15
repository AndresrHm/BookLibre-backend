package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.dto.EstadoReserva
import ar.edu.unsam.phm.requireBusiness
import ar.edu.unsam.phm.utils.diasEntre
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import java.time.LocalDate
import java.time.temporal.ChronoUnit

@Entity
class Reserva(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "libro_id")
    val libro: Libro,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prestador_id")
    val usuarioPrestador: Usuario,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lector_id")
    val usuarioLector: Usuario,

    val fechaRecogida: LocalDate,
    val fechaDevolucion: LocalDate,
) {
    @Id
    @GeneratedValue
    var id: Long? = null

    // TODO: Revisar la forma de obtener cantidad reservas
    fun darKarma(cantidadReservas: Int): Int {
        return diasEntre(fechaRecogida, fechaDevolucion) * 5 + libro.darKarma(this, cantidadReservas)
    }

    fun estado(hoy: LocalDate = LocalDate.now()): EstadoReserva {
        return when {
            hoy.isAfter(fechaDevolucion) -> EstadoReserva.DEVUELTO
            ChronoUnit.DAYS.between(hoy, fechaDevolucion) <= 3 -> EstadoReserva.PROXIMO_A_VENCER
            else -> EstadoReserva.ACTIVO
        }
    }
}
