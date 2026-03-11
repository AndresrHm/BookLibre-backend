package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.service.UsuarioService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin("*")
class UsuarioController (
    val usuarioService: UsuarioService
){
    @GetMapping("/usuarioHeader/{usuarioId}")
    fun getUsuarioHeader(@PathVariable usuarioId : Int) = usuarioService.usuarioHeaderInfo(usuarioId)


}