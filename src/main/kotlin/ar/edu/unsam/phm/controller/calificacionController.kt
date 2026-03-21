package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.Calificacion
import ar.edu.unsam.phm.domain.PageResponse
import ar.edu.unsam.phm.dto.CalificacionDTO
import ar.edu.unsam.phm.dto.CrearCalificacionDTO
import ar.edu.unsam.phm.dto.OrdenCalificacion
import ar.edu.unsam.phm.mapper.CalificacionMapper
import ar.edu.unsam.phm.service.CalificacionService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("calificaciones")
@CrossOrigin("*")
class CalificacionController(val calificacionService: CalificacionService) {

    @PostMapping
    fun calificarLibro(
        @RequestBody calificacion: CrearCalificacionDTO
    ) {
        return calificacionService.calificarLibro(calificacion)
    }

    @GetMapping("{libroId}")
    fun calificacionesLibro(
        @PathVariable libroId: Int,
        @RequestParam page: Int,
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