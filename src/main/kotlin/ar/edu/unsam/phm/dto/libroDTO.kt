package ar.edu.unsam.phm.dto

import java.time.LocalDate

data class Libro(
    val id: Long,
    val titulo: String,
    val autor: String,
    val imagenUrl: String,
    val ratingPromedio: Double,
)

data class LibroPrestadoDTO(
    val reservaId: Long,
    val titulo: String,
    val autor: String,
    val imagenUrl: String,
    val ratingLibro: Double,
    val prestadoPor: String,
    val fechaInicio: LocalDate,
    val fechaFin: LocalDate,
    val estado: EstadoReserva,
    val puedeCalificar: Boolean,
)