package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.repository.Identificable
import ar.edu.unsam.phm.utils.daysBetween
import java.time.LocalDate

class Reserva(
    val libro: Libro,
    val usuarioPrestador: Usuario,
    val usuarioLector: Usuario,
    val fechaRecogida: LocalDate,
    val fechaDevolucion: LocalDate
) : Identificable {
    override var id: Int = 0

    fun darKarma(): Int {
        return daysBetween(fechaDevolucion, fechaRecogida) * 5 + libro.darKarma(this)
    }
}
