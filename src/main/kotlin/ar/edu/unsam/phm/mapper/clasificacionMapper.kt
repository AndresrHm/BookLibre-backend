package ar.edu.unsam.phm.mapper

import ar.edu.unsam.phm.domain.Calificacion
import ar.edu.unsam.phm.dto.CalificacionDTO

fun CalificacionDTO.toCalificacion(): Calificacion {
    return Calificacion(
        libroID = libroID,
        usuarioID = usuarioID,
        nombreUsuario = this.nombreUsuario,
        imagenUsuario = this.imagenUsuario,
        puntuacion = this.puntuacion,
        comentario = this.comentario,
        fecha = this.fecha,
    )
}