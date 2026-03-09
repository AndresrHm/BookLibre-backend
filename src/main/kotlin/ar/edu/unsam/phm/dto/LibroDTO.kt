package ar.edu.unsam.phm.dto

import java.time.LocalDate
import ar.edu.unsam.phm.domain.Genero
import ar.edu.unsam.phm.domain.Usuario

data class LibroDTO(
    val id: Long,
    val titulo: String,
    val autor: String,
    val imagenUrl: String,
    val ratingPromedio: Double
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
    val puedeCalificar: Boolean
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

data class PageResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int
)

