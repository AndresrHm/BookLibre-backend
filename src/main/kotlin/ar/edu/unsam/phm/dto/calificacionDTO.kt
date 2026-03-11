package ar.edu.unsam.phm.dto

data class CalificacionDTO (
    val usuarioID: Int,
    val nombreUsuario: String,
    val imagenURL: String?,
    val comentario: String,
    val puntuacion: Double,
)