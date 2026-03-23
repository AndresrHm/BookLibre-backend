package ar.edu.unsam.phm.mapper

import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.dto.CreacionReservaDTO
import ar.edu.unsam.phm.dto.EstadoReserva
import ar.edu.unsam.phm.dto.ReservaDTO
import org.springframework.stereotype.Component

@Component
class ReservaMapper {
    fun toReservaDetalleDTO(obj: Reserva, userID: Int): ReservaDTO {
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
            libroId = obj.libro.id,
            titulo = obj.libro.titulo,
            autor = obj.libro.autor,
            imagenUrl = obj.libro.portada,
            ratingLibro = obj.libro.rating(),
            usuario = usuario,
            fechaInicio = obj.fechaRecogida,
            fechaFin = obj.fechaDevolucion,
            estado = obj.estado(),
            puedeCalificar = puedeCalificar,
            biblioKarma = obj.libro.darKarma(obj),
        )
    }

    fun toCreacionReservaDTO(reserva: Reserva): CreacionReservaDTO {
        return CreacionReservaDTO(
            libroId = reserva.libro.id,
            usuarioPrestadorId = reserva.usuarioPrestador.id,
            usuarioLectorId = reserva.usuarioLector.id,
            fechaRecogida = reserva.fechaRecogida,
            fechaDevolucion = reserva.fechaDevolucion
        )
    }
}