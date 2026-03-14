package ar.edu.unsam.phm.mapper

import ar.edu.unsam.phm.domain.Calificacion
import ar.edu.unsam.phm.dto.CalificacionDTO

object CalificacionMapper {
    fun toDomain(dto: CalificacionDTO): Calificacion {
        return Calificacion(
            libroID = dto.libroID,
            usuarioID = dto.usuarioID,
            nombreUsuario = dto.nombreUsuario,
            imagenUsuario = dto.imagenUsuario,
            puntuacion = dto.puntuacion,
            comentario = dto.comentario,
            fecha = dto.fecha,
        )
    }
}