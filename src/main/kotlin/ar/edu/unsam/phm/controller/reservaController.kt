package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.PageResponse
import ar.edu.unsam.phm.dto.ReservaDTO
import ar.edu.unsam.phm.service.ReservaService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin("*")
class ReservaController(val reservaService: ReservaService) {

    @GetMapping("usuario/{id}/reservas/realizadas")
    fun getReservasRealizadas(
        @PathVariable id: Int = 1, // test
        @RequestParam page: Int,
        @RequestParam size: Int = 6,
    ): PageResponse<ReservaDTO> {
        return reservaService.getReservasRealizadas(id, page, size)
    }

    @GetMapping("usuario/{id}/reservas/recibidas")
    fun getReservasRecibidas(
        @PathVariable id: Int = 1, // test
        @RequestParam page: Int,
        @RequestParam size: Int = 6,
    ): PageResponse<ReservaDTO> {
        return reservaService.getReservasRecibidas(id, page, size)
    }

}