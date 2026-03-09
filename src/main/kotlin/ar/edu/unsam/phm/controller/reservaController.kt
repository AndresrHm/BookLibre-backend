package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.dto.TipoReserva
import ar.edu.unsam.phm.service.ReservaService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin("*")
class ReservaController(val reservaService: ReservaService) {
    @GetMapping
    fun getReservas(
        @RequestParam tipo: TipoReserva,
        @RequestParam page: Int,
        @RequestParam size: Int,
    ): Int {//PageResponse<LibroPrestadoDTO> {
        val userID = 1 // mock
        return 1
    }
}

