package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.dto.UsuarioEdicionDTO
import ar.edu.unsam.phm.dto.UsuarioKarmaDTO
import ar.edu.unsam.phm.mapper.UsuarioMapper
import ar.edu.unsam.phm.service.UsuarioService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
@CrossOrigin("*")
class UsuarioController(
    val usuarioService: UsuarioService,
    val usuarioMapper: UsuarioMapper
) {
    @GetMapping("/usuarioHeader/{usuarioId}")
    fun getUsuarioHeader(@PathVariable usuarioId: Int) = usuarioService.usuarioHeaderInfo(usuarioId)

    @PutMapping("/updateUsuario/{usuarioId}")
    fun updateUsuario(
        @PathVariable usuarioId: Int,
        @RequestBody usuario: UsuarioEdicionDTO
    ) = usuarioService.updateUsuario(usuario, usuarioId)

    @GetMapping("/usuarioPerfil/{usuarioId}")
    fun getUsuario(@PathVariable usuarioId: Int) = usuarioService.usuarioPerfil(usuarioId)

    @GetMapping("/usuario/{usuarioId}")
    fun getUsuarioKarma(@PathVariable usuarioId: Int): UsuarioKarmaDTO {
        val usuario = usuarioService.usuarioById(usuarioId)
        return usuarioMapper.toUsuarioKarma(usuario)
    }
}