package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.repository.RepositorioDatos
import ar.edu.unsam.phm.utils.daysBetween
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class reserva(
    val libro: Libro,
    val usuarioPrestador: Usuario,
    val usuarioLector: Usuario,
    val fechaRecogida: LocalDate,
    val fechaDevolucion: LocalDate
) : RepositorioDatos {
    override var id: Int = 0

    override fun searchCondition(texto: String): Boolean {
        return true // de momento lo dejaremos en true para no generar conflicto
    }

    fun darKarma(): Int {
        return daysBetween(fechaDevolucion, fechaRecogida) * 5 + libro.darKarma(this)
    }
}
