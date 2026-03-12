package ar.edu.unsam.phm.mapper

import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.dto.EstadoReserva
import ar.edu.unsam.phm.dto.ReservaDTO
import java.time.LocalDate
import java.time.temporal.ChronoUnit

fun Reserva.toReservaDTO(userID: Int): ReservaDTO {
    val estado = when {
        LocalDate.now().isAfter(fechaDevolucion) -> EstadoReserva.DEVUELTO
        ChronoUnit.DAYS.between(LocalDate.now(), fechaDevolucion) <= 3 -> EstadoReserva.PROXIMO_A_VENCER
        else -> EstadoReserva.ACTIVO
    }

    val puedeCalificar =
        usuarioLector.id == userID &&
        estado == EstadoReserva.DEVUELTO

    val usuario =
        if (usuarioLector.id == userID) {
            usuarioPrestador.nombre
        } else {
            usuarioLector.nombre
        }

    return ReservaDTO(
        reservaId = id,
        titulo = libro.titulo,
        autor = libro.autor,
        imagenUrl = libro.portada,
        ratingLibro = libro.rating(),
        usuario = usuario,
        fechaInicio = fechaRecogida,
        fechaFin = fechaDevolucion,
        estado = estado,
        puedeCalificar = puedeCalificar,
    )
}