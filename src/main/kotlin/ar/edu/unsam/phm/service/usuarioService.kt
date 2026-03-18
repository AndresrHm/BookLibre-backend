package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.NotFoundException
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.UsarioPerfilDTO
import ar.edu.unsam.phm.dto.UsuarioEdicionDTO
import ar.edu.unsam.phm.dto.UsuarioHeaderDTO
import ar.edu.unsam.phm.mapper.UsuarioMapper
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.stereotype.Service

@Service
class UsuarioService(
    val usuarioRepositorio: UsuarioRepository,
    val usuarioMapper: UsuarioMapper
) {
    fun usuarioById(id: Int) = usuarioRepositorio.getById(id) ?: throw NotFoundException("No se encontró usuario de id <$id>")

    fun usuarioHeaderInfo(id: Int): UsuarioHeaderDTO {
        val usuario = usuarioById(id)
        return usuarioMapper.toUsuarioHeaderDTO(usuario)
    }

    fun getUsuarioUpdate(id: Int): UsuarioEdicionDTO {
        val usuario = usuarioById(id)
        return usuarioMapper.toUsuarioEdicionDTO(usuario)
    }

    fun usuarioUpdate(datosUsuario: UsuarioEdicionDTO): Usuario {
        val usuario = usuarioById(datosUsuario.id)
        usuario.update(datosUsuario)
        usuarioRepositorio.update(usuario)
        return usuario
    }

    fun usuarioPerfil(id: Int): UsarioPerfilDTO {
        val usuario = usuarioById(id)
        return usuarioMapper.toUsuarioPerfilDTO(usuario)
    }
}
