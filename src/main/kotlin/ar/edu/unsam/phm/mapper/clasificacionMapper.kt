package ar.edu.unsam.phm.mapper

import ar.edu.unsam.phm.domain.Calificacion
import ar.edu.unsam.phm.dto.CrearCalificacionDTO
import java.time.LocalDate

object CrearCalificacionMapper {
    fun toDomain(dto: CrearCalificacionDTO): Calificacion {
        return Calificacion(
            libroID = dto.libroId,
            usuarioID = dto.usuarioId,
            nombreUsuario = dto.nombreUsuario,
            imagenUsuario = dto.imagenUsuario,
            puntuacion = dto.puntuacion,
            comentario = dto.comentario,
            fecha = LocalDate.now(),
        )
    }
}