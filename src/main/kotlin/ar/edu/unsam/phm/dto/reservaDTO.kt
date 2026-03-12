package ar.edu.unsam.phm.dto

import java.time.LocalDate

data class ReservaDTO(
    val id: Int,
    val libroId: Int,
    val prestadorId: Int,
    val lectorId: Int,
    val fechaInicio: LocalDate,
    val fechaFin: LocalDate,
    val fechaDevolucion: LocalDate,
    val calificacion: Int,
)

enum class TipoReserva {
    TO_ME,
    BY_ME,
}

enum class EstadoReserva {
    ACTIVO,
    PROXIMO_A_VENCER,
    DEVUELTO,
}