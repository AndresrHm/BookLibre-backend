package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.service.ReservaService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.time.LocalDate

@RestController
@CrossOrigin("*")
class ReservaController(val reservaService: ReservaService) {
    @GetMapping
    fun getReservas(
        @RequestParam userID: Int = 1,
        @RequestParam tipo: TipoReserva,
        @RequestParam page: Int,
        @RequestParam size: Int = 6,
    ): PageResponse<LibroPrestadoDTO> {
        return reservaService.getReservas(userID, tipo, page, size)
    }
}


// enums / data classes
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

data class PageResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    val totalElements: Long,
    val totalPages: Int,
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