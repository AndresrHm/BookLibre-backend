package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.dto.UsarioPerfilDTO
import ar.edu.unsam.phm.dto.UsuarioEdicionDTO
import ar.edu.unsam.phm.dto.UsuarioHeaderDTO
import ar.edu.unsam.phm.dto.UsuarioKarmaDTO
import ar.edu.unsam.phm.mapper.UsuarioMapper
import ar.edu.unsam.phm.service.UsuarioService
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("usuario")
@CrossOrigin("*")
class UsuarioController(
    val usuarioService: UsuarioService,
    val usuarioMapper: UsuarioMapper
) {
    @GetMapping("{usuarioId}/header")
    fun getUsuarioHeader(@PathVariable usuarioId: Int) : UsuarioHeaderDTO {
        val usuario = usuarioService.usuarioById(usuarioId)
        return usuarioMapper.toUsuarioHeaderDTO(usuario)
    }
    @PutMapping
    fun updateUsuario(
        @RequestBody usuario: UsuarioEdicionDTO
    ) = usuarioService.usuarioUpdate(usuario)

    @GetMapping("{usuarioId}/update")
    fun getUsuarioUpdate(@PathVariable usuarioId: Int) = usuarioService.getUsuarioUpdate(usuarioId)

    @GetMapping("{usuarioId}/perfil")
    fun getUsuario(@PathVariable usuarioId: Int) : UsarioPerfilDTO {
        val usuario = usuarioService.usuarioById(usuarioId)
        return usuarioMapper.toUsuarioPerfilDTO(usuario)
    }

    @GetMapping("{usuarioId}/karma")
    fun getUsuarioKarma(@PathVariable usuarioId: Int): UsuarioKarmaDTO {
        val usuario = usuarioService.usuarioById(usuarioId)
        return usuarioMapper.toUsuarioKarma(usuario)
    }

    @GetMapping("{usuarioId}/tipoUsuario")
    fun getUsuarioTipo(@PathVariable usuarioId: Int) = usuarioService.getUsuarioTipo(usuarioId)
}