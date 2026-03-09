package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.repository.Identificable
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class Reserva(
    val libro: Libro,
    val usuarioPrestador: Usuario,
    val usuarioLector: Usuario,
    val fechaRecogida: LocalDate,
    val fechaDevolucion: LocalDate,
    override var id: Int = -1,
) : Identificable {
    fun darKarma(): Int {
        return ChronoUnit.DAYS.between(fechaDevolucion, fechaRecogida).toInt() * 5 + libro.darKarma(this)
    }
}
