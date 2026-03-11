package ar.edu.unsam.phm.dto

import java.time.LocalDate

data class CalificacionDTO (
    val usuarioID: Int,
    val nombreUsuario: String,
    val imagenURL: String?,
    val comentario: String,
    val puntuacion: Double,
    val fecha: LocalDate,
)

enum class OrdenCalificacion {
    DATE_ASC,
    DATE_DESC,
    RATING_ASC,
    RATING_DESC,
}