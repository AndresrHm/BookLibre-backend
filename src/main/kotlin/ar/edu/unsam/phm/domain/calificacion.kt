package ar.edu.unsam.phm.domain

import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.errors.requireBusiness
import ar.edu.unsam.phm.repository.Identificable
import java.time.LocalDate

data class Calificacion(
    val usuarioID: Int,
    val nombreUsuario: String,
    val imagenUsuario: String = "",
    val libroID: Int,
    val puntuacion: Double,
    val comentario: String,
    val fecha: LocalDate,
): Identificable {
    override var id: Int = -1

    init {
        requireBusiness(usuarioID > 0, "usuarioID debe ser mayor a cero")
        requireBusiness(libroID > 0, "libroID debe ser mayor a cero")
        requireBusiness(nombreUsuario.isNotBlank(), "nombreUsuario no puede estar vacío")
        requireBusiness(puntuacion in 0.0..5.0, "puntuacion debe estar entre 0.0 y 5.0")
        requireBusiness(comentario.isNotBlank(), "comentario no puede estar vacío")
        requireBusiness(!fecha.isAfter(LocalDate.now()), "fecha no puede ser futura")
    }
}
