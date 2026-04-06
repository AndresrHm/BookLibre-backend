package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.requireBusiness
import ar.edu.unsam.phm.repository.Identificable
import java.time.LocalDate

data class Calificacion(
    val reserva: Reserva,
    val puntuacion: Double,
    val comentario: String,
    val fecha: LocalDate,
): Identificable {
    override var id: Int = -1
}
