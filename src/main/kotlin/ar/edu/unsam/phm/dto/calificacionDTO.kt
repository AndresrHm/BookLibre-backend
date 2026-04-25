package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.errors.ErrorStrings
import ar.edu.unsam.phm.requireBusiness
import java.time.LocalDate

const val MAX_CARACTERES_COMENTARIO = 500

data class CalificacionDTO(
    val nombreUsuario: String,
    val imagenUsuario: String = "",
    val comentario: String,
    val puntuacion: Double,
    val fecha: LocalDate,
)

data class CrearCalificacionDTO(
    val reservaId: Long,
    val comentario: String,
    val puntuacion: Double,
) {
    init {
        requireBusiness(comentario.length <= MAX_CARACTERES_COMENTARIO, ErrorStrings.COMENTARIO_MAX_500)
        requireBusiness(puntuacion in 0.5..5.0, ErrorStrings.PUNTUACION_ENTRE_05_Y_50)
    }
}

enum class OrdenCalificacion {
    DATE_ASC, DATE_DESC, RATING_ASC, RATING_DESC,
}