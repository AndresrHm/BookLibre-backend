package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.errors.ErrorStrings
import ar.edu.unsam.phm.errors.requireBusiness
import java.time.LocalDate

data class ReservaDTO(
    val reservaId: Long?,
    val libroId: Long?,
    val titulo: String,
    val autor: String,
    val imagenUrl: String,
    val calificacionLibro: Double,
    val usuario: String,
    val fechaInicio: LocalDate,
    val fechaFin: LocalDate,
    val estado: EstadoReserva,
    val puedeCalificar: Boolean,
    val biblioKarma: Int,
) {
}

data class CreacionReservaDTO(
    val libroId: Long,
    val usuarioPrestadorId: Long,
    val fechaRecogida: LocalDate,
    val fechaDevolucion: LocalDate,
) {
    init {
        requireBusiness(fechaDevolucion.isAfter(fechaRecogida), ErrorStrings.FECHA_DEVOLUCION_POSTERIOR_RECOGIDA)
    }
}

enum class EstadoReserva {
    ACTIVO,
    PROXIMO_A_VENCER,
    DEVUELTO,
}