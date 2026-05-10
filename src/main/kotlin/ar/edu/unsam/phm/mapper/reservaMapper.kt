package ar.edu.unsam.phm.mapper

import ar.edu.unsam.phm.domain.EstadoReserva
import ar.edu.unsam.phm.domain.Libro
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.dto.ReservaDTO
import org.springframework.stereotype.Component

@Component
class ReservaMapper {
    fun toDetalleDTO(
        obj: Reserva,
        userID: Long,
        cantidadReservas: Long,
        calificacionLibro: Double,
        libro: Libro,
    ): ReservaDTO {
        val puedeCalificar =
            obj.usuarioLector.id == userID &&
                    obj.estado() == EstadoReserva.DEVUELTO

        val usuario =
            if (obj.usuarioLector.id == userID) {
                obj.usuarioPrestador.nombre
            } else {
                obj.usuarioLector.nombre
            }

        return ReservaDTO(
            reservaId = obj.id,
            libroId = libro.id,
            titulo = libro.titulo,
            autor = libro.autor,
            imagenUrl = libro.portada,
            calificacionLibro = calificacionLibro,
            usuario = usuario,
            fechaInicio = obj.fechaRecogida,
            fechaFin = obj.fechaDevolucion,
            estado = obj.estado(),
            puedeCalificar = puedeCalificar,
            biblioKarma = libro.darKarma(obj, cantidadReservas.toInt()),
        )
    }
}
