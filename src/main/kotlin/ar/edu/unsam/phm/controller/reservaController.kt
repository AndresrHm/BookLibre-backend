package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.PageResponse
import ar.edu.unsam.phm.dto.CreacionReservaDTO
import ar.edu.unsam.phm.dto.ReservaDTO
import ar.edu.unsam.phm.mapper.ReservaMapper
import ar.edu.unsam.phm.service.LibroService
import ar.edu.unsam.phm.service.ReservaService
import ar.edu.unsam.phm.service.UsuarioService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
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
    val libroService: LibroService,
    val reservaMapper: ReservaMapper,
) {

    @PostMapping
    fun reservarLibro(
        @RequestBody reserva: CreacionReservaDTO,
    ): CreacionReservaDTO {
        // Busco los objetos de dominio para poder armar el objeto de dominio
        val libro = libroService.obtenerLibroDetalle(reserva.libroId)
        val usuarioLector = usuarioService.getUsuarioById(reserva.usuarioLectorId)
        val usuarioPrestador = usuarioService.getUsuarioById(reserva.usuarioPrestadorId)
        val reservaObjeto = reservaService.crearReserva(
            libro,
            usuarioLector,
            usuarioPrestador,
            reserva.fechaRecogida,
            reserva.fechaDevolucion
        )
        return reservaMapper.toCreacionReservaDTO(reservaObjeto)
    }

    @GetMapping("/{usuarioId}/realizadas")
    fun getReservasRealizadas(
        @PathVariable usuarioId: Int,
        @RequestParam page: Int = 0,
        @RequestParam size: Int = 6,
        @RequestParam string: String?,
    ): PageResponse<ReservaDTO> {
        val result = reservaService.getReservasRealizadas(usuarioId, page, size, string)

        return PageResponse(
            content = result.content.map { it -> reservaMapper.toReservaDetalleDTO(it, usuarioId) },
            page = result.page,
            size = result.size,
            totalElements = result.content.size,
            totalPages = result.totalPages,
        )
    }

    @GetMapping("{usuarioId}/recibidas")
    fun getReservasRecibidas(
        @PathVariable usuarioId: Int,
        @RequestParam page: Int = 0,
        @RequestParam size: Int = 6,
        @RequestParam string: String?,
    ): PageResponse<ReservaDTO> {
        val result = reservaService.getReservasRecibidas(usuarioId, page, size, string)

        return PageResponse(
            content = result.content.map { it -> reservaMapper.toReservaDetalleDTO(it, usuarioId) },
            page = result.page,
            size = result.size,
            totalElements = result.content.size,
            totalPages = result.totalPages,
        )
    }

}