package ar.edu.unsam.phm.mapper

import ar.edu.unsam.phm.domain.Calificacion
import ar.edu.unsam.phm.dto.CalificacionDTO

object CalificacionMapper {
    fun toDTO(obj: Calificacion): CalificacionDTO {
        return CalificacionDTO(
            nombreUsuario = obj.reserva.usuarioLector.nombre + " " + obj.reserva.usuarioLector.apellido,
            imagenUsuario = obj.reserva.usuarioLector.urlImagen,
            puntuacion = obj.puntuacion,
            comentario = obj.comentario,
            fecha = obj.fecha,
        )
    }

    fun toDTO(calificaciones: List<Calificacion>): List<CalificacionDTO> {
        return calificaciones.map { toDTO(it) }
    }
}