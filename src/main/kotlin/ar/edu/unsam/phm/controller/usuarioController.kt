package ar.edu.unsam.phm.controller

import ar.edu.unsam.phm.dto.SealedGetUsuarioDTO
import ar.edu.unsam.phm.dto.TipoDTOUsuario
import ar.edu.unsam.phm.dto.UsuarioEdicionDTO
import ar.edu.unsam.phm.mapper.UsuarioMapper
import ar.edu.unsam.phm.service.UsuarioService
import org.springframework.http.ResponseEntity
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

    @PutMapping
    fun updateUsuario(
        @RequestBody usuario: UsuarioEdicionDTO
    ): ResponseEntity<Void> {
        val usuarioExistente = usuarioService.getUsuarioById(usuario.id!!)
        val usuarioNuevo = usuarioMapper.usuarioEdicionDTOtoUsuario(usuario, usuarioExistente)
        usuarioService.usuarioUpdate(usuarioNuevo)
        return ResponseEntity.ok().build()
    }

    @GetMapping("{usuarioId}/{tipoDTOUsuario}")
    fun getUsuarioType(@PathVariable usuarioId: Long, @PathVariable tipoDTOUsuario: String): ResponseEntity<SealedGetUsuarioDTO> {
        val usuario = usuarioService.getUsuarioById(usuarioId)
        val tipoDTOEnum = usuarioMapper.toTipoDTOUsuario(tipoDTOUsuario)

        val response = when (tipoDTOEnum) {
            TipoDTOUsuario.HEADER -> {
                val dto = usuarioMapper.toUsuarioHeaderDTO(usuario)
                SealedGetUsuarioDTO.Header(dto)
            }

            TipoDTOUsuario.UPDATE -> {
                val dto = usuarioMapper.toUsuarioEdicionDTO(usuario)
                SealedGetUsuarioDTO.Update(dto)
            }

            TipoDTOUsuario.PERFIL -> {
                val dto = usuarioMapper.toUsuarioPerfilDTO(usuario)
                SealedGetUsuarioDTO.Perfil(dto)
            }

            TipoDTOUsuario.KARMA -> {
                val dto = usuarioMapper.toUsuarioKarmaDTO(usuario)
                SealedGetUsuarioDTO.Karma(dto)
            }

            TipoDTOUsuario.TIPOUSUARIO -> {
                val tipo = usuario.tipoUsuario
                SealedGetUsuarioDTO.TipoPerfil(tipo)
            }
        }
        
        return ResponseEntity.ok(response)
    }

}
