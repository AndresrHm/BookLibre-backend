package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.dto.LibroPrestadoDTO
import ar.edu.unsam.phm.dto.PageResponse
import ar.edu.unsam.phm.dto.TipoReserva
import ar.edu.unsam.phm.service.ReservaService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin("*")
class ReservaController(val reservaService: ReservaService) {
    @GetMapping("/reservas")
    fun getReservas(
        @RequestParam userID: Int = 1,
        @RequestParam tipoReserva: TipoReserva,
        @RequestParam page: Int,
        @RequestParam size: Int = 6,
    ): PageResponse<LibroPrestadoDTO> {
        return reservaService.getReservas(userID, tipoReserva, page, size)
    }
}