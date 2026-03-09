package ar.edu.unsam.phm.dto

import ar.edu.unsam.phm.domain.Calificacion
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Usuario
import java.time.LocalDate

data class Libro(
    val id: Int,
    val titulo: String,
    val autor: String,
    val imagenUrl: String,
    val ratingPromedio: Double,
)

data class FiltrosDeLibrosDTO(
    val genero: Genero?,
    val paginasMin: Int?,
    val paginasMax: Int?,
    val fechaDesde: LocalDate?,
    val fechaHasta: LocalDate?,
    val isbn13: String?,
    val usuarioPrestador: Usuario?
)

data class LibroPrestadoDTO(
    val reservaId: Int,
    val titulo: String,
    val autor: String,
    val imagenUrl: String,
    val calificacion: Calificacion,
    val usuario: String,
    val fechaInicio: LocalDate,
    val fechaFin: LocalDate,
    val estado: EstadoReserva,
    val puedeCalificar: Boolean,
)