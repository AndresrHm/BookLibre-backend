package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.Calificacion
import ar.edu.unsam.phm.dto.CalificacionDTO
import ar.edu.unsam.phm.dto.PageResponse
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

    @PostMapping("/libro/{id}/calificar")
    fun calificarLibro(
        @PathVariable("id") libroID: Int,
        @RequestParam usuarioID: Int, // En el futuro esto vuela
        @RequestBody calificacionDTO: CalificacionDTO
    ) {
        return calificacionService.calificarLibro(usuarioID, libroID, calificacionDTO)
    }

    @GetMapping("libro/{id}/calificaciones")
    fun calificacionesLibro(
        @PathVariable("id") libroID: Int,
        @RequestParam page: Int,
        @RequestParam size: Int = 6,
    ): PageResponse<Calificacion> {
        return calificacionService.obtenerCalificaciones(libroID, page, size)
    }
}