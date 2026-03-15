package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.BusinessException
import java.time.LocalDate

data class CalificacionDTO(
    val libroID: Int,
    val usuarioID: Int,
    val nombreUsuario: String,
    val imagenUsuario: String = "",
    val comentario: String,
    val puntuacion: Double,
    val fecha: LocalDate,
) {
    init {
        if (libroID <= 0) {
            throw BusinessException("libroID inválido")
        }

        if (usuarioID <= 0) {
            throw BusinessException("usuarioID inválido")
        }

        if (nombreUsuario.isBlank()) {
            throw BusinessException("nombreUsuario no puede estar vacío")
        }

        if (comentario.isBlank()) {
            throw BusinessException("comentario no puede estar vacío")
        }

        if (comentario.length >= 500) {
            throw BusinessException("comentario no debe superar los 500 caracteres")
        }

        if (puntuacion !in 0.0..5.0) {
            throw BusinessException("puntuacion debe estar entre 0.0 y 5.0")
        }

        if (fecha.isAfter(LocalDate.now())) {
            throw BusinessException("fecha inválida")
        }
    }
}

enum class OrdenCalificacion {
    DATE_ASC, DATE_DESC, RATING_ASC, RATING_DESC,
}