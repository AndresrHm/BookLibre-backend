package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.requireBusiness
import java.time.LocalDate

data class CalificacionDTO(
    val nombreUsuario: String,
    val imagenUsuario: String = "",
    val comentario: String,
    val puntuacion: Double,
    val fecha: LocalDate,
)

data class CrearCalificacionDTO(
    val reservaId: Int,
    val usuarioId: Int,
    val comentario: String,
    val puntuacion: Double,
) {
    init {
        requireBusiness(comentario.length <= 500, "comentario no debe superar los 500 caracteres")
        requireBusiness(puntuacion in 0.5..5.0, "puntuacion debe estar entre 0.5 y 5.0")
    }
}

enum class OrdenCalificacion {
    DATE_ASC, DATE_DESC, RATING_ASC, RATING_DESC,
}