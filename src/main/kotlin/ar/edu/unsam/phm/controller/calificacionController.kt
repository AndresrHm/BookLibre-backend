package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.Calificacion
import ar.edu.unsam.phm.domain.PageResponse
import ar.edu.unsam.phm.dto.CrearCalificacionDTO
import ar.edu.unsam.phm.dto.OrdenCalificacion
import ar.edu.unsam.phm.mapper.CrearCalificacionMapper
import ar.edu.unsam.phm.service.CalificacionService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin("*")
class CalificacionController(val calificacionService: CalificacionService) {

    @PostMapping("/libros/{libroId}/calificaciones")
    fun calificarLibro(
        @PathVariable libroId: Int,
        @RequestParam usuarioId: Int,
        @RequestBody crearCalificacionDTO: CrearCalificacionDTO
    ) {
        println(crearCalificacionDTO)
        return calificacionService.calificarLibro(usuarioId, libroId, CrearCalificacionMapper.toDomain(crearCalificacionDTO))
    }

    @GetMapping(    "libros/{libroId}/calificaciones")
    fun calificacionesLibro(
        @PathVariable libroId: Int,
        @RequestParam page: Int,
        @RequestParam size: Int = 2,
        @RequestParam orden: OrdenCalificacion = OrdenCalificacion.DATE_DESC,
    ): PageResponse<Calificacion> {
        return calificacionService.obtenerCalificaciones(libroId, orden, page, size)
    }
}