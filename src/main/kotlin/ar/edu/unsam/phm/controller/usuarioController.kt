package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.dto.UsuarioEdicionDTO
import ar.edu.unsam.phm.service.UsuarioService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin("*")
class UsuarioController (
    val usuarioService: UsuarioService
){
    @GetMapping("/usuarioHeader/{usuarioId}")
    fun getUsuarioHeader(@PathVariable usuarioId : Int) = usuarioService.usuarioHeaderInfo(usuarioId)

    @PutMapping("/usuarioUpdate")
    fun updateUsuario(
        @RequestBody usuario: UsuarioEdicionDTO
    ) = usuarioService.usuarioUpdate(usuario)

    @GetMapping("/getUsuarioUpdate/{usuarioId}")
    fun getUsuarioUpdate(@PathVariable usuarioId : Int) = usuarioService.getUsuarioUpdate(usuarioId)
    
    @GetMapping("/usuarioPerfil/{usuarioId}")
    fun getUsuario(@PathVariable usuarioId : Int) = usuarioService.usuarioPerfil(usuarioId)

}