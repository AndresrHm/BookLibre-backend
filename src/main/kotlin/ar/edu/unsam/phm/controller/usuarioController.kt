package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.dto.SealedGetUsuarioDTO
import ar.edu.unsam.phm.dto.TipoDTOUsuario
import ar.edu.unsam.phm.dto.UsuarioEdicionDTO
import ar.edu.unsam.phm.mapper.UsuarioMapper
import ar.edu.unsam.phm.service.UsuarioService
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("usuario")
class UsuarioController(
    val usuarioService: UsuarioService,
    val usuarioMapper: UsuarioMapper
) {

    @PutMapping
    fun updateUsuario(
        @Valid @RequestBody usuario: UsuarioEdicionDTO
    ): ResponseEntity<Void> {
        val usuarioExistente = usuarioService.getUsuarioById(usuario.id!!)
        val usuarioNuevo = usuarioMapper.fromEdicionDTO(usuario, usuarioExistente)
        usuarioService.usuarioUpdate(usuarioNuevo)
        return ResponseEntity.ok().build()
    }

    @GetMapping("{tipoDTOUsuario}")
    fun getUsuarioType(
        authentication: Authentication,
        @PathVariable tipoDTOUsuario: String
    ): ResponseEntity<SealedGetUsuarioDTO> {
        val usuarioId = authentication.name.toLong()

        val usuario = usuarioService.getUsuarioById(usuarioId)
        val tipoDTOEnum = usuarioMapper.toTipoDTO(tipoDTOUsuario)

        val handlers: Map<TipoDTOUsuario, () -> SealedGetUsuarioDTO> = mapOf(
            TipoDTOUsuario.HEADER      to { SealedGetUsuarioDTO.Header(usuarioMapper.toHeaderDTO(usuario)) },
            TipoDTOUsuario.UPDATE      to { SealedGetUsuarioDTO.Update(usuarioMapper.toEdicionDTO(usuario)) },
            TipoDTOUsuario.PERFIL      to { SealedGetUsuarioDTO.Perfil(usuarioMapper.toPerfilDTO(usuario)) },
            TipoDTOUsuario.KARMA       to { SealedGetUsuarioDTO.Karma(usuarioMapper.toKarmaDTO(usuario)) },
            TipoDTOUsuario.TIPOUSUARIO to { SealedGetUsuarioDTO.TipoPerfil(usuario.roles) },
        )

        val response = handlers[tipoDTOEnum]?.invoke()
            ?: throw IllegalArgumentException("Tipo DTO invalido: $tipoDTOEnum")

        return ResponseEntity.ok(response)
    }

}
