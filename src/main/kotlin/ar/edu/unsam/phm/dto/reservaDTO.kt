package ar.edu.unsam.phm.dto

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
)

enum class EstadoReserva {
    ACTIVO,
    PROXIMO_A_VENCER,
    DEVUELTO,
}