package ar.edu.unsam.phm.service

import ar.edu.unsam.phm.BusinessException
import ar.edu.unsam.phm.NotFoundException
import ar.edu.unsam.phm.controller.ReservaType
import ar.edu.unsam.phm.domain.Usuario
import ar.edu.unsam.phm.errors.ErrorStrings
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
        if (
            camposObligatoriosVacios(usuario)
        ) {
            throw BusinessException(ErrorStrings.CAMPOS_OBLIGATORIOS_INCOMPLETOS)
        }

        when {
            usuario.esLector() -> {
                if (reservaRepositorio.findReservasByType(usuario.id, ReservaType.TO_ME, 1, 1, "").totalElements > 0) {
                    throw BusinessException(ErrorStrings.USUARIO_TIENE_LIBROS_PRESTADOS)
                }
                libroRepositorio.eliminarPorIDPropietario(usuario.id)}
            usuario.esPublicador() ->
                if (reservaRepositorio.findReservasByType(usuario.id, ReservaType.BY_ME, 1, 1, "").totalElements > 0) {
                    throw BusinessException(ErrorStrings.USUARIO_TIENE_DEVOLUCIONES_PENDIENTES)
                }
            usuario.esLectorPublicador() -> {}
        }
        usuarioRepositorio.update(usuario)
        return usuario
    }

    private fun camposObligatoriosVacios(usuario: Usuario): Boolean =
        usuario.nombre == "" ||
        usuario.apellido == "" ||
        usuario.ciudad == ""
}
