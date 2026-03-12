package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.dto.UsuarioEdicionDTO
import ar.edu.unsam.phm.repository.UsuarioRepository
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
    val usuarioService: UsuarioService,
    private val usuarioRepository: UsuarioRepository
){
    @GetMapping("/usuarioHeader/{usuarioId}")
    fun getUsuarioHeader(@PathVariable usuarioId : Int) = usuarioService.usuarioHeaderInfo(usuarioId)

    @PutMapping("/actualizarPlato/{id}/{idLocal}")
    fun updateUsuario(
        @PathVariable id: Int,
        @RequestBody usuario: UsuarioEdicionDTO
    ) = usuarioService.updateUsuario(usuario,id)

}