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

    init {
        requireBusiness(puntuacion in 0.0..5.0, "puntuacion debe estar entre 0.0 y 5.0")
        requireBusiness(comentario.isNotBlank(), "comentario no debe estar vacío")
        requireBusiness(comentario.length <= 500, "comentario no debe superar los 500 caracteres")
    }
}
