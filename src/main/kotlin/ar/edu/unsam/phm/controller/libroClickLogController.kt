package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.dto.LibroClickLogDTO
import ar.edu.unsam.phm.dto.ResumenClickLibroPorUsuarioDTO
import ar.edu.unsam.phm.mapper.ClickMapper
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("libro-click-logs")
class LibroClickLogController(
    private val clickMapper: ClickMapper,
) {
    //No esta implementado en el front
    @GetMapping("{libroId}")
    fun libroClickPorLibro(
        @PathVariable libroId: String,
    ): List<LibroClickLogDTO> {
        return clickMapper.obtenerClicksPorLibro(libroId)
    }

    @GetMapping("usuario/resumen")
    fun resumenClickLibroPorUsuario(
        authentication: Authentication,
    ): ResumenClickLibroPorUsuarioDTO {
        val usuarioId = authentication.name.toLong()
        return clickMapper.obtenerResumenClickLibroPorUsuario(usuarioId)
    }
}
