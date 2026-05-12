package ar.edu.unsam.phm.controller

import ActionResponse
import ar.edu.unsam.phm.domain.PageResponse
import ar.edu.unsam.phm.dto.CalificacionDTO
import ar.edu.unsam.phm.dto.CrearCalificacionDTO
import ar.edu.unsam.phm.dto.OrdenCalificacion
import ar.edu.unsam.phm.mapper.CalificacionMapper
import ar.edu.unsam.phm.service.CalificacionService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("calificaciones")
class CalificacionController(val calificacionService: CalificacionService) {

    @PostMapping
    fun calificarLibro(
        authentication: Authentication,
        @RequestBody calificacion: CrearCalificacionDTO
    ): ResponseEntity<ActionResponse> {
        val usuarioId = authentication.name.toLong()
        calificacionService.calificarLibro(usuarioId, calificacion)
        val mensajeExito = "Calificación registrada exitosamente."
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ActionResponse(mensajeExito))
    }

    @GetMapping("{libroId}")
    fun calificacionesLibro(
        @PathVariable libroId: String,
        @RequestParam page: Int = 0,
        @RequestParam size: Int = 2,
        @RequestParam orden: OrdenCalificacion = OrdenCalificacion.DATE_DESC,
    ): PageResponse<CalificacionDTO> {
        val result = calificacionService.obtenerCalificaciones(libroId, orden, page, size)

        return PageResponse(
            content = result.content.map { it -> CalificacionMapper.toDTO(it) },
            page = result.page,
            size = result.size,
            totalElements = result.content.size,
            totalPages = result.totalPages
        )
    }
}