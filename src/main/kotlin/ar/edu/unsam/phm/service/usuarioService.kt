package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.NotFoundException
import ar.edu.unsam.phm.controller.ReservaType
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.errors.ErrorStrings
import ar.edu.unsam.phm.repository.LibroRepository
import ar.edu.unsam.phm.repository.ReservaRepository
import ar.edu.unsam.phm.repository.UsuarioRepository
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UsuarioService(
    val usuarioRepositorio: UsuarioRepository,
    val reservaRepositorio: ReservaRepository,
    val libroRepositorio: LibroRepository
) {

    fun getUsuarioById(id: Long) =
        usuarioRepositorio.findByIdOrNull(id) ?: throw NotFoundException("No se encontró usuario con id $id")


    fun getUsuarioByNombre(nombre: String) =
        usuarioRepositorio.findByNombre(nombre) ?: throw NotFoundException("No se encontró usuario con nombre $nombre")

    @Transactional(readOnly = false, rollbackFor = [Exception::class])
    fun usuarioUpdate(usuario: Usuario): Usuario {
        if (camposObligatoriosVacios(usuario)) {
            throw BusinessException(ErrorStrings.CAMPOS_OBLIGATORIOS_INCOMPLETOS)
        }

        when {
            // Para eliminar usuario, no aca
            // Si el usuario tiene libros prestados, puede ser eliminado, pero la baja es logica...
            usuario.esLector() -> {
                if (
                    reservaRepositorio.countByUsuarioLectorId(usuario.id!!) > 0
                ) {
                    throw BusinessException(ErrorStrings.USUARIO_TIENE_LIBROS_PRESTADOS)
                }
                libroRepositorio.eliminarPorIDPropietario(usuario.id!!)
            }

            usuario.esPublicador() ->
                if (
                    reservaRepositorio.countByUsuarioPrestadorId(usuario.id!!) > 0
                ) {
                    throw BusinessException(ErrorStrings.USUARIO_TIENE_DEVOLUCIONES_PENDIENTES)
                }

            usuario.esLectorPublicador() -> {}
        }
        usuarioRepositorio.save(usuario)
        return usuario
    }

    private fun camposObligatoriosVacios(usuario: Usuario): Boolean =
        usuario.nombre == "" ||
                usuario.apellido == "" ||
                usuario.ciudad == ""


    fun obtenerCantidadDeLibrosPrestados(usuarioId: Long): Int {
        return reservaRepositorio.findAll()
            .filter { it.usuarioPrestador.id == usuarioId }
            .toList()
            .size
    }

    fun obtenerCantidadDeLibrosLeidos(usuarioId: Long): Int {
        return reservaRepositorio.findAll()
            .filter { it.usuarioLector.id == usuarioId }
            .toList()
            .size
    }

    fun getNombreDeUsuario(id: Long): String? {
        return usuarioRepositorio.findByIdOrNull(id)?.nombre
    }
}

