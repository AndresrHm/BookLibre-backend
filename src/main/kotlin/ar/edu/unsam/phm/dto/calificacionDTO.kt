package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.errors.requireBusiness
import java.time.LocalDate
data class CrearCalificacionDTO(
    val libroId: Int,
    val usuarioId: Int,
    val nombreUsuario: String,
    val imagenUsuario: String = "",
    val comentario: String,
    val puntuacion: Double,
) {
    init {
        requireBusiness(libroId > 0, "libroID inválido")
        requireBusiness(usuarioId > 0, "usuarioID inválido")
        requireBusiness(nombreUsuario.isNotBlank(), "nombreUsuario no puede estar vacío")
        requireBusiness(comentario.isNotBlank(), "comentario no puede estar vacío")
        requireBusiness(comentario.length <= 500, "comentario no debe superar los 500 caracteres")
        requireBusiness(puntuacion in 0.0..5.0, "puntuacion debe estar entre 0.0 y 5.0")
    }
}

enum class OrdenCalificacion {
    DATE_ASC, DATE_DESC, RATING_ASC, RATING_DESC,
}