package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.repository.Identificable
import java.time.LocalDate

data class Calificacion(
    val usuarioID: Int,
    val nombreUsuario: String,
    val imagenUsuario: String? = "",
    val libroID: Int,
    val puntuacion: Double,
    val comentario: String,
    val fecha: LocalDate,
): Identificable {
    override var id: Int = -1
}
