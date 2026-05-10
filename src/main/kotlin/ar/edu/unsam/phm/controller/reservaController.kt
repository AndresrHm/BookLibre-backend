package ar.edu.unsam.phm.controller

import ActionResponse
import ar.edu.unsam.phm.domain.PageResponse
import ar.edu.unsam.phm.domain.Reserva
import ar.edu.unsam.phm.dto.CreacionReservaDTO
import ar.edu.unsam.phm.dto.ReservaDTO
import ar.edu.unsam.phm.mapper.ReservaMapper
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.service.CalificacionService
import ar.edu.unsam.phm.service.ReservaService
import ar.edu.unsam.phm.service.UsuarioService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("reservas")
class ReservaController(
    val reservaService: ReservaService,
    val usuarioService: UsuarioService,
    val reservaMapper: ReservaMapper,
    val libroRepository: LibroRepository,
) {

    @PostMapping
    fun reservarLibro(
        authentication: Authentication,
        @RequestBody reserva: CreacionReservaDTO,
    ): ResponseEntity<ActionResponse> {
        val usuarioId = authentication.name.toLong()
        val usuarioLector = usuarioService.getUsuarioById(usuarioId)
        val usuarioPrestador = usuarioService.getUsuarioById(reserva.usuarioPrestadorId)

        reservaService.crearReserva(
            reserva.libroId,
            usuarioLector,
            usuarioPrestador,
            reserva.fechaRecogida,
            reserva.fechaDevolucion
        )

        val mensajeExito = "Reserva creada exitosamente"
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ActionResponse(mensajeExito))
    }

    @GetMapping()
    fun reservas(
        authentication: Authentication,
        @RequestParam type: ReservaType,
        @RequestParam page: Int = 0,
        @RequestParam size: Int = 4,
        @RequestParam search: String?,
    ): PageResponse<ReservaDTO> {
        val usuarioId = authentication.name.toLong()

        val result = reservaService.getReservas(usuarioId, type, page, size, search)

        val libroIds = result.content.map { it.libroId }.distinct()

        val counts = reservaService.countReservasByLibroIds(libroIds)
            .associate { it.getLibroId() to it.getTotal() }

        val librosMap = libroRepository.findAllById(libroIds).associateBy { it.id!! }

        return PageResponse(
            content = result.content.map { reserva: Reserva ->
                val libroId = reserva.libroId
                val libro = librosMap[libroId]
                    ?: throw IllegalStateException("Libro $libroId no encontrado para reserva")

                reservaMapper.toDetalleDTO(
                    reserva,
                    usuarioId,
                    counts[libroId] ?: 0L,
                    libro.calificacionPromedio(),
                    libro,
                )
            },
            page = result.page,
            size = result.size,
            totalElements = result.content.size,
            totalPages = result.totalPages,
        )
    }
}

enum class ReservaType {
    TO_ME,
    BY_ME
}
