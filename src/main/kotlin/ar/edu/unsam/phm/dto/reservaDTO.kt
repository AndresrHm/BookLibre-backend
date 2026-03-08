package ar.edu.unsam.phm.dto

import java.time.LocalDate

data class Reserva(
    val id: Long,
    val libroId: Long,
    val prestadorId: Long,
    val lectorId: Long,
    val fechaInicio: LocalDate,
    val fechaFin: LocalDate,
    val fechaDevolucion: LocalDate,
    val rating: Double,
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

fun Reserva.toDTO() {

}