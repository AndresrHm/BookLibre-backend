package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.dto.UsarioPerfilDTO
import ar.edu.unsam.phm.dto.UsuarioEdicionDTO
import ar.edu.unsam.phm.dto.UsuarioHeaderDTO
import ar.edu.unsam.phm.errors.NotFoundException
import ar.edu.unsam.phm.mapper.UsuarioMapper
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.stereotype.Service

@Service
class UsuarioService (
    val usuarioRepositorio: UsuarioRepository
){
    fun usuarioById(id: Int) = usuarioRepositorio.getById(id) ?: throw NotFoundException("No se encontró usuario de id <$id>")

    fun usuarioHeaderInfo(id: Int): UsuarioHeaderDTO {
        val usuario = usuarioById(id)
        return UsuarioMapper.toUsuarioHeaderDTO(usuario)
    }

    fun updateUsuario(datosUsuario: UsuarioEdicionDTO, idUsuario: Int): Usuario {
        val usuario = usuarioById(idUsuario)
        usuario.update(datosUsuario)
        usuarioRepositorio.update(usuario)
        return usuario
    }

    fun usuarioPerfil(id: Int): UsarioPerfilDTO {
        val usuario = usuarioById(id)
        return UsuarioMapper.toUsuarioPerfilDTO(usuario)
    }
}

