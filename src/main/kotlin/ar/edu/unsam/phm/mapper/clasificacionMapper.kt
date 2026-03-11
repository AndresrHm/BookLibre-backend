package ar.edu.unsam.phm.mapper

import ar.edu.unsam.phm.domain.Calificacion
import ar.edu.unsam.phm.dto.CalificacionDTO

fun CalificacionDTO.toCalificacion(): Calificacion {
    return Calificacion(
        usuarioID = usuarioID,
        nombreUsuario = this.nombreUsuario,
        imagenURL = this.imagenURL,
        puntuacion = this.puntuacion,
        comentario = this.comentario,
    )
}