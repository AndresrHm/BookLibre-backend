package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.domain.PageResponse
import ar.edu.unsam.phm.dto.ReservaDTO
import ar.edu.unsam.phm.mapper.ReservaMapper
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
        @PathVariable id: Int,
        @RequestParam page: Int = 0,
        @RequestParam size: Int = 6,
    ): PageResponse<ReservaDTO> {
        val result = reservaService.getReservasRealizadas(id, page, size)

        return PageResponse(
            content = result.content.map { it -> ReservaMapper.toDTO(it, id) },
            page = result.page,
            size = result.size,
            totalElements = result.content.size,
            totalPages = result.totalPages,
        )
    }

    @GetMapping("usuario/{id}/reservas/recibidas")
    fun getReservasRecibidas(
        @PathVariable id: Int,
        @RequestParam page: Int = 0,
        @RequestParam size: Int = 6,
    ): PageResponse<ReservaDTO> {
        val result = reservaService.getReservasRecibidas(id, page, size)

        return PageResponse(
            content = result.content.map { it -> ReservaMapper.toDTO(it, id) },
            page = result.page,
            size = result.size,
            totalElements = result.content.size,
            totalPages = result.totalPages,
        )
    }

}