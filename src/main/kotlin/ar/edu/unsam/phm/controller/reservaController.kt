package ar.edu.unsam.phm.controller

import ActionResponse
import ar.edu.unsam.phm.domain.PageResponse
import ar.edu.unsam.phm.dto.CreacionReservaDTO
import ar.edu.unsam.phm.dto.LibroDetalleDTO
import ar.edu.unsam.phm.dto.RegisterRequestDTO
import ar.edu.unsam.phm.dto.ReservaDTO
import ar.edu.unsam.phm.mapper.LibroMapper
import ar.edu.unsam.phm.mapper.ReservaMapper
import ar.edu.unsam.phm.service.CalificacionService
import ar.edu.unsam.phm.service.LibroService
import ar.edu.unsam.phm.service.ReservaService
import ar.edu.unsam.phm.service.UsuarioService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("reservas")
@CrossOrigin("*")
class ReservaController(
    val reservaService: ReservaService,
    val usuarioService: UsuarioService,
    val calificacionService: CalificacionService,
    val libroService: LibroService,
    val reservaMapper: ReservaMapper,
) {

    @PostMapping
    fun reservarLibro(
        @RequestBody reserva: CreacionReservaDTO,
    ): ResponseEntity<ActionResponse> {
        // Busco los objetos de dominio para poder armar el objeto de dominio
        val libro = libroService.obtenerLibro(reserva.libroId)
        val cantidadReservas = reservaService.cantidadReservas(libro.id)
        val usuarioLector = usuarioService.getUsuarioById(reserva.usuarioLectorId)
        val usuarioPrestador = usuarioService.getUsuarioById(reserva.usuarioPrestadorId)

        val reservaObjeto = reservaService.crearReserva(
            libro,
            cantidadReservas,
            usuarioLector,
            usuarioPrestador,
            reserva.fechaRecogida,
            reserva.fechaDevolucion
        )
        reservaMapper.toCreacionReservaDTO(reservaObjeto)

        val mensajeExito = "Reserva creada exitosamente"
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ActionResponse(mensajeExito))
    }

    @GetMapping()
    fun reservas(
        @RequestParam usuarioId: Long,
        @RequestParam type: ReservaType,
        @RequestParam page: Int = 0,
        @RequestParam size: Int = 4,
        @RequestParam search: String?,
    ): PageResponse<ReservaDTO> {
        val result = reservaService.getReservas(usuarioId, type, page, size, search)

        return PageResponse(
            content = result.content.map { it ->
                reservaMapper.toReservaDetalleDTO(
                    it,
                    usuarioId,
                    reservaService.cantidadReservas(it.libro.id),
                    calificacionService.promedioCalificaciones(it.libro.id),
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