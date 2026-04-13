package ar.edu.unsam.phm.mapper

import ar.edu.unsam.phm.domain.Calificacion
import ar.edu.unsam.phm.dto.CalificacionDTO

object CalificacionMapper {
    fun toDTO(obj: Calificacion): CalificacionDTO {
        return CalificacionDTO(
            nombreUsuario = obj.usuario.nombre + " " + obj.usuario.apellido,
            imagenUsuario = obj.usuario.urlImagen,
            puntuacion = obj.puntuacion,
            comentario = obj.comentario,
            fecha = obj.fecha,
        )
    }

    fun toDTO(calificaciones: List<Calificacion>): List<CalificacionDTO> {
        return calificaciones.map { toDTO(it) }
    }
}