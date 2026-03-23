package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.requireBusiness
import java.time.LocalDate

data class ReservaDTO(
    val reservaId: Int,
    val libroId: Int,
    val titulo: String,
    val autor: String,
    val imagenUrl: String,
    val ratingLibro: Double,
    val usuario: String,
    val fechaInicio: LocalDate,
    val fechaFin: LocalDate,
    val estado: EstadoReserva,
    val puedeCalificar: Boolean,
    val biblioKarma: Int,
) {
}

data class CreacionReservaDTO(
    val libroId: Int,
    val usuarioPrestadorId: Int,
    val usuarioLectorId: Int,
    val fechaRecogida: LocalDate,
    val fechaDevolucion: LocalDate,
) {
    init {
        requireBusiness(fechaDevolucion.isAfter(fechaRecogida), "fechaDevolucion debe ser posterior a fechaRecogida")
        requireBusiness(usuarioLectorId != usuarioPrestadorId, "El usuario prestador y el lector deben ser distintos")
        // TODO: Realizar estas dos validaciones en el service.
//        requireBusiness(usuarioPrestador.id == libro.idPropietario, "El libro no pertenece al usuario prestador")
//        requireBusiness(usuarioLector.id != libro.idPropietario, "Un usuario no puede reservar su propio libro")
    }
}

enum class EstadoReserva {
    ACTIVO,
    PROXIMO_A_VENCER,
    DEVUELTO,
}