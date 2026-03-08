package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.domain.Calificacion
import java.time.LocalDate

data class Libro(
    val id: Int,
    val titulo: String,
    val autor: String,
    val imagenUrl: String,
    val ratingPromedio: Double,
)

data class LibroPrestadoDTO(
    val reservaId: Int,
    val titulo: String,
    val autor: String,
    val imagenUrl: String,
    val calificacion: Calificacion,
    val prestadoPor: String,
    val fechaInicio: LocalDate,
    val fechaFin: LocalDate,
    val estado: EstadoReserva,
    val puedeCalificar: Boolean,
)