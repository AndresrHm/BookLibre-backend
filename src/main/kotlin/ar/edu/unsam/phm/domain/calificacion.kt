package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.BusinessException

data class Calificacion(
    val usuarioID: Int,
    val nombreUsuario: String,
    val imagenURL: String? = "",
    val puntuacion: Double,
    val comentario: String
) {
    init {
        validarNombre()
        validarPuntuacion()
        validarComentario()
    }

    private fun validarNombre() {
        if (nombreUsuario.isBlank()) {
            throw BusinessException("La calificación debe tener un nombre de usuario.")
        }
    }

    private fun validarPuntuacion() {
        if (puntuacion !in 0.0..5.0) {
            throw BusinessException("La puntuación debe estar entre 0.0 y 5.0.")
        }
    }

    private fun validarComentario() {
        if (comentario.isBlank()) {
            throw BusinessException("El comentario no debe estar vacío.")
        } else if (comentario.length >= 500) {
            throw ar.edu.unsam.phm.errors.BusinessException("El comentario no debe superar los 500 caracteres.")
        }
    }
}
