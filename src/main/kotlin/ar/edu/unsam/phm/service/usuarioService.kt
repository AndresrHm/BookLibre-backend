package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.dto.UsuarioHeaderDTO
import ar.edu.unsam.phm.errors.NotFoundException
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.stereotype.Service

@Service
class UsuarioService (
    val usuarioRepositorio: UsuarioRepository
){
    fun usuarioById(id: Int) = usuarioRepositorio.getById(id) ?: throw NotFoundException("No se encontró usuario de id <$id>")

    fun usuarioHeaderInfo(id: Int): UsuarioHeaderDTO {
        val usuario = usuarioById(id)
        return UsuarioHeaderDTO(
            id = usuario.id,
            urlImagen = usuario.urlImagen,
            karma = usuario.biblioKarma
            )
    }
}