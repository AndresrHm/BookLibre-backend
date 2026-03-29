package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.NotFoundException
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.stereotype.Service

@Service
class UsuarioService(
    val usuarioRepositorio: UsuarioRepository,
    val reservaRepositorio: ReservaRepository,
    val libroRepositorio: LibroRepository
) {
    fun getUsuarioById(id: Int) = usuarioRepositorio.getById(id) ?: throw NotFoundException("No se encontró usuario con id $id")

    fun usuarioUpdate(usuario: Usuario): Usuario {
        if(
            usuario.nombre == "" ||
            usuario.apellido == "" ||
            usuario.ciudad == ""){
            throw BusinessException("Uno o mas campos obligatorios estan incompletos.")
        }

        when {
            usuario.esLector() -> {
                if (reservaRepositorio.findByPrestadorID(usuario.id, 1, 1, "").totalElements > 0) {
                    throw BusinessException("El usuario tiene libros prestados.")
                }
                libroRepositorio.eliminarPorIDPropietario(usuario.id)}
            usuario.esPublicador() ->
                if (reservaRepositorio.findByLectorID(usuario.id,1,1,"").totalElements > 0) {
                    throw BusinessException("El usuario tiene devoluciones pendientes.")
                }
            usuario.esLectorPublicador() -> {}
        }
        usuarioRepositorio.update(usuario)
        return usuario
    }

}
